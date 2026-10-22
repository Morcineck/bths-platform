# Topologia pública do BTHS

Issue #70. Decisão de endereço, rota e proxy antes do primeiro deploy.

## Decisão

- O BTHS (login dos hóspedes e painel ADMIN/STAFF) fica em **`https://app.beattrips.com.br`**.
- Interface e API na **mesma origem**: `https://app.beattrips.com.br/` serve o Next.js e `https://app.beattrips.com.br/api/*` chega ao Spring Boot.
- O domínio `www.beattrips.com.br` fica reservado para a página comercial da Beat Trips. É um site à parte: não recebe o cookie de login nem chama a API.
- Staging usa um endereço próprio, sugerido `app-staging.beattrips.com.br`, com a mesma topologia.

Um subdomínio não exige novo domínio: é um registro no DNS do domínio já registrado.

## Desenho

```text
Hóspede / equipe (navegador)
        │  HTTPS (443)
        ▼
Proxy reverso com TLS  ─ único componente com portas públicas (80/443)
        │  HTTP, rede interna
        ▼
Next.js (frontend, :3000)
        │  /api/*  →  rewrite interno
        ▼
Spring Boot (backend, :8080)  ─ sem porta pública
        │
        ▼
MySQL (:3306)                 ─ sem porta pública
```

- Só o proxy reverso publica portas. Backend e MySQL ficam em rede privada, como já ocorre no Compose.
- O proxy tem um único destino (o Next.js). O encaminhamento de `/api/*` ao backend já é feito pelo `rewrites` do `next.config.ts` (variável de build `BTHS_INTERNAL_API_URL`).
- Os endpoints de saúde do Spring (`/actuator/health`) ficam fora de `/api` e, portanto, **não** são alcançáveis pelo proxy público. A saúde do frontend é `/health`.

## Alternativas consideradas

| Opção | Resultado |
|---|---|
| **A. `app.beattrips.com.br`, mesma origem (escolhida)** | Isola o app do site comercial, cookie host-only, sem CORS entre origens, sem cookies de terceiros. |
| B. `www.beattrips.com.br` | Ocuparia o endereço natural do site comercial e misturaria scripts de marketing com a origem do login. |
| C. Hosts distintos (por exemplo `app.` e `api.`) | Exigiria cookie entre sites, `SameSite=None`, preflight CORS em toda chamada e depende de cookies de terceiros, que navegadores restringem (Safari e iPhone em especial). Rejeitada. |

## Cookies, CORS e CSRF nesta topologia

- **Cookie `BTHS_TOKEN`**: `HttpOnly`, `Secure=true`, `SameSite=Lax`, sem `Domain` (host-only em `app.beattrips.com.br`). O `www` não o recebe. Ver `docs/configuracao-producao.md`.
- **CORS**: a origem pública entra na lista, pois o proxy faz o Spring enxergar o `Origin` do navegador como origem diferente. Produção: `BTHS_CORS_ALLOWED_ORIGINS=https://app.beattrips.com.br`. O `www` **não** entra na lista: uma requisição vinda do `www` com `Origin` próprio é recusada pelo backend (403).
- **CSRF**: token duplo (`XSRF-TOKEN` + cabeçalho `X-XSRF-TOKEN`), já ativo. Observação: `www` e `app` são o mesmo *site* para o `SameSite` (mesmo domínio registrável), então `SameSite=Lax` sozinho **não** protege contra uma requisição saída do `www`. A defesa nesse caso são o token CSRF e a lista fechada de CORS.

## Configuração necessária

**DNS**
- Registro `app` (A/AAAA ou CNAME) apontando para o servidor ou plataforma do app.
- Staging: registro `app-staging`.
- O `www` e a raiz continuam apontando para a página comercial, sem relação com o BTHS.

**Certificado HTTPS**
- Válido para `app.beattrips.com.br` (e para o endereço de staging). O `www` tem certificado próprio, de responsabilidade do site comercial.
- Emissão e renovação automáticas pelo proxy (por exemplo, Let's Encrypt) ou pela plataforma escolhida.

**Backend**

| Variável | Produção |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `BTHS_CORS_ALLOWED_ORIGINS` | `https://app.beattrips.com.br` |
| `BTHS_AUTH_COOKIE_SECURE` | não definir (padrão `true`) |
| `BTHS_DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` | segredos do ambiente, nunca no repositório |

**Frontend (argumentos de build)**

| Argumento | Produção |
|---|---|
| `NEXT_PUBLIC_API_URL` | vazio (mesma origem) |
| `BTHS_INTERNAL_API_URL` | endereço interno do backend (no Compose, `http://backend:8080`) |

**Rede**
- Publicar 80 e 443 somente no proxy. No Compose de produção, a porta `127.0.0.1:3000` do frontend deixa de ser a porta de entrada.
- Backend e MySQL sem portas publicadas.

**Links e QR Code**
- Links enviados aos hóspedes e URLs de check-in devem usar sempre `https://app.beattrips.com.br`. Trocar o endereço depois invalida links já distribuídos.

## Hospedagem

Ainda não decidida. As duas opções são compatíveis com este desenho:
1. **Servidor próprio com Docker Compose**: o proxy reverso entra como um serviço da stack e termina o TLS.
2. **Plataforma gerenciada**: o TLS e o roteamento são da plataforma, e é preciso garantir um destino interno para o backend.

A escolha fica para a issue #75 (staging).

## A verificar no staging (#75 e #76)

- Que o cookie `XSRF-TOKEN` recebe o atributo `Secure` atrás do proxy. O Spring decide esse atributo pelo esquema que o backend enxerga; com o proxy falando HTTP com o backend, ele precisa receber o esquema original por cabeçalhos de encaminhamento (`X-Forwarded-Proto`). Possível ajuste: `server.forward-headers-strategy`.
- Que o `X-Forwarded-Proto` chega íntegro pelas duas camadas (proxy e Next.js).
- Login completo no navegador, em desktop, Android e iPhone (issue #77).
- HSTS e demais cabeçalhos de segurança (issue #73) apenas depois que o HTTPS estiver estável no endereço.

## Critérios de aceite da #70

- [x] Arquitetura documentada (este documento).
- [x] Mesma origem versus hosts distintos: mesma origem.
- [x] Configuração necessária para staging e produção identificada.