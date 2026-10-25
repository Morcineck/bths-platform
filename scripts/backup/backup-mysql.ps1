<#
Backup cifrado do MySQL do BTHS (issue #78).

Le a conexao de variaveis de ambiente:
  BTHS_BACKUP_HOST, BTHS_BACKUP_PORT, BTHS_BACKUP_DB, BTHS_BACKUP_USER,
  MYSQL_PWD (senha do usuario de backup), BTHS_BACKUP_AGE_RECIPIENT (chave publica age)
  BTHS_BACKUP_SSL_MODE (opcional, padrao REQUIRED)

Gera: dump -> cifra com age -> grava no destino local e no Drive.
Mantem os ultimos $Retencao diarios e os 3 mensais mais recentes.
#>
param(
    [string]$Ambiente = 'staging',
    [string]$DestinoLocal = 'C:\Users\robso\Backups\bths',
    [string]$DestinoDrive = 'G:\Meu Drive\Backups\bths',
    [int]$Retencao = 14
)

$ErrorActionPreference = 'Stop'

foreach ($v in 'BTHS_BACKUP_HOST', 'BTHS_BACKUP_PORT', 'BTHS_BACKUP_DB', 'BTHS_BACKUP_USER', 'MYSQL_PWD', 'BTHS_BACKUP_AGE_RECIPIENT') {
    if (-not (Test-Path "env:$v")) { throw "Variavel de ambiente ausente: $v" }
}
if (-not (Get-Command age -ErrorAction SilentlyContinue)) { throw 'age nao encontrado no PATH' }

$ssl = if ($env:BTHS_BACKUP_SSL_MODE) { $env:BTHS_BACKUP_SSL_MODE } else { 'REQUIRED' }
$agora = Get-Date
$arquivo = "bths-$Ambiente-$($agora.ToString('yyyyMMdd-HHmmss')).sql.age"
$tmp = Join-Path $env:TEMP ("bths-backup-" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force -Path $tmp, $DestinoLocal, $DestinoDrive | Out-Null

try {
    $dump = "mysqldump -h $($env:BTHS_BACKUP_HOST) -P $($env:BTHS_BACKUP_PORT) -u $($env:BTHS_BACKUP_USER) --ssl-mode=$ssl --single-transaction --routines --triggers --no-tablespaces --set-gtid-purged=OFF $($env:BTHS_BACKUP_DB) > /out/dump.sql"
    docker run --rm -e MYSQL_PWD -v "${tmp}:/out" mysql:8.4 sh -c $dump
    if ($LASTEXITCODE -ne 0) { throw "mysqldump falhou (codigo $LASTEXITCODE)" }

    $fim = Get-Content (Join-Path $tmp 'dump.sql') -Tail 5
    if (-not ($fim -match 'Dump completed')) { throw 'dump incompleto: marcador final ausente' }

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