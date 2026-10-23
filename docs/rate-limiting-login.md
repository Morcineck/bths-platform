# Limite de tentativas de login

Documento da issue #72. Descreve como o `POST /api/auth/login` se protege contra força bruta.

## Regra

- A chave do limite é o **e-mail normalizado** (sem espaços nas pontas e em minúsculas).
- Cada falha de credencial soma na janela de 15 minutos. Na 5ª falha, o e-mail fica bloqueado por 5 minutos.
- Na reincidência o bloqueio dobra (5, 10, 20, 40 minutos...) até o teto de 1 hora.
- A reincidência é esquecida depois que passa 1 hora (o teto) do fim do último bloqueio.
- Falha durante um bloqueio não estende o bloqueio. Login com sucesso zera a contagem do e-mail.
- Falha de infraestrutura (por exemplo, banco fora do ar) **não conta** como falha do usuário.
- Com o e-mail bloqueado, o login responde `429 Too Many Requests` com o header `Retry-After` (segundos) e **não consulta a senha**, nem a correta.

## Não vaza a existência do usuário

E-mail inexistente e senha errada já respondem igual (`401`, "E-mail ou senha inválidos!") e contam do mesmo jeito. A resposta `429` também é idêntica, exista ou não a conta. O `LoginRateLimitingTest` confere isso.

## Propriedades

| Propriedade | Padrão | Significado |
|---|---|---|
| `bths.auth.limite-login.maximo-falhas` | `5` | falhas na janela que provocam o bloqueio |
| `bths.auth.limite-login.janela` | `15m` | janela de contagem das falhas |
| `bths.auth.limite-login.bloqueio-inicial` | `5m` | duração do primeiro bloqueio |
| `bths.auth.limite-login.bloqueio-maximo` | `1h` | teto do bloqueio progressivo |

A aplicação não inicia com valores inválidos (falhas menor que 1, durações zero ou negativas, teto menor que o bloqueio inicial). Por variável de ambiente, o Spring aceita, por exemplo, `BTHS_AUTH_LIMITELOGIN_MAXIMOFALHAS=5`.

## Auditoria

Quando uma falha provoca um bloqueio, o `AuthService` registra um `WARN`:

```text
Login bloqueado por excesso de tentativas falhas: conta=a***@beattrips.com, bloqueioSegundos=300.
```

- A conta aparece mascarada; o e-mail completo e a senha nunca vão para o log.
- Cada tentativa falha e cada resposta `429` já geram a linha de acesso do `RequestIdFilter` (`docs/observabilidade.md`), com o `requestId`. Não há log extra por tentativa, para não inundar o log durante um ataque.

## Limitações conhecidas

- **Estado em memória.** Reiniciar a aplicação zera as contagens, e o limite não é compartilhado entre instâncias. Hoje o BTHS roda em uma instância. Com mais de uma, o estado deve ir para um armazenamento compartilhado (por exemplo, o MySQL).
- **Bloqueio por e-mail.** Quem souber o e-mail de uma conta pode bloqueá-la por alguns minutos errando a senha de propósito. É o custo de limitar por e-mail; o teto de 1 hora limita o efeito.
- **Sem limite por IP.** Atrás do proxy do Next, o backend vê o IP do container do frontend, não o do cliente. Limitar por IP agora bloquearia todos juntos. O limite por IP entra depois que o proxy real encaminhar o IP do cliente de forma confiável (issue #75, `X-Forwarded-For` com proxy confiável).
- **Só o login.** Outros endpoints não têm limite nesta issue.
- **Frontend.** O formulário de login ainda mostra a mensagem genérica para o `429`, sem informar o tempo de espera.

## Como validar manualmente

Com a stack no ar, em PowerShell, seis tentativas com senha errada para o mesmo e-mail (a 6ª deve voltar `429`):

```powershell
$sessao = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest -Uri "http://127.0.0.1:3000/api/auth/csrf" -WebSession $sessao -UseBasicParsing | Out-Null
$xsrf = ($sessao.Cookies.GetCookies("http://127.0.0.1:3000") | Where-Object Name -eq "XSRF-TOKEN").Value
1..6 | ForEach-Object {
    try {
        $r = Invoke-WebRequest -Uri "http://127.0.0.1:3000/api/auth/login" -Method POST -WebSession $sessao `
            -Headers @{ "X-XSRF-TOKEN" = $xsrf } -ContentType "application/json" `
            -Body '{"email":"teste-limite@exemplo.com","senha":"errada"}' -UseBasicParsing
        "$_ -> $($r.StatusCode)"
    } catch {
        "$_ -> $($_.Exception.Response.StatusCode.value__)"
    }
}
```