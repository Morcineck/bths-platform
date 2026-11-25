# Segredos e usuários do banco

Documento da issue #71. Define quais segredos o BTHS usa, como gerá-los, que privilégios cada usuário do banco tem e a política inicial de rotação.

## Inventário

| Segredo | Variável | Usado por | Onde fica |
|---|---|---|---|
| Chave de assinatura do JWT | `JWT_SECRET` | backend | variável protegida do ambiente |
| Usuário e senha da aplicação | `DB_USERNAME`, `DB_PASSWORD` | backend (conexão do dia a dia) | variável protegida do ambiente |
| Usuário e senha de migração | `BTHS_DB_MIGRACAO_USUARIO`, `BTHS_DB_MIGRACAO_SENHA` | Flyway, ao iniciar o backend | variável protegida do ambiente |
| Senha do dono do banco | no Neon, a do `neondb_owner`; no Compose, `POSTGRES_PASSWORD` (vinda de `BTHS_COMPOSE_ROOT_PASSWORD`) | administração do banco | painel do provedor ou variável protegida; a aplicação nunca usa |
| Token do SonarQube | `SONAR_TOKEN` | CI | segredos do GitHub Actions |

No Compose local, os valores vêm do `.env.compose` (ignorado pelo Git): `BTHS_COMPOSE_DB_PASSWORD`, `BTHS_COMPOSE_MIGRACAO_PASSWORD`, `BTHS_COMPOSE_ROOT_PASSWORD` e `BTHS_COMPOSE_JWT_SECRET`.

## Regras

- Nenhum segredo é versionado. O `.gitignore` bloqueia `.env` e `.env.*`; só o `backend/.env.example`, com valores fictícios, é versionado.
- Cada ambiente (desenvolvimento, Compose local, staging e produção) tem **valores próprios**. Nunca copiar um segredo de staging para produção, nem o contrário.
- Segredos não vão para imagens, logs, mensagens de commit, issues nem capturas de tela.
- Em staging e produção as variáveis são obrigatórias e **sem valor padrão**: a aplicação não inicia sem elas. Onde guardar (gerenciador de segredos do provedor ou variáveis protegidas do servidor) será decidido junto com a hospedagem, na issue #75.

## Gerar os valores

Chave JWT (256 bits em Base64):

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

Senha de banco (192 bits em hexadecimal):

```powershell
$bytes = New-Object byte[] 24
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
-join ($bytes | ForEach-Object { $_.ToString("x2") })
```

As senhas do banco não podem conter aspas simples nem barra invertida (o script do Compose recusa). No Neon, as senhas são geradas pelo próprio painel (**Reset password**).

## Validação automática do `JWT_SECRET`

Nos perfis `staging` e `prod`, o `ValidadorSegredoJwt` impede a aplicação de iniciar se a chave:

- estiver ausente;
- não for Base64 válido;
- tiver menos de 256 bits (32 bytes) depois de decodificada;
- tiver bytes demais repetidos (menos de 16 valores distintos), como uma chave de zeros.

A mensagem de erro nunca mostra o valor da chave. Fora desses perfis (desenvolvimento e testes) a regra não se aplica.

## Usuários do banco

A aplicação nunca usa o dono do banco. Há dois usuários, com privilégios no schema `public` do BTHS:

| Usuário | Variáveis | Privilégios |
|---|---|---|
| Aplicação | `DB_USERNAME` / `DB_PASSWORD` | `SELECT`, `INSERT`, `UPDATE`, `DELETE` |
| Migração (Flyway) | `BTHS_DB_MIGRACAO_USUARIO` / `BTHS_DB_MIGRACAO_SENHA` | `CREATE` no schema; é dono das tabelas que cria |

No PostgreSQL, a tabela pertence a quem a cria. Por isso as permissões da aplicação são dadas com `ALTER DEFAULT PRIVILEGES`: toda tabela que o Flyway criar já nasce liberada para a aplicação.

Os perfis `staging` e `prod` configuram o Flyway com o usuário de migração (`spring.flyway.user` e `spring.flyway.password`). O `UsuariosBancoPerfisTest` garante que a separação e a ausência de senhas nos arquivos de configuração não regridam.

### No Compose local

O script `docker/postgres/criar-usuarios.sh` cria os dois usuários e roda automaticamente na primeira inicialização de um volume vazio. Ele é idempotente: em um volume que já existe, aplique-o à mão:

```powershell
docker compose --env-file .env.compose -f compose.yaml exec postgres bash /docker-entrypoint-initdb.d/criar-usuarios.sh
```

