# Configuração de produção

Issue #69. Cookies, CORS e URL da API configuráveis por ambiente, sem
dependência de `localhost` em staging e produção.

## Variáveis por ambiente

| Propriedade | Variável de ambiente | Padrão no código | Dev local | Staging / Produção | Compose |
|---|---|---|---|---|---|
| `bths.auth.cookie-secure` | `BTHS_AUTH_COOKIE_SECURE` | `true` | `false` | `true` | `false` (roda em HTTP) |
| `bths.auth.cookie-same-site` | `BTHS_AUTH_COOKIE_SAME_SITE` | `Lax` | `Lax` | `Lax` | `Lax` |
| `bths.auth.duracao-sessao` | `BTHS_AUTH_DURACAO_SESSAO` | `1h` | `1h` | `1h` | `1h` |
| `bths.cors.allowed-origins` | `BTHS_CORS_ALLOWED_ORIGINS` | lista vazia | `http://localhost:3000` | **obrigatória** | `http://127.0.0.1:3000,http://localhost:3000` |

Variáveis de build do frontend (definidas com `--build-arg`):

| Argumento | Regra |
|---|---|
| `NEXT_PUBLIC_API_URL` | Vazio (padrão) significa mesma origem, via `/api`. Não pode conter localhost. |
| `BTHS_INTERNAL_API_URL` | **Obrigatório**, sem valor padrão. Endereço interno do backend (no Compose, `http://backend:8080`). Não pode conter localhost. |

## Cookie de autenticação (`BTHS_TOKEN`)

- **`Secure`**: o padrão do código é `true`. Só o desenvolvimento local e o
  Compose o desligam, de forma explícita, porque rodam em HTTP. Esquecer a
  configuração em um ambiente novo resulta em cookie protegido, nunca exposto.
- **`SameSite=Lax`**: adequado para frontend e API na mesma origem com token CSRF.
  `Strict` impediria o cookie em navegação vinda de outro site (links do
  WhatsApp, e-mail ou QR Code), e `None` só seria necessário com origens
  diferentes. `None` exige `Secure=true`; a aplicação não inicia com
  `SameSite=None` e `Secure=false`.
- **Sem `Domain`**: o cookie fica restrito ao host que o emitiu.
- **Duração**: `bths.auth.duracao-sessao` define a validade do JWT e o
  `Max-Age` do cookie, a partir de uma única propriedade, para os dois nunca divergirem.
  Valores aceitos: `30m`, `1h`, `PT1H`. Zero ou negativo impede a inicialização.

## CORS e o proxy `/api`

O navegador envia o cabeçalho `Origin` também em requisições de mesma origem
(por exemplo, o login). O proxy `/api` do Next.js encaminha a chamada ao
backend em outro endereço (`http://backend:8080`), então o Spring a trata como
requisição entre origens. **A origem pública do site precisa estar em
`bths.cors.allowed-origins`**, mesmo sem chamadas diretas entre origens.

Evidência (Compose, 22/10/2026): antes da correção, um POST de login com
`Origin: http://127.0.0.1:3000` retornava **403**, pois só `localhost:3000`
constava na lista. Depois da correção, o mesmo POST retorna 401 (credenciais
inválidas), e uma origem fora da lista continua retornando 403.

Regras da lista:
- cada origem tem esquema (`http` ou `https`), host e porta opcional;
- sem barra final, caminho ou curinga; a aplicação não inicia com `*` ou com
  `https://*.exemplo.com`;
- separar várias origens por vírgula.

O cabeçalho `X-Request-Id` é exposto ao frontend (`Access-Control-Expose-Headers`).

Em staging e produção a variável `BTHS_CORS_ALLOWED_ORIGINS` é obrigatória:
sem ela a aplicação não inicia, para nunca herdar a origem de desenvolvimento.

Exemplo para produção:

```text
BTHS_CORS_ALLOWED_ORIGINS=https://www.beattrips.com.br
```

A decisão sobre o domínio raiz (`beattrips.com.br` sem `www`) e a topologia
pública pertence à issue #70.

## Travas de build e inicialização

- O backend não inicia com `bths.cors.allowed-origins` inválida, com
  `SameSite` desconhecido, com `SameSite=None` sem `Secure` ou com duração de sessão não positiva.
- O build do frontend falha se `BTHS_INTERNAL_API_URL` estiver ausente ou se
  qualquer um dos dois argumentos apontar para `localhost`, `127.0.0.1`, `0.0.0.0` ou `[::1]`.

## Como validar no Compose

CORS (a origem do Compose deve retornar 401 e uma origem estranha, 403):

```powershell
$s = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest -Uri http://127.0.0.1:3000/api/auth/csrf -WebSession $s -UseBasicParsing | Out-Null
$t = ($s.Cookies.GetCookies("http://127.0.0.1:3000") | Where-Object Name -eq "XSRF-TOKEN").Value
foreach ($o in @("http://127.0.0.1:3000","https://site-malicioso.test")) {
  try { Invoke-WebRequest -Uri http://127.0.0.1:3000/api/auth/login -Method POST -WebSession $s -Headers @{"X-XSRF-TOKEN"=$t; "Origin"=$o} -ContentType "application/json" -Body '{"email":"inexistente@teste.invalid","senha":"errada"}' -UseBasicParsing | Out-Null } catch { "$o -> HTTP " + [int]$_.Exception.Response.StatusCode }
}
```

Trava do frontend (os dois builds devem falhar):

```powershell
docker build --build-arg BTHS_INTERNAL_API_URL=http://localhost:8080 ./frontend
docker build ./frontend
```

## Limitações

- `Secure=true` com HTTPS real só será homologado em staging (issues #75 e #76).
  O Compose roda em HTTP.
- O login completo com usuário cadastrado, pelo navegador, ainda não foi
  homologado na stack Compose.
- Possível endurecimento futuro: nome de cookie com prefixo `__Host-`, que
  obriga `Secure`, `Path=/` e ausência de `Domain`.