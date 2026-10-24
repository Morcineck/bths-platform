# Segredos e usuários do banco

Documento da issue #71. Define quais segredos o BTHS usa, como gerá-los, que privilégios cada usuário do MySQL tem e a política inicial de rotação.

## Inventário

| Segredo | Variável | Usado por | Onde fica |
|---|---|---|---|
| Chave de assinatura do JWT | `JWT_SECRET` | backend | variável protegida do ambiente |
| Usuário e senha da aplicação | `DB_USERNAME`, `DB_PASSWORD` | backend (conexão do dia a dia) | variável protegida do ambiente |
| Usuário e senha de migração | `BTHS_DB_MIGRACAO_USUARIO`, `BTHS_DB_MIGRACAO_SENHA` | Flyway, ao iniciar o backend | variável protegida do ambiente |
| Senha root do MySQL | `MYSQL_ROOT_PASSWORD` (no Compose: `BTHS_COMPOSE_ROOT_PASSWORD`) | administração do banco | variável protegida do ambiente; a aplicação nunca usa |
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

As senhas do banco não podem conter aspas simples nem barra invertida (o script do Compose recusa).

## Validação automática do `JWT_SECRET`

Nos perfis `staging` e `prod`, o `ValidadorSegredoJwt` impede a aplicação de iniciar se a chave:

- estiver ausente;
- não for Base64 válido;
- tiver menos de 256 bits (32 bytes) depois de decodificada;
- tiver bytes demais repetidos (menos de 16 valores distintos), como uma chave de zeros.

A mensagem de erro nunca mostra o valor da chave. Fora desses perfis (desenvolvimento e testes) a regra não se aplica.

## Usuários do MySQL

A aplicação nunca usa o root. Há dois usuários, com privilégios no schema do BTHS:

| Usuário | Variáveis | Privilégios |
|---|---|---|
| Aplicação | `DB_USERNAME` / `DB_PASSWORD` | `SELECT`, `INSERT`, `UPDATE`, `DELETE` |
| Migração (Flyway) | `BTHS_DB_MIGRACAO_USUARIO` / `BTHS_DB_MIGRACAO_SENHA` | os de DML, mais `CREATE`, `ALTER`, `DROP`, `INDEX` e `REFERENCES` |

Os perfis `staging` e `prod` configuram o Flyway com o usuário de migração (`spring.flyway.user` e `spring.flyway.password`). O `UsuariosBancoPerfisTest` garante que a separação e a ausência de senhas nos arquivos de configuração não regridam.

### No Compose local

O script `docker/mysql/criar-usuarios.sh` cria os dois usuários e roda automaticamente na primeira inicialização de um volume vazio. Ele é idempotente: em um volume que já existe, aplique-o à mão:

```powershell
docker compose --env-file .env.compose -f compose.yaml exec mysql bash /docker-entrypoint-initdb.d/criar-usuarios.sh
```

### Em staging e produção

Criar os usuários uma única vez, como administrador do banco, com os mesmos privilégios (ajuste o schema e o host de origem da aplicação):

```sql
CREATE USER 'bths_app'@'<host>' IDENTIFIED BY '<senha-da-aplicacao>';
CREATE USER 'bths_migracao'@'<host>' IDENTIFIED BY '<senha-de-migracao>';

GRANT SELECT, INSERT, UPDATE, DELETE
  ON `<schema>`.* TO 'bths_app'@'<host>';

GRANT SELECT, INSERT, UPDATE, DELETE,
      CREATE, ALTER, DROP, INDEX, REFERENCES
  ON `<schema>`.* TO 'bths_migracao'@'<host>';
```

Se uma migração futura precisar de views, triggers ou procedures, o privilégio correspondente é concedido **apenas ao usuário de migração**.

## Política inicial de rotação

| Segredo | Quando rotacionar |
|---|---|
| `JWT_SECRET` | a cada 6 meses e em qualquer suspeita de vazamento |
| Senha da aplicação | a cada 6 meses e em qualquer suspeita de vazamento |
| Senha de migração | a cada 6 meses e em qualquer suspeita de vazamento |
| Senha root do MySQL | a cada 12 meses e em qualquer suspeita de vazamento |
| `SONAR_TOKEN` | a cada 12 meses e quando alguém com acesso sair do projeto |

Registrar a data da última rotação de cada segredo no local onde os segredos são guardados.

### Procedimentos

**`JWT_SECRET`.** Gerar uma chave nova, atualizar a variável no ambiente e reiniciar o backend. Todas as sessões ativas são encerradas (o cookie dura 1 hora) e os usuários precisam entrar de novo. Ainda não existe rotação sem interrupção.

**Senha da aplicação.**
1. No MySQL: `ALTER USER 'bths_app'@'<host>' IDENTIFIED BY '<nova-senha>';`
2. Atualizar `DB_PASSWORD` no ambiente.
3. Reiniciar o backend logo em seguida. Conexões novas com a senha antiga falham entre o passo 1 e o 3, então fazer em horário de baixo uso.

**Senha de migração.**
1. No MySQL: `ALTER USER 'bths_migracao'@'<host>' IDENTIFIED BY '<nova-senha>';`
2. Atualizar `BTHS_DB_MIGRACAO_SENHA` no ambiente.

Sem indisponibilidade: a senha só é usada quando o backend inicia. Se o passo 2 for esquecido, o próximo deploy falha na inicialização (falha segura).

**Senha root.** Trocar no MySQL e na variável do ambiente. A aplicação não usa o root, então não há impacto no backend.

**No Compose local.** Alterar o valor no `.env.compose`, recriar o MySQL (`up -d mysql`) e reaplicar o script `criar-usuarios.sh` (acima), depois recriar o backend (`up -d backend`).

**Em caso de vazamento.** Rotacionar imediatamente todos os segredos que possam ter sido expostos, na ordem: senhas do banco, `JWT_SECRET`, `SONAR_TOKEN`. Depois revisar os logs do período (`docs/observabilidade.md`).

## Limitações e próximos passos

- O gerenciador de segredos do servidor depende da hospedagem (issue #75).
- O Flyway ainda roda dentro da aplicação, então as credenciais de migração ficam no ambiente do backend. A separação protege contra SQL injection, não contra o controle do container. Quando a hospedagem estiver definida, a migração pode virar um passo separado, sem essas credenciais no backend.
- A rotação do `JWT_SECRET` derruba as sessões. Aceitar duas chaves ao mesmo tempo (a nova e a anterior) permitiria rotacionar sem interromper ninguém.
- Uma varredura automática de segredos no CI (por exemplo, gitleaks) ainda não existe.