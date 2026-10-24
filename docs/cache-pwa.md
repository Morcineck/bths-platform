# Cache e PWA

Issue #74. Regras para que dados privados não fiquem em cache, no navegador ou em proxies, e contrato de segurança que o futuro PWA deverá seguir.

O BTHS será um PWA (app do hóspede instalável). Nesta issue ficaram definidas as regras e feita a preparação. O manifest, os ícones e o service worker **não** foram criados: ficam para uma issue própria, porque service workers só funcionam em HTTPS e a validação real depende do staging (issue #75).

## O que já está em vigor

| Recurso | Cache-Control | Origem |
|---|---|---|
| Páginas (HTML) do frontend | `private, no-store` | `frontend/next.config.ts` |
| Redirecionamentos para `/login` | `private, no-store` | mesma regra do `next.config.ts` |
| Arquivos de `/_next/static` | `public, max-age=31536000, immutable` | Next.js (nome com hash, nunca muda) |
| API (`/api/**`) | `no-cache, no-store, max-age=0, must-revalidate` | Spring Security |
| QR Code do hóspede (`GET /api/app/check-in/qr`) | `no-store` (padrão da API) | Spring Security, coberto por teste |

Antes desta issue, as páginas saíam com `s-maxage=31536000`, ou seja, um cache compartilhado poderia guardá-las por um ano.

Regras:
- A regra de páginas exclui `/api`, `/_next` e qualquer caminho com extensão de arquivo (ícones, `favicon.ico`). Esses recursos são públicos e podem ter cache.
- O `no-store` nas páginas tira a página da navegação voltar/avançar em cache (bfcache). Em troca, o botão "voltar" refaz a requisição, o que também garante que a tela volte para o login depois do logout.
- As páginas não contêm dados de hóspedes no HTML. Os dados vêm da API, depois que a página carrega, e a API já é `no-store`.

## Rotas protegidas

O `frontend/src/proxy.ts` redireciona para `/login` quem não tem o cookie `BTHS_TOKEN`. Ele protege: `/dashboard`, `/hospedes`, `/motoristas`, `/veiculos`, `/avisos`, `/traslados`, `/agenda-viagem`, `/check-in`, `/hospedagens`, `/quartos` e `/app`.

Antes desta issue, o `matcher` apontava para `/motorista` (rota que não existe) e deixava de fora `avisos`, `traslados`, `agenda-viagem`, `check-in`, `hospedagens` e `quartos`.

Regra para o futuro: **toda rota autenticada nova precisa entrar no `matcher`**. O proxy só confere a presença do cookie. Quem valida o JWT e o perfil é o backend.

## Logout

`POST /api/auth/logout` responde:

```text
204 No Content
Set-Cookie: BTHS_TOKEN=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax
Clear-Site-Data: "cache", "storage"
```

- `"cache"` apaga o cache HTTP do site.
- `"storage"` apaga localStorage, sessionStorage, IndexedDB e remove os service workers registrados.
- Os cookies **não** entram no `Clear-Site-Data`: o `BTHS_TOKEN` é removido pelo `Set-Cookie`.
- Os navegadores só obedecem em contexto seguro (HTTPS ou `localhost`).

Objetivo: em aparelho compartilhado, nada da sessão anterior sobra depois de sair.

## Contrato para o futuro PWA (service worker)

Quando a issue do PWA for feita, o service worker deve seguir estas regras:

1. **Pode guardar em cache apenas:** arquivos estáticos (`/_next/static/**`), ícones e uma página offline genérica, sem dados de usuário.
2. **Nunca guardar em cache:**
    - qualquer rota `/api/**` (inclui `/api/auth/*` e o QR Code);
    - páginas autenticadas com dados;
    - respostas com `Set-Cookie` ou que tragam o JWT.
3. **Não ler nem copiar o JWT.** O token continua só no cookie `HttpOnly`. Nada de localStorage, sessionStorage ou IndexedDB para token, dados de hóspede ou QR Code.
4. **Dados offline:** se uma tela precisar funcionar offline, isso exige decisão e issue próprias, com análise do que pode ficar no aparelho e por quanto tempo. Até lá, o modo offline mostra só a página genérica.
5. **Logout:** o `Clear-Site-Data` já remove o service worker e seus caches. O service worker não deve tentar impedir isso.
6. **Estratégia de cache:** para arquivos estáticos, cache primeiro com os nomes com hash do Next. Para navegação, sempre rede primeiro, caindo na página offline só se a rede falhar.
7. **Escopo:** o service worker é registrado na raiz (`/`) e precisa ser servido com `Cache-Control: no-cache`, para as atualizações chegarem.
8. A CSP já prevê `worker-src 'self' blob:`. Conferir ao ativar a política (ver `headers-seguranca.md`).

## Como conferir

Medição de headers (Compose em `http://127.0.0.1:3000`):

```powershell
$base = "http://127.0.0.1:3000"
foreach ($caminho in "/login","/avisos","/quartos","/dashboard","/app") {
  try {
    $r = Invoke-WebRequest -Uri "$base$caminho" -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop
    $status = $r.StatusCode; $h = $r.Headers
  } catch {
    $r = $_.Exception.Response
    $status = [int]$r.StatusCode; $h = $r.Headers
  }
  "$caminho -> $status | Cache-Control: $($h['Cache-Control'])"
}
```

Esperado: todas com `private, no-store`. Sem cookie, só `/login` responde `200`. As demais respondem `307` para `/login`.

Logout (o header CSRF usa o valor do **cookie** `XSRF-TOKEN`, e não o campo `token` do corpo de `/api/auth/csrf`, por causa do `csrf.spa()`):

```powershell
$sessao = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-RestMethod -Uri "$base/api/auth/csrf" -WebSession $sessao | Out-Null
$cookieCsrf = $sessao.Cookies.GetCookies($base)["XSRF-TOKEN"].Value
$r = Invoke-WebRequest -Uri "$base/api/auth/logout" -Method Post -WebSession $sessao -UseBasicParsing -Headers @{ "X-XSRF-TOKEN" = $cookieCsrf }
$r.Headers['Clear-Site-Data']
```

Testes automatizados:
- `AuthControllerTest`: o logout envia `Clear-Site-Data: "cache", "storage"`.
- `AppHospedeAuthorizationTest`: o QR Code do hóspede responde com `no-store`.

## Limitações conhecidas

- **Service worker não existe ainda.** O contrato acima não foi exercitado na prática. Precisa de HTTPS (staging, issue #75) e de uma issue de PWA.
- **`Clear-Site-Data` não foi conferido em um navegador real.** Foi validado pelo caminho HTTP completo (frontend → rewrite → backend) e por teste, mas não observando o navegador limpar o armazenamento. Fazer isso junto com a validação em HTTPS no staging.
- **Cookie sem `Secure` no Compose local**, por ser HTTP. Em staging/produção o `Secure` é obrigatório (ver `configuracao-producao.md`).
- **CDN ou proxy futuros:** os headers `private, no-store` já impedem cache compartilhado, mas a configuração do proxy real (issue #75) deve ser conferida para garantir que ele respeita o `Cache-Control` da origem.
- **bfcache desativado** nas páginas privadas, por consequência do `no-store`. É o comportamento desejado por segurança.