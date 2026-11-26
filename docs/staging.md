# Ambiente de staging

Staging público do BTHS Platform, em HTTPS, usado para validar o deploy antes de produção. Usa **apenas dados fictícios**.

## Endereços

| Parte | URL |
|---|---|
| Frontend (entrada do usuário) | https://bths-staging-frontend.onrender.com |
| Backend (API) | https://bths-staging-backend.onrender.com |

O navegador só fala com o frontend. As chamadas `/api/*` são encaminhadas pelo Next.js (rewrite) para o backend, então o cookie `BTHS_TOKEN` é de origem única (host-only, `Secure`, `HttpOnly`, `SameSite=Lax`).

## Topologia

```
navegador --HTTPS--> Next.js (Render) --rewrite /api--> Spring Boot (Render) --TLS--> PostgreSQL 17 (Neon)
```

- Frontend e backend: dois Web Services no Render (plano free, Docker, região Virginia, branch `develop`).
- Banco: PostgreSQL 17 gerenciado no Neon (plano Free, projeto `bths-staging`, banco `neondb`, região AWS US East 1 / N. Virginia, a mesma do Render, SSL obrigatório). Até 25/11/2026 o staging usou MySQL 8.4 no Aiven (#172).
- Perfil Spring: `staging`. Flyway roda no startup com usuário próprio de migração; a aplicação usa `ddl-auto=validate`.

## Desvios em relação à produção

- **Backend acessível publicamente.** Em produção o backend deve ficar em rede privada; no Render free não existe rede privada entre serviços.
- **Região nos EUA.** O Render grátis não oferece Brasil, e o banco fica na mesma região do backend para reduzir a latência. Produção precisa de análise de região e LGPD antes de receber dados reais.
- **Hibernação.** No plano free o serviço dorme após 15 min sem uso. A primeira requisição pode levar de 1 a 3 min (a aplicação sobe em cerca de 3 min a frio). Se o backend estiver dormindo, o proxy do frontend pode responder 500 até ele acordar.
- **Banco suspende por inatividade.** No plano Free do Neon, o banco suspende depois de 5 min sem consultas; a primeira consulta depois disso leva alguns segundos a mais. O pool do HikariCP foi ajustado para não manter o banco acordado, e o monitor externo deve usar a liveness, não a readiness (ver `docs/configuracao-producao.md`, #175).
- **TLS do banco:** `sslmode=require` (criptografa, mas não verifica a identidade do servidor). Produção deve usar `verify-full` com o certificado da CA.
- **HSTS:** o backend envia `Strict-Transport-Security`; as páginas do frontend ainda não.
- **Deploy manual** (Auto-Deploy desligado) em ambos os serviços.

## Variáveis de ambiente

Nunca versionar valores. Os nomes abaixo são cadastrados no painel do Render.

Backend (`bths-staging-backend`): `SPRING_PROFILES_ACTIVE`, `PORT`, `JAVA_TOOL_OPTIONS`, `BTHS_DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `BTHS_DB_MIGRACAO_USUARIO`, `BTHS_DB_MIGRACAO_SENHA`, `BTHS_CORS_ALLOWED_ORIGINS`, `JWT_SECRET`.

Frontend (`bths-staging-frontend`): `BTHS_INTERNAL_API_URL` (URL pública do backend, lida em build) e `PORT`. `NEXT_PUBLIC_API_URL` fica vazio (mesma origem).

- Segredos do staging são exclusivos dele e não são reaproveitados em produção.
- Dois usuários no banco, com privilégio mínimo: `bths_app` (aplicação, DML) e `bths_migracao` (migração, DDL). O `neondb_owner`, criado pelo Neon, não é usado pelo BTHS.
- `BTHS_DB_URL` usa o endereço **direto** do Neon (sem `-pooler`) com `sslmode=require`: o backend já tem o pool do HikariCP, e o Flyway precisa de conexão direta.
- `JWT_SECRET` precisa ser Base64, com no mínimo 32 bytes decodificados (validado na subida).
- A URL do backend é fixada no build do frontend: se mudar, é preciso novo deploy do frontend.

## Como foi montado

1. Neon: criar o projeto `bths-staging` (PostgreSQL 17, AWS US East 1) e criar os usuários `bths_migracao` e `bths_app` pelo SQL Editor (`docs/segredos.md`). As senhas são geradas pelo **Reset password** do Neon.
2. Render: criar o Web Service do backend (Docker, contexto `./backend`, Dockerfile `./backend/Dockerfile`, health check `/actuator/health/liveness`) e carregar as variáveis.
3. Conferir a execução do Flyway (V1 aplicada do zero pelo `bths_migracao`) e o readiness em `/actuator/health/readiness`.
4. Render: criar o Web Service do frontend (Docker, contexto `./frontend`, Dockerfile `./frontend/Dockerfile`, health check `/login`) com `BTHS_INTERNAL_API_URL` e `PORT=3000`.
5. Criar um usuário ADMIN de teste direto no banco (não existe seed), com a senha em hash BCrypt de 60 caracteres. Os demais usuários de teste (STAFF e HOSPEDE) são criados pelo próprio sistema.

## Como validar

- `GET /actuator/health/readiness` no backend retorna `{"status":"UP"}`.
- `GET /login` no frontend retorna 200 com `Cache-Control: private, no-store`.
- Fluxo `GET /api/auth/csrf` → `POST /api/auth/login` (com `X-XSRF-TOKEN` igual ao valor do cookie `XSRF-TOKEN`) → `GET /api/auth/me` → `POST /api/auth/logout`, sempre pelo domínio do frontend.
- Login: cookie `BTHS_TOKEN` com `Secure; HttpOnly; SameSite=Lax`.
- Logout: 204, `Clear-Site-Data: "cache", "storage"` e cookie expirado.
- No navegador: login, menu e telas carregam.

## Limitações conhecidas

- Domínio próprio (por exemplo `app-staging.beattrips.com.br`) ainda não configurado; o staging usa o domínio `onrender.com`.

## Migração para o Neon (#172) - 25/11/2026

| Verificação | Resultado |
|---|---|
| Flyway | V1 aplicada com sucesso pelo `bths_migracao` |
| Tabelas | 15 (14 do BTHS e o histórico do Flyway) |
| `GET /actuator/health/readiness` | `{"status":"UP"}` com o `bths_app` |
| Login pelo frontend | ADMIN, STAFF e HOSPEDE autenticados, cada um com o perfil certo |
| Gravação | usuário STAFF, viagem, hóspede e acesso BTHS do hóspede cadastrados pelo sistema |
| Backup e restore | backup cifrado com `pg_dump` restaurado sem erro num PostgreSQL descartável (`docs/backup-restore.md`) |

## Validação de CSRF, cookies e sessão (#76)

Verificado em 24/10/2026 pelo domínio do frontend, com o usuário ADMIN de teste.

| Verificação | Resultado |
|---|---|
| Cookie `BTHS_TOKEN` | `HttpOnly; Secure; SameSite=Lax`, host-only, validade de 1 h |
| `GET /api/auth/csrf` | 200; cookie `XSRF-TOKEN` com `Secure` |
| POST sem `X-XSRF-TOKEN` | 403 |
| POST com `X-XSRF-TOKEN` errado | 403 |
| POST, PUT e PATCH com o token igual ao cookie | 201, 200 e 200 |
| PUT sem `X-XSRF-TOKEN` | 403 |
| Requisição sem cookie | 401 |
| Requisição com cookie `BTHS_TOKEN` inválido | 401 |
| Logout | 204, `Clear-Site-Data: "cache", "storage"` e cookie zerado |
| Requisição após o logout (sem o cookie) | 401 |
| Navegador: F5 logado, Voltar após sair, rota interna deslogado | sessão mantida; redireciona para `/login` |

JWT expirado retorna 401; a cobertura está nos testes do backend (`JwtServiceTest`, `JwtAuthenticationFilterTest` e `AuthCsrfIntegrationTest`), e não foi esperada ao vivo (a sessão dura 1 h).

### Limitação conhecida: JWT sem revogação

O logout apaga o cookie no navegador, mas o servidor não invalida o JWT: reenviar o token antigo continua retornando 200 até ele expirar (1 h). Só tem efeito se o token vazar, e o cookie é `HttpOnly` e `Secure`. Possíveis mitigações futuras: lista de revogação no servidor ou expiração mais curta.