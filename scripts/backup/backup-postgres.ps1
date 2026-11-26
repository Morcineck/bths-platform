<#
Backup cifrado do PostgreSQL do BTHS (issues #78 e #172).

Le a conexao de variaveis de ambiente:
  BTHS_BACKUP_HOST, BTHS_BACKUP_PORT, BTHS_BACKUP_DB, BTHS_BACKUP_USER,
  PGPASSWORD (senha do usuario de backup), BTHS_BACKUP_AGE_RECIPIENT (chave publica age)
  BTHS_BACKUP_SSL_MODE (opcional, padrao require)

Gera: dump -> cifra com age -> grava no destino local e no Drive.
Mantem os ultimos $Retencao diarios e os 3 mensais mais recentes.
#>
param(
    [string]$Ambiente = 'staging',
    [string]$DestinoLocal = 'C:\Users\robso\Backups\bths',
    [string]$DestinoDrive = 'G:\Meu Drive\Backups\bths',
    [int]$Retencao = 14,
    # Caminho de um arquivo NOME=valor (ex.: .env.backup.producao); usado pela tarefa agendada
    [string]$ArquivoEnv = ''
)

$ErrorActionPreference = 'Stop'

# Carrega a conexao do arquivo quando informado: a tarefa agendada nao tem as variaveis da janela do PowerShell
if ($ArquivoEnv) {
    if (-not (Test-Path $ArquivoEnv)) { throw "Arquivo de ambiente nao encontrado: $ArquivoEnv" }
    # Ignora linhas vazias e comentarios; o -split com limite 2 corta so no primeiro '=' (valores podem ter '=')
    Get-Content $ArquivoEnv | Where-Object { $_ -and -not $_.StartsWith('#') } | ForEach-Object {
        $k, $v = $_ -split '=', 2
        # Trim tira espacos e quebras de linha que sobram no fim (o Bloco de Notas pode deixar)
        Set-Item -Path "env:$($k.Trim())" -Value $v.Trim()
    }
}

foreach ($v in 'BTHS_BACKUP_HOST', 'BTHS_BACKUP_PORT', 'BTHS_BACKUP_DB', 'BTHS_BACKUP_USER', 'PGPASSWORD', 'BTHS_BACKUP_AGE_RECIPIENT') {
    if (-not (Test-Path "env:$v")) { throw "Variavel de ambiente ausente: $v" }
}
if (-not (Get-Command age -ErrorAction SilentlyContinue)) { throw 'age nao encontrado no PATH' }

$ssl = if ($env:BTHS_BACKUP_SSL_MODE) { $env:BTHS_BACKUP_SSL_MODE } else { 'require' }
$agora = Get-Date
$arquivo = "bths-$Ambiente-$($agora.ToString('yyyyMMdd-HHmmss')).sql.age"
$tmp = Join-Path $env:TEMP ("bths-backup-" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force -Path $tmp, $DestinoLocal, $DestinoDrive | Out-Null

try {
    $dump = "pg_dump -h $($env:BTHS_BACKUP_HOST) -p $($env:BTHS_BACKUP_PORT) -U $($env:BTHS_BACKUP_USER) -d $($env:BTHS_BACKUP_DB) --no-owner --no-privileges -f /out/dump.sql"
    docker run --rm -e PGPASSWORD -e "PGSSLMODE=$ssl" -v "${tmp}:/out" postgres:17 sh -c $dump
    if ($LASTEXITCODE -ne 0) { throw "pg_dump falhou (codigo $LASTEXITCODE)" }

    $fim = Get-Content (Join-Path $tmp 'dump.sql') -Tail 5
    if (-not ($fim -match 'PostgreSQL database dump complete')) { throw 'dump incompleto: marcador final ausente' }

    $saida = Join-Path $DestinoLocal $arquivo
    age -r $env:BTHS_BACKUP_AGE_RECIPIENT -o $saida (Join-Path $tmp 'dump.sql')
    if ($LASTEXITCODE -ne 0) { throw "age falhou (codigo $LASTEXITCODE)" }

    Copy-Item $saida (Join-Path $DestinoDrive $arquivo)

    if ($agora.Day -eq 1) {
        foreach ($base in $DestinoLocal, $DestinoDrive) {
            $mensal = Join-Path $base 'mensal'
            New-Item -ItemType Directory -Force -Path $mensal | Out-Null
            Copy-Item $saida (Join-Path $mensal $arquivo)
            Get-ChildItem $mensal -Filter "bths-$Ambiente-*.sql.age" | Sort-Object LastWriteTime -Descending | Select-Object -Skip 3 | Remove-Item
        }
    }

    foreach ($base in $DestinoLocal, $DestinoDrive) {
        Get-ChildItem $base -Filter "bths-$Ambiente-*.sql.age" | Sort-Object LastWriteTime -Descending | Select-Object -Skip $Retencao | Remove-Item
    }

    $tam = (Get-Item $saida).Length
    Write-Host "OK: $arquivo ($tam bytes) em $DestinoLocal e $DestinoDrive"
}
finally {
    Remove-Item -Recurse -Force $tmp -ErrorAction SilentlyContinue
}