# BTHS Platform - Observabilidade

## Objetivo

Permitir que a equipe investigue o que aconteceu em uma
requisição, a partir dos logs da aplicação, sem expor dados
sensíveis.

Issue: #68

## Logs estruturados

Nos perfis `staging` e `prod`, o backend escreve os logs no
console em JSON, no formato Elastic Common Schema (ECS).
Cada linha traz, entre outros campos:

- `@timestamp`, em UTC;
- `log.level` e `log.logger`;
- `process.thread.name`;
- `service.name` e `service.version`;
- `message`;
- `requestId`, quando a linha pertence a uma requisição.

No desenvolvimento local, sem esses perfis, o formato continua
em texto comum.

Nos perfis `staging` e `prod` as consultas SQL do Hibernate
não são impressas.

## Identificador de requisição

Toda requisição recebe um identificador, que:

- é lido do header `X-Request-Id`, se o valor for válido
  (letras, números, `-` e `_`, até 64 caracteres);
- caso contrário, é gerado um UUID;
- é gravado no MDC e aparece como `requestId` nos logs;
- é devolvido no header `X-Request-Id` da resposta;
- é removido do MDC ao fim da requisição, mesmo com falha.

O filtro executa antes da cadeia do Spring Security. Por isso as
respostas 401 e 403 também carregam o identificador.

## Linha de log por requisição

Cada requisição gera uma linha com método, caminho, status e
duração:

POST /api/auth/login -> 401 (293 ms)

Regras:

- status abaixo de 500 usa o nível INFO;
- status 500 ou superior, ou falha não tratada, usa o nível ERROR;
- os caminhos `/actuator/health` não geram linha, para não
  poluir o log com os healthchecks;
- a query string não é registrada;
- o código de check-in em `/api/check-in/qr/{codigo}` é
  substituído por `{codigo}`, pois é um passe pessoal.

## Falhas não tratadas

Uma exceção que escapa da aplicação é registrada em nível ERROR,
com a stack trace e o `requestId`. A resposta ao cliente não
muda e não expõe detalhes técnicos.

Não existe um handler genérico de `Exception` no
`GlobalExceptionHandler` de propósito: ele transformaria em 500
os erros de validação, JSON inválido e rota inexistente, que
hoje o Spring responde como 400, 404 e 405.

## Autenticação

O `JwtAuthenticationFilter` registra o motivo de uma requisição
não autenticada, sem nunca registrar o token:

| Situação | Nível | Conteúdo |
|---|---|---|
| Token expirado | INFO | Token JWT expirado. |
| Token adulterado, malformado ou de usuário inexistente | WARN | Apenas o tipo da exceção |
| Falha inesperada (por exemplo, banco indisponível) | ERROR | Mensagem e stack trace |

No login, uma falha interna de autenticação (por exemplo, banco
indisponível) é registrada em ERROR. Uma senha incorreta comum
não gera registro de erro. Em ambos os casos a resposta continua
sendo 401 com "E-mail ou senha inválidos!".

## Dados que não são registrados

- token JWT, cookies e headers;
- senhas e corpo das requisições;
- query string;
- código de check-in do QR Code.

- **Valores rejeitados em validações.** O log do Spring para erros de validação
  (`DefaultHandlerExceptionResolver`) inclui o valor enviado pelo cliente, o que
  expõe senha, CPF e e-mail. Esse logger está configurado em `ERROR` em staging e
  prod, então os 400 comuns aparecem apenas na linha por requisição
  (`POST /api/auth/login -> 400`).

Atenção: a mensagem de algumas exceções, principalmente de banco
de dados, pode conter valores de dados. Como logs de ERROR
incluem a stack trace, o acesso aos logs deve ser restrito.

## Retenção

No Docker Compose, cada serviço usa o driver `json-file` com
rotação: arquivos de até 10 MB, no máximo 5 por serviço.
Quando o limite é atingido, os registros mais antigos são
descartados. Ao recriar um container, seus logs anteriores
são perdidos.

Para guardar por mais tempo ou consultar vários containers em
conjunto, será necessário um coletor de logs, ainda não
adotado.

## Como consultar

Todos os comandos na raiz do repositório, em PowerShell.

Seguir os logs do backend:

```powershell
docker compose --env-file .env.compose -f compose.yaml logs -f --no-log-prefix backend
```

Buscar uma requisição pelo identificador:

```powershell
docker compose --env-file .env.compose -f compose.yaml logs --no-log-prefix backend | Select-String '"requestId":"ID-AQUI"'
```

Listar os erros:

```powershell
docker compose --env-file .env.compose -f compose.yaml logs --no-log-prefix backend | Select-String '"level":"ERROR"'
```

## Limitações conhecidas

- Não há coleta centralizada, métricas nem tracing distribuído.
- Token válido de usuário desativado não gera log.
- O login com falha de infraestrutura ainda responde 401.
- O header `X-Request-Id` só é legível pelo navegador em
  chamadas de mesma origem. O CORS atual não o expõe.
- O motivo detalhado de um 400 (qual campo falhou) não aparece nos logs, só na
  resposta HTTP. Para diagnosticar, use o `requestId` para localizar a requisição
  e reproduza com o payload de teste.