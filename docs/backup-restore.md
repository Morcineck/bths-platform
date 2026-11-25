# BTHS Platform - Backup e restore do PostgreSQL

**Issues:** #78 - Implementar e testar backup e restore · #172 - Migração para PostgreSQL
**Estado:** procedimento adaptado para PostgreSQL (Neon) em 25/11/2026. **O backup e o restore no Neon ainda precisam ser testados** (seção 8). Produção ainda não existe; este procedimento valerá para ela com as mudanças indicadas na seção 9.

## 1. Objetivo

Garantir a recuperação dos dados operacionais do BTHS se o banco for perdido, corrompido ou apagado pelo provedor.

## 2. Estratégia

| Ponto | Decisão |
|---|---|
| Ferramenta | `pg_dump` do container `postgres:17` (sem instalar cliente PostgreSQL) |
| Usuário | `bths_backup`, somente leitura: `SELECT` nas tabelas e sequências do schema `public` |
| Frequência | diária (perda máxima aceita: 24 h) |
| Retenção | 14 backups diários e 3 mensais, em cada local |
| Cifra | `age`, com chave pública; só quem tem a chave privada decifra |
| Locais | cópia no PC do responsável e cópia no Google Drive (outro provedor, fora do Neon) |
| Repositório | **nenhum dump vai para o Git**; só o script e esta documentação |

Motivos para ficar fora do Git: o repositório é público, o dump contém dados pessoais e hashes de senha, e o histórico do Git não esquece o que foi commitado.

O dump é gerado com `--no-owner` e `--no-privileges`: ele não carrega donos nem permissões e pode ser restaurado em qualquer PostgreSQL. As permissões do destino são recriadas à parte (`docs/segredos.md`).

## 3. Arquivos

| Item | Local |
|---|---|
| Script | `scripts/backup/backup-postgres.ps1` |
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

Criar uma vez, como dono do banco (no Neon, pelo SQL Editor com o `neondb_owner`). O usuário nasce sem senha; a senha é definida depois com **Reset password** na página Roles do Neon e guardada só no `.env.backup`:

```sql
CREATE ROLE bths_backup LOGIN;
GRANT CONNECT ON DATABASE <banco> TO bths_backup;
GRANT USAGE ON SCHEMA public TO bths_backup;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO bths_backup;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO bths_backup;
ALTER DEFAULT PRIVILEGES FOR ROLE bths_migracao IN SCHEMA public
  GRANT SELECT ON TABLES TO bths_backup;
ALTER DEFAULT PRIVILEGES FOR ROLE bths_migracao IN SCHEMA public
  GRANT SELECT ON SEQUENCES TO bths_backup;
```

O Neon só permite **Reset password** em usuário que já tem senha. Antes do reset, definir uma senha provisória aleatória com `ALTER ROLE bths_backup WITH PASSWORD '<provisoria>';`.

Variáveis do `.env.backup`: `BTHS_BACKUP_HOST` (endereço **direto** do Neon, sem `-pooler`), `BTHS_BACKUP_PORT` (`5432`), `BTHS_BACKUP_DB`, `BTHS_BACKUP_USER`, `PGPASSWORD`, `BTHS_BACKUP_AGE_RECIPIENT` e, opcionalmente, `BTHS_BACKUP_SSL_MODE` (padrão `require`).

## 6. Fazer um backup

Na raiz do repositório, no PowerShell:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
Get-Content .env.backup | ForEach-Object { $k, $v = $_ -split '=', 2; Set-Item -Path "env:$k" -Value $v }
.\scripts\backup\backup-postgres.ps1 -Ambiente staging
```

O script gera o dump dentro de um container, só o considera válido se terminar com `PostgreSQL database dump complete`, cifra com `age`, grava no PC e no Drive, apaga o texto puro e aplica a retenção. Saída esperada: `OK: <arquivo> (<bytes> bytes) em <PC> e <Drive>`.

## 7. Restaurar

**Nunca restaurar por cima do banco em uso.** Restaurar sempre num PostgreSQL novo, conferir e só então decidir o que fazer (ver `docs/migracoes-banco.md`, seção 5).

1. Decifrar o backup escolhido:

```powershell
age -d -i C:\Users\robso\Segredos\bths-backup.key -o $env:TEMP\dump.sql <arquivo>.sql.age
```

2. Subir um PostgreSQL descartável e restaurar:

```powershell
$pw = -join ((48..57) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
docker run -d --name bths-restore-teste -e POSTGRES_PASSWORD=$pw -e POSTGRES_DB=bths_restore postgres:17
# aguardar o servidor responder
docker exec bths-restore-teste pg_isready -U postgres
docker cp $env:TEMP\dump.sql bths-restore-teste:/tmp/b.sql
docker exec bths-restore-teste psql -v ON_ERROR_STOP=1 -U postgres -d bths_restore -f /tmp/b.sql
```

3. Conferir (o SQL entra por stdin; no PowerShell 5.1 aspas duplas dentro de `-c` quebram):

```powershell
"SELECT COUNT(*) AS tabelas FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'; SELECT version, success FROM flyway_schema_history ORDER BY installed_rank; SELECT perfil, COUNT(*) AS total FROM usuarios GROUP BY perfil;" | docker exec -i bths-restore-teste psql -U postgres -d bths_restore
```

4. Remover o texto puro e o container: `Remove-Item $env:TEMP\dump.sql` e `docker rm -f bths-restore-teste`.

Cuidado no PowerShell 5.1: o operador `>` converte a saída para UTF-16 e corrompe o dump. Por isso o dump é gravado dentro do container e copiado com `docker cp`.

## 8. Evidências

### PostgreSQL (Neon)

Pendente: executar o backup do staging no Neon com o script e restaurar o arquivo cifrado num PostgreSQL descartável.

### Histórico em MySQL (24/10/2026)

Antes da issue #172, o mesmo processo foi validado com `mysqldump`: backup e restore no MySQL do Compose (15 tabelas, dump restaurado idêntico ao original), backup cifrado do staging no Aiven gravado no PC e no Drive, e restore a partir do arquivo cifrado com Flyway V1 e V2 e 1 ADMIN restaurados.

## 9. Limitações e próximos passos

- **Agendamento:** o script automatiza o backup, mas ainda é executado à mão. Agendar (Agendador de Tarefas do Windows ou cron da VPS) fica para a definição da hospedagem de produção.
- **Dependência do PC:** o backup só roda com o PC ligado. Em produção, o ideal é executar no servidor, mantendo uma cópia fora dele.
- **Volume de dados:** repetir o restore com dados de produção antes do evento.
- **Chave privada:** quem perde a chave perde os backups cifrados. Manter as três cópias (PC, Gerenciador de Senhas do Google, pendrive) e conferir de tempos em tempos.
- **Neon:** o plano grátis guarda histórico para restauração só por 6 horas. Não tratar o Neon como única cópia.
- **Rotação:** trocar a senha do `bths_backup` a cada 6 meses (política do `docs/segredos.md`).
- **Produção:** usar usuário de backup próprio do ambiente de produção e `-Ambiente producao`; nunca reaproveitar a senha ou os arquivos do staging.

## 10. Responsável

Robson (responsável único): executa os backups, guarda a chave privada e testa o restore a cada mudança relevante de schema e antes do evento.
