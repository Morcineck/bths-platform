
# BTHS Platform - Health Checks

## Objetivo

Implementar verificações de saúde para os serviços do
BTHS Platform, permitindo que o Docker Compose identifique
quando cada serviço está disponível.

Issue: #67

## Serviços monitorados

| Serviço | Verificação |
|---|---|
| PostgreSQL 17 | Consulta SQL SELECT 1 com o usuário da aplicação |
| Spring Boot 4.1 | Spring Boot Actuator |
| Next.js 16 | Endpoint GET /health |

## Backend - Spring Boot Actuator

Dependência utilizada:
spring-boot-starter-actuator

Endpoints disponíveis:

- GET /actuator/health
- GET /actuator/health/liveness
- GET /actuator/health/readiness

Liveness verifica o estado de funcionamento da aplicação.

Readiness verifica se a aplicação está pronta para
receber requisições, incluindo a disponibilidade do banco.

As respostas públicas não exibem detalhes internos
dos componentes.

O Spring Security permite requisições GET aos endpoints
de saúde, mantendo as demais regras de autenticação.

## Frontend - Next.js

Endpoint:

GET /health

Resposta esperada:

{"status":"UP"}

O endpoint não exige autenticação e utiliza
Cache-Control: no-store.

A verificação confirma que o servidor Next.js consegue
responder a requisições HTTP.

## Docker Compose

A sequência de inicialização é:

1. PostgreSQL atinge o estado healthy.
2. Backend atinge o estado healthy.
3. Frontend é iniciado.
4. Frontend atinge o estado healthy.

O backend utiliza o endpoint de readiness.

O frontend utiliza Node.js para consultar /health,
sem instalar dependências adicionais na imagem.

## Verificação operacional

Executar na raiz do repositório:

```powershell
docker compose --env-file .env.compose -f compose.yaml ps
```

Resultado esperado:

- postgres: healthy
- backend: healthy
- frontend: healthy

## Validações realizadas

- Health Checks do backend aprovados com MockMvc.
- Respostas públicas não revelam detalhes internos.
- Autenticação das demais rotas preservada.
- Frontend /health respondeu HTTP 200.
- Cache-Control: no-store confirmado.
- Banco, backend e frontend atingiram healthy (MySQL na validação original; PostgreSQL revalidado em 25/11/2026, #172).
- Teste negativo do comando de Health Check do
  frontend retornou código de saída 1.
- Teste positivo do frontend operacional retornou 0.
- Regressão backend: 551 testes aprovados.
- Build frontend aprovado.
- ESLint frontend aprovado.

## Limitações

- O estado unhealthy não provoca automaticamente
  o reinício de um container ainda em execução.
- A dependência service_healthy controla a ordem
  de inicialização, não a recuperação automática
  de serviços dependentes.
- Os testes não simulam uma falha real do banco.
- O Health Check não substitui observabilidade,
  logs, métricas ou alertas.
- Homologação em staging real com HTTPS permanece pendente.

## Segurança

- Não expor detalhes internos do Actuator publicamente.
- Não incluir credenciais nas respostas dos endpoints.
- Manter os arquivos de ambiente fora do Git.
- Não alterar o banco local de desenvolvimento localhost:5433/bths_platform.

## Referências

- Issue #66 - Docker Compose
- Issue #67 - Health Checks
- Issue #82 - Flyway em staging
- docs/docker-compose.md