### Em staging e produção

Criar os usuários uma única vez, como dono do banco (no Neon, pelo SQL Editor com o `neondb_owner`), antes do primeiro deploy:

```sql
CREATE ROLE bths_migracao LOGIN;
CREATE ROLE bths_app LOGIN;

-- permite ao dono definir as permissões automáticas em nome do usuário de migração
GRANT bths_migracao TO neondb_owner;

REVOKE ALL ON DATABASE <banco> FROM PUBLIC;
GRANT CONNECT ON DATABASE <banco> TO bths_app, bths_migracao;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO bths_app;
GRANT USAGE, CREATE ON SCHEMA public TO bths_migracao;

ALTER DEFAULT PRIVILEGES FOR ROLE bths_migracao IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO bths_app;
ALTER DEFAULT PRIVILEGES FOR ROLE bths_migracao IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO bths_app;
```

Os usuários nascem sem senha, para a senha não passar pelo SQL Editor (que guarda histórico). O Neon só permite **Reset password** em usuário que já tem senha: definir antes uma senha provisória aleatória com `ALTER ROLE <usuario> WITH PASSWORD '<provisoria>';` e, em seguida, usar o **Reset password** na página Roles. A senha gerada aparece uma única vez.

Se uma migração futura precisar de views, triggers ou procedures, o privilégio correspondente é concedido **apenas ao usuário de migração**.

## Política inicial de rotação

| Segredo | Quando rotacionar |
|---|---|
| `JWT_SECRET` | a cada 6 meses e em qualquer suspeita de vazamento |
| Senha da aplicação | a cada 6 meses e em qualquer suspeita de vazamento |
| Senha de migração | a cada 6 meses e em qualquer suspeita de vazamento |
| Senha do dono do banco | a cada 12 meses e em qualquer suspeita de vazamento |
| `SONAR_TOKEN` | a cada 12 meses e quando alguém com acesso sair do projeto |

Registrar a data da última rotação de cada segredo no local onde os segredos são guardados.

### Procedimentos

**`JWT_SECRET`.** Gerar uma chave nova, atualizar a variável no ambiente e reiniciar o backend. Todas as sessões ativas são encerradas (o cookie dura 1 hora) e os usuários precisam entrar de novo. Ainda não existe rotação sem interrupção.

**Senha da aplicação.**
1. No Neon: **Reset password** do `bths_app` na página Roles (ou `ALTER ROLE bths_app WITH PASSWORD '<nova-senha>';` em outro PostgreSQL).
2. Atualizar `DB_PASSWORD` no ambiente.
3. Reiniciar o backend logo em seguida. Conexões novas com a senha antiga falham entre o passo 1 e o 3, então fazer em horário de baixo uso.

**Senha de migração.**
1. No Neon: **Reset password** do `bths_migracao` na página Roles (ou `ALTER ROLE bths_migracao WITH PASSWORD '<nova-senha>';` em outro PostgreSQL).
2. Atualizar `BTHS_DB_MIGRACAO_SENHA` no ambiente.

Sem indisponibilidade: a senha só é usada quando o backend inicia. Se o passo 2 for esquecido, o próximo deploy falha na inicialização (falha segura).

**Senha do dono do banco.** Trocar no painel do provedor (no Neon, **Reset password** do `neondb_owner`). A aplicação não usa o dono, então não há impacto no backend.

**No Compose local.** Alterar o valor no `.env.compose`, recriar o PostgreSQL (`up -d postgres`) e reaplicar o script `criar-usuarios.sh` (acima), depois recriar o backend (`up -d backend`).

**Em caso de vazamento.** Rotacionar imediatamente todos os segredos que possam ter sido expostos, na ordem: senhas do banco, `JWT_SECRET`, `SONAR_TOKEN`. Depois revisar os logs do período (`docs/observabilidade.md`).

## Limitações e próximos passos

- O gerenciador de segredos do servidor depende da hospedagem (issue #75).
- O Flyway ainda roda dentro da aplicação, então as credenciais de migração ficam no ambiente do backend. A separação protege contra SQL injection, não contra o controle do container. Quando a hospedagem estiver definida, a migração pode virar um passo separado, sem essas credenciais no backend.
- A rotação do `JWT_SECRET` derruba as sessões. Aceitar duas chaves ao mesmo tempo (a nova e a anterior) permitiria rotacionar sem interromper ninguém.
- Uma varredura automática de segredos no CI (por exemplo, gitleaks) ainda não existe.