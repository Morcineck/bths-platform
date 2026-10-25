# BTHS Platform - Backup e restore do MySQL

**Issue:** #78 - Implementar e testar backup e restore do MySQL
**Estado:** procedimento validado no staging (Aiven) em 24/10/2026. Produção ainda não existe; este procedimento valerá para ela com as mudanças indicadas na seção 9.

## 1. Objetivo

Garantir a recuperação dos dados operacionais do BTHS se o banco for perdido, corrompido ou apagado pelo provedor.

## 2. Estratégia

| Ponto | Decisão |
|---|---|
| Ferramenta | `mysqldump` do container `mysql:8.4` (sem instalar cliente MySQL) |
| Usuário | `bths_backup`, somente leitura: `SELECT`, `SHOW VIEW`, `TRIGGER` no schema do BTHS |
| Frequência | diária (perda máxima aceita: 24 h) |
| Retenção | 14 backups diários e 3 mensais, em cada local |
| Cifra | `age`, com chave pública; só quem tem a chave privada decifra |
| Locais | cópia no PC do responsável e cópia no Google Drive (outro provedor, fora do Aiven) |
| Repositório | **nenhum dump vai para o Git**; só o script e esta documentação |

Motivos para ficar fora do Git: o repositório é público, o dump contém dados pessoais e hashes de senha, e o histórico do Git não esquece o que foi commitado.

## 3. Arquivos

| Item | Local |
|---|---|
| Script | `scripts/backup/backup-mysql.ps1` |
| Configuração (ignorada pelo Git) | `.env.backup` na raiz do repositório |
| Backups no PC | `C:\Users\robso\Backups\bths` (mensais em `mensal\`) |
| Backups no Drive | `G:\Meu Drive\Backups\bths` (mensais em `mensal\`) |
| Chave privada | `C:\Users\robso\Segredos\bths-backup.key`, mais cópias no Gerenciador de Senhas do Google e em pendrive |

Nome dos arquivos: `bths-<ambiente>-AAAAMMDD-HHMMSS.sql.age`.

## 4. Pré-requisitos

- Docker em execução.
- `age` instalado (`winget install --id FiloSottile.age -e --source winget`).
- Google Drive para computador sincronizando a pasta de backup.
- Par de chaves gerado com `age-keygen -o <arquivo>`. A chave pública vai em `BTHS_BACKUP_AGE_RECIPIENT`; a privada **nunca** vai para o repositório nem para a pasta de backups.

## 5. Usuário de backup no banco

Criar uma vez, como administrador do banco (a senha é gerada aleatoriamente e guardada só no `.env.backup`):

```sql
CREATE USER IF NOT EXISTS 'bths_backup'@'%' IDENTIFIED BY '<senha-de-backup>';
GRANT SELECT, SHOW VIEW, TRIGGER ON <schema>.* TO 'bths_backup'@'%';
```

Variáveis do `.env.backup`: `BTHS_BACKUP_HOST`, `BTHS_BACKUP_PORT`, `BTHS_BACKUP_DB`, `BTHS_BACKUP_USER`, `MYSQL_PWD`, `BTHS_BACKUP_AGE_RECIPIENT` e, opcionalmente, `BTHS_BACKUP_SSL_MODE` (padrão `REQUIRED`).

## 6. Fazer um backup

Na raiz do repositório, no PowerShell:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
Get-Content .env.backup | ForEach-Object { $k, $v = $_ -split '=', 2; Set-Item -Path "env:$k" -Value $v }
.\scripts\backup\backup-mysql.ps1 -Ambiente staging
```

O script gera o dump dentro de um container, só o considera válido se terminar com `Dump completed`, cifra com `age`, grava no PC e no Drive, apaga o texto puro e aplica a retenção. Saída esperada: `OK: <arquivo> (<bytes> bytes) em <PC> e <Drive>`.

## 7. Restaurar

**Nunca restaurar por cima do banco em uso.** Restaurar sempre num MySQL novo, conferir e só então decidir o que fazer (ver `docs/migracoes-banco.md`, seção 5).

1. Decifrar o backup escolhido:

```powershell
age -d -i C:\Users\robso\Segredos\bths-backup.key -o $env:TEMP\dump.sql <arquivo>.sql.age
```

2. Subir um MySQL descartável e restaurar:

```powershell
$pw = -join ((48..57) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
docker run -d --name bths-restore-teste -e MYSQL_ROOT_PASSWORD=$pw -e MYSQL_DATABASE=bths_restore mysql:8.4
# aguardar o servidor responder (mysqladmin -h127.0.0.1 ping)
docker cp $env:TEMP\dump.sql bths-restore-teste:/tmp/b.sql
docker exec bths-restore-teste sh -c 'MYSQL_PWD=$MYSQL_ROOT_PASSWORD mysql -uroot bths_restore < /tmp/b.sql'
```

3. Conferir (o SQL entra por stdin; no PowerShell 5.1 aspas duplas dentro de `-e` quebram):

```powershell
"SELECT COUNT(*) AS tabelas FROM information_schema.tables WHERE table_schema=DATABASE(); SELECT version, success FROM flyway_schema_history ORDER BY installed_rank; SELECT perfil, COUNT(*) AS total FROM usuarios GROUP BY perfil;" | docker exec -i bths-restore-teste sh -c 'MYSQL_PWD=$MYSQL_ROOT_PASSWORD mysql -uroot bths_restore'
```

4. Remover o texto puro e o container: `Remove-Item $env:TEMP\dump.sql` e `docker rm -f bths-restore-teste`.

Cuidado no PowerShell 5.1: o operador `>` converte a saída para UTF-16 e corrompe o dump. Por isso o dump é gravado dentro do container e copiado com `docker cp`.

## 8. Evidências (24/10/2026)

| Teste | Resultado |
|---|---|
| Backup e restore no MySQL do Compose | 15 tabelas; dump do restaurado idêntico ao original (0 diferenças fora dos comentários). O Compose só tinha estrutura e o histórico do Flyway. |
| Backup do Aiven (staging) com o script | `.sql.age` de 20.261 bytes, primeira linha `age-encryption.org/v1` (cifrado), gravado no PC e no Drive. |
| Restore a partir do arquivo cifrado | Chave privada decifrou; 15 tabelas; Flyway V1 e V2 com `success=1`; 1 ADMIN restaurado. |

## 9. Limitações e próximos passos

- **Agendamento:** o script automatiza o backup, mas ainda é executado à mão. Agendar (Agendador de Tarefas do Windows ou cron da VPS) fica para a definição da hospedagem de produção.
- **Dependência do PC:** o backup só roda com o PC ligado. Em produção, o ideal é executar no servidor, mantendo uma cópia fora dele.
- **Volume de dados:** o restore com dados foi provado só com os dados fictícios do staging (1 usuário ADMIN). Repetir com dados de produção antes do evento.
- **Chave privada:** quem perde a chave perde os backups cifrados. Manter as três cópias (PC, Gerenciador de Senhas do Google, pendrive) e conferir de tempos em tempos.
- **Aiven:** o plano gratuito não documenta retenção de dados de serviço desligado. Não tratar o Aiven como única cópia.
- **Rotação:** trocar a senha do `bths_backup` a cada 6 meses (política do `docs/segredos.md`).
- **Produção:** usar usuário de backup próprio do ambiente de produção e `-Ambiente producao`; nunca reaproveitar a senha ou os arquivos do staging.

## 10. Responsável

Robson (responsável único): executa os backups, guarda a chave privada e testa o restore a cada mudança relevante de schema e antes do evento.