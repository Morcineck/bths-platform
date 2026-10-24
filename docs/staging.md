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
navegador --HTTPS--> Next.js (Render) --rewrite /api--> Spring Boot (Render) --TLS--> MySQL 8.4 (Aiven)
```

- Frontend e backend: dois Web Services no Render (plano free, Docker, região Virginia, branch `develop`).
- Banco: MySQL 8.4 gerenciado no Aiven (plano grátis, América do Norte, SSL obrigatório).
- Perfil Spring: `staging`. Flyway roda no startup com usuário próprio de migração; a aplicação usa `ddl-auto=validate`.

## Desvios em relação à produção

- **Backend acessível publicamente.** Em produção o backend deve ficar em rede privada; no Render free não existe rede privada entre serviços.
- **Região nos EUA.** Render e Aiven grátis não oferecem Brasil. Produção precisa de análise de região e LGPD antes de receber dados reais.
- **Hibernação.** No plano free o serviço dorme após 15 min sem uso. A primeira requisição pode levar de 1 a 3 min (a aplicação sobe em cerca de 3 min a frio). Se o backend estiver dormindo, o proxy do frontend pode responder 500 até ele acordar.
- **Banco desliga por inatividade** no plano grátis do Aiven.
- **TLS do banco:** `sslMode=REQUIRED` (criptografa, mas não verifica a identidade do servidor). Produção deve usar `VERIFY_CA` ou `VERIFY_IDENTITY` com o certificado da CA.
- **HSTS:** o backend envia `Strict-Transport-Security`; as páginas do frontend ainda não.
- **Deploy manual** (Auto-Deploy desligado) em ambos os serviços.

## Variáveis de ambiente

Nunca versionar valores. Os nomes abaixo são cadastrados no painel do Render.

Backend (`bths-staging-backend`): `SPRING_PROFILES_ACTIVE`, `PORT`, `JAVA_TOOL_OPTIONS`, `BTHS_DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `BTHS_DB_MIGRACAO_USUARIO`, `BTHS_DB_MIGRACAO_SENHA`, `BTHS_CORS_ALLOWED_ORIGINS`, `JWT_SECRET`.

Frontend (`bths-staging-frontend`): `BTHS_INTERNAL_API_URL` (URL pública do backend, lida em build) e `PORT`. `NEXT_PUBLIC_API_URL` fica vazio (mesma origem).

- Segredos do staging são exclusivos dele e não são reaproveitados em produção.
- Dois usuários no banco, com privilégio mínimo: aplicação (DML) e migração (DML + DDL).
- `JWT_SECRET` precisa ser Base64, com no mínimo 32 bytes decodificados (validado na subida).
- A URL do backend é fixada no build do frontend: se mudar, é preciso novo deploy do frontend.

## Como foi montado

1. Aiven: criar serviço MySQL grátis e criar os dois usuários de banco.
2. Render: criar o Web Service do backend (Docker, contexto `./backend`, Dockerfile `./backend/Dockerfile`, health check `/actuator/health/liveness`) e carregar as variáveis.
3. Conferir nos logs a execução do Flyway (V1 e V2 aplicadas do zero) e o readiness em `/actuator/health/readiness`.
4. Render: criar o Web Service do frontend (Docker, contexto `./frontend`, Dockerfile `./frontend/Dockerfile`, health check `/login`) com `BTHS_INTERNAL_API_URL` e `PORT=3000`.
5. Criar um usuário ADMIN de teste direto no banco (não existe seed). A senha é aleatória, o hash é BCrypt de 60 caracteres e as credenciais ficam só em arquivo local ignorado pelo Git.

## Como validar

- `GET /actuator/health/readiness` no backend retorna `{"status":"UP"}`.
- `GET /login` no frontend retorna 200 com `Cache-Control: private, no-store`.
- Fluxo `GET /api/auth/csrf` → `POST /api/auth/login` (com `X-XSRF-TOKEN` igual ao valor do cookie `XSRF-TOKEN`) → `GET /api/auth/me` → `POST /api/auth/logout`, sempre pelo domínio do frontend.
- Login: cookie `BTHS_TOKEN` com `Secure; HttpOnly; SameSite=Lax`.
- Logout: 204, `Clear-Site-Data: "cache", "storage"` e cookie expirado.
- No navegador: login, menu e telas carregam.

## Limitações conhecidas

- O dashboard fica em "Carregando dashboard..." quando não existe nenhuma viagem cadastrada (estado vazio sem tratamento no frontend). Aparece no staging por causa do banco vazio.
- Domínio próprio (por exemplo `app-staging.beattrips.com.br`) ainda não configurado; o staging usa o domínio `onrender.com`.
- Sem backup/restore testado do banco.