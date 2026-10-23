# Headers de segurança

Issue #73. Linha de base de headers HTTP de segurança para frontend e backend.

## Quem envia o quê

| Header | Origem | Valor | Objetivo |
|---|---|---|---|
| `X-Content-Type-Options` | Next.js e Spring | `nosniff` | Impede o navegador de adivinhar o tipo do conteúdo. |
| `X-Frame-Options` | Next.js e Spring | `DENY` | Impede que o BTHS seja aberto dentro de um frame (clickjacking). |
| `Referrer-Policy` | Next.js | `strict-origin-when-cross-origin` | Não vaza caminhos e parâmetros para outros sites. |
| `Referrer-Policy` | Spring | `no-referrer` | Respostas da API não precisam informar origem. |
| `Permissions-Policy` | Next.js | `camera=(self), microphone=(), geolocation=(), payment=()` | Libera só a câmera, necessária para ler QR Code no check-in. |
| `Content-Security-Policy-Report-Only` | Next.js | ver abaixo | Relata violações sem bloquear. |
| `Strict-Transport-Security` | Proxy com TLS | ver abaixo | Força HTTPS. Pendente da issue #75. |

Regras:
- O Next.js aplica seus headers a todas as rotas **exceto `/api`**. Os headers da API são do backend (Spring Security); os padrões `nosniff`, `X-Frame-Options` e `Cache-Control` ficam ativos e são cobertos por testes (`SecurityHeadersTest`).
- O Next.js não envia mais `X-Powered-By`.

## Política de segurança de conteúdo (CSP)

Modo atual: **`Report-Only`**. O navegador registra no console as violações, mas nada é bloqueado.

```text
default-src 'self'
script-src 'self' 'unsafe-inline'
style-src 'self' 'unsafe-inline'
img-src 'self' data: blob: https://i0.wp.com
font-src 'self'
connect-src 'self'
media-src 'self' blob:
worker-src 'self' blob:
object-src 'none'
base-uri 'self'
form-action 'self'
frame-ancestors 'none'
```

Por que `Report-Only` e `'unsafe-inline'`:
- O Next.js injeta scripts e estilos inline para hidratar a página. Sem `'unsafe-inline'`, o CSP exigiria *nonce* por requisição (via `proxy.ts`), mudança maior e mais arriscada, deixada para uma etapa futura.
- Mesmo com `'unsafe-inline'`, a política já restringe origens de rede (`connect-src`, `form-action`), objetos, `base` e frames.
- O clickjacking já é bloqueado hoje por `X-Frame-Options: DENY`. O `frame-ancestors` do CSP só passa a valer quando a política for aplicada.

Plano para ativar o bloqueio:
1. Validar no staging, com o console aberto, os fluxos: login, painel ADMIN/STAFF, app do hóspede, check-in com câmera e QR Code (issue #77).
2. Corrigir qualquer violação relatada (por exemplo, uma origem de imagem ou mídia não prevista).
3. Trocar `Content-Security-Policy-Report-Only` por `Content-Security-Policy` em `frontend/next.config.ts`.
4. Evolução futura: nonce por requisição, removendo `'unsafe-inline'` de `script-src`.

## HSTS

O `Strict-Transport-Security` é enviado pelo **proxy reverso que termina o TLS** (issue #75), e não pela aplicação. Só deve existir em ambiente com HTTPS estável.

Valores sugeridos, em escalada gradual:

```text
Strict-Transport-Security: max-age=300
```

Depois de validar o staging, subir para uma semana, depois um mês, e por fim um ano. Evitar `includeSubDomains` e `preload` no início: o domínio `beattrips.com.br` também serve a página comercial e outros subdomínios, e o HSTS, uma vez aceito pelo navegador, não é facilmente desfeito.

## Como validar

```powershell
foreach ($u in @("/login","/api/auth/csrf")) {
  "=== $u ==="
  (Invoke-WebRequest -Uri "http://127.0.0.1:3000$u" -UseBasicParsing).Headers.GetEnumerator() | Sort-Object Key | Where-Object { $_.Key -match "X-Content|X-Frame|Referrer|Permissions|Content-Security|Powered|Cache-Control|X-Request" } | ForEach-Object { "$($_.Key): $($_.Value)" }
}
```

## Limitações e pendências

- CSP ainda não bloqueia; depende da validação no staging (#75, #77).
- HSTS pendente do proxy com TLS (#75).
- A página `/login` é estática e vem com `Cache-Control: s-maxage=31536000`; a revisão de cache e dados privados está na issue #74.
- O cookie `XSRF-TOKEN` atrás do proxy deve ser conferido na #76 (ver `docs/topologia-publica.md`).