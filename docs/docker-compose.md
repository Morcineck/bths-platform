
# BTHS Platform - Docker Compose

## Objetivo

Executar frontend Next.js, backend Spring Boot e MySQL
em ambiente Docker Compose independente.

Issue: #66

## Serviços

| Serviço | Tecnologia | Porta |
|---|---|---|
| frontend | Next.js 16 / Node 22 | 127.0.0.1:3000 |
| backend | Spring Boot 4.1 / Java 17 | 8080 interna |
| mysql | MySQL 8.4 | 3306 interna |

O frontend encaminha as chamadas /api para o backend
por meio dos rewrites do Next.js.

## Pré-requisitos

- Docker Desktop com Docker Compose
- Docker Engine em execução
- Porta 3000 disponível no computador

## Credenciais

Criar na raiz do projeto um arquivo `.env.compose`
com as seguintes variáveis:

BTHS_COMPOSE_DB_PASSWORD
BTHS_COMPOSE_ROOT_PASSWORD
BTHS_COMPOSE_JWT_SECRET

Todas devem receber valores aleatórios fortes e
exclusivos deste ambiente.

BTHS_COMPOSE_JWT_SECRET deve conter uma chave aleatória de pelo menos 256 bits, codificada em Base64.
O Docker Compose repassa seu valor ao backend pela variável JWT_SECRET.

O arquivo `.env.compose` é ignorado pelo Git.

NUNCA reutilizar as credenciais do banco original
nem versionar arquivos com senhas.

## Construção das imagens

Executar na raiz do repositório:

```powershell
docker compose --env-file .env.compose -f compose.yaml build
```

## Iniciar a stack

```powershell
docker compose --env-file .env.compose -f compose.yaml up -d --no-build
```

O MySQL deve ficar saudável antes da inicialização
do backend.

Acesse:

http://127.0.0.1:3000/login

## Verificar os serviços

```powershell
docker compose --env-file .env.compose -f compose.yaml ps
```

## Validar frontend

```powershell
curl.exe -sS -o NUL -w "HTTP %{http_code}" http://127.0.0.1:3000/login
```

Esperado: HTTP 200.

## Validar comunicação com backend

```powershell
curl.exe -sS -o NUL -w "HTTP %{http_code}" http://127.0.0.1:3000/api/auth/csrf
```

Esperado: HTTP 200.

Sem autenticação, `/api/auth/me` deve retornar
HTTP 401.

Uma requisição POST para `/api/auth/logout`
sem token CSRF deve ser bloqueada.

## Banco de dados

Banco: bths_compose

Usuário: bths_compose

Host interno: mysql

Porta interna: 3306

Volume: bths-compose_bths_mysql_data

O backend utiliza o perfil staging, Flyway habilitado
e Hibernate configurado como validate.

As migrações V1 e V2 foram validadas com sucesso.

## Persistência

O volume permanece após:

```powershell
docker compose --env-file .env.compose -f compose.yaml down
```

Para reiniciar:

```powershell
docker compose --env-file .env.compose -f compose.yaml up -d --no-build
```

A persistência do histórico Flyway foi validada
após a remoção e recriação dos containers.

ATENÇÃO: não utilizar `down -v` sem autorização,
pois essa opção remove volumes gerenciados pelo Compose.

## Logs

```powershell
docker compose --env-file .env.compose -f compose.yaml logs --tail=100 backend
```

## Segurança e limitações

- MySQL não possui portas publicadas no host.
- Backend não possui porta publicada no host.
- Frontend é publicado somente em 127.0.0.1:3000.
- Credenciais são fornecidas externamente.
- O MySQL original localhost:3306/bths_platform
  não deve ser utilizado ou modificado.
- O ambiente não representa homologação de produção.
- HTTPS e cookies Secure ainda exigem configuração.
- O login autenticado completo ainda não foi
  homologado nesta stack.
- NEXT_PUBLIC_API_URL é definida no build.
- BTHS_INTERNAL_API_URL determina o destino dos
  rewrites durante o build do Next.js.
- O frontend ainda não aguarda um healthcheck HTTP
  do backend para iniciar.

## Validações realizadas

- Build Docker das imagens aprovado.
- Inicialização dos três serviços aprovada.
- MySQL 8.4.11 saudável.
- Spring Boot 4.1.0 inicializado.
- Flyway V1 e V2 aplicadas.
- Frontend /login respondeu HTTP 200.
- Encaminhamento /api/auth/csrf respondeu HTTP 200.
- Cookie XSRF-TOKEN recebido.
- Acesso anônimo a /api/auth/me retornou HTTP 401.
- POST /api/auth/logout sem CSRF retornou HTTP 403.
- POST /api/auth/logout com CSRF retornou HTTP 204.
- Persistência do volume confirmada após reinicialização.
- Flyway reconheceu schema V2 sem reaplicar migrações.

## Referências

- Issue #65: Dockerfiles do backend e frontend
- Issue #66: Docker Compose
- Issue #82: Homologação Flyway em staging
- docs/dockerfiles.md
