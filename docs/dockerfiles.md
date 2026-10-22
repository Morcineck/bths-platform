
# Docker - BTHS Platform

Issue: #65 - Dockerfiles do backend e frontend

## 1. Objetivo

Containerizar o backend Spring Boot e o frontend Next.js com builds multi-stage, execução sem root e configurações externas.

A orquestração entre serviços pertence à issue #66 (Docker Compose).

## 2. Imagens

| Componente | Build | Runtime | Porta |
|---|---|---|---|
| Backend | Maven + JDK 17 | Temurin JRE 17 | 8080 |
| Frontend | Node.js 22 | Node.js 22 standalone | 3000 |

Ambas as imagens utilizam usuários não-root.

- Backend: `bths` (UID/GID 999 no teste)
- Frontend: `nextjs` (UID/GID 1001 no teste)

## 3. Construção das imagens

Executar os comandos PowerShell na raiz do monorepo.

### Backend

```powershell
docker build -t bths-backend:issue-65 .\backend
```

O Dockerfile compila o JAR com Maven e copia o executável para a imagem final.

O build utiliza `-DskipTests`. Os testes automatizados devem ser executados separadamente.

### Frontend

```powershell
docker build -t bths-frontend:issue-65 .\frontend
```

O Dockerfile utiliza `npm ci`, compila o Next.js e copia os artefatos standalone para a imagem de execução.

O arquivo `next.config.ts` deve manter:

```typescript
output: "standalone"
```

Os `images.remotePatterns` existentes devem ser preservados.

## 4. Variáveis de ambiente

### Backend

| Variável | Finalidade |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil Spring Boot |
| `BTHS_DB_URL` | URL JDBC do banco |
| `DB_USERNAME` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave JWT em Base64 |
| `SPRING_FLYWAY_ENABLED` | Habilitação do Flyway |

O perfil `staging` utiliza Flyway e Hibernate `validate`.

Nunca conectar testes Docker ao MySQL local original.

### Frontend

`NEXT_PUBLIC_API_URL` é incorporada ao JavaScript durante o build.

O valor vazio produz chamadas relativas a `/api`, conforme a implementação atual.

```powershell
docker build --build-arg NEXT_PUBLIC_API_URL="" --build-arg BTHS_INTERNAL_API_URL="http://backend:8080" -t bths-frontend:issue-65 .\frontend
```

Para funcionar com URL relativa, o ambiente precisa encaminhar `/api` ao backend.

Esse reverse proxy ainda não foi implementado nesta issue.

Variáveis `NEXT_PUBLIC_*` não devem conter segredos.

`BTHS_INTERNAL_API_URL` é obrigatório no build (issue #69). O build falha sem ele
ou se qualquer um dos dois argumentos apontar para localhost.

## 5. Teste isolado do frontend

Executar sem rede externa e sem publicar portas:

```powershell
docker run -d --rm --network none --name bths-frontend-smoke bths-frontend:issue-65
```

Testar a página de login:

```powershell
docker exec bths-frontend-smoke node -e "fetch('http://127.0.0.1:3000/login').then(r => console.log('HTTP:', r.status)).catch(console.error)"
```

Resultado validado: HTTP 200.

Encerrar:

```powershell
docker stop bths-frontend-smoke
```

## 6. Teste isolado do backend

A execução do backend requer um datasource.

Na issue #65 foi utilizado MySQL 8.4 temporário, em rede Docker interna, com armazenamento `tmpfs`.

Procedimento de validação:

1. Criar uma rede Docker com `--internal`.
2. Iniciar MySQL temporário sem publicar a porta 3306.
3. Gerar credenciais e uma chave JWT temporárias.
4. Iniciar o backend com perfil `staging`.
5. Configurar explicitamente a URL JDBC para o banco temporário.
6. Verificar a aplicação das migrações Flyway.
7. Testar endpoints HTTP.
8. Encerrar e remover os containers e a rede.

A URL JDBC utilizada foi:

```text
jdbc:mysql://bths-issue65-mysql:3306/bths_issue65
```

A execução utilizou `SPRING_DATASOURCE_URL` e `BTHS_DB_URL` apontando para esse banco.


### Preparação do banco temporário

Executar no PowerShell, sem utilizar credenciais do ambiente original.

```powershell
# Criar rede isolada
docker network create --driver bridge --internal bths-issue65-isolated

# Gerar credenciais temporárias
$BthsTestRootPassword = [guid]::NewGuid().ToString("N")
$BthsTestAppPassword = [guid]::NewGuid().ToString("N")

# Gerar chave JWT temporária de 256 bits
$jwtBytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($jwtBytes)
$rng.Dispose()
$BthsTestJwtSecret = [Convert]::ToBase64String($jwtBytes)

# Iniciar MySQL descartável
docker run -d --rm `
  --name bths-issue65-mysql `
  --network bths-issue65-isolated `
  --mount "type=tmpfs,destination=/var/lib/mysql,tmpfs-size=1073741824" `
  -e "MYSQL_ROOT_PASSWORD=$BthsTestRootPassword" `
  -e "MYSQL_DATABASE=bths_issue65" `
  -e "MYSQL_USER=bths_test" `
  -e "MYSQL_PASSWORD=$BthsTestAppPassword" `
  mysql:8.4

# Conferir inicialização
docker logs --tail 40 bths-issue65-mysql
```

Aguardar a mensagem `ready for connections` antes de iniciar o backend.

Após os testes e a remoção do container do backend:

```powershell
docker stop bths-issue65-mysql
docker network rm bths-issue65-isolated
```

O armazenamento temporário será descartado ao encerrar o MySQL. Não há publicação de portas no host.

### Executar o backend no ambiente isolado

Pré-requisitos:
- Rede `bths-issue65-isolated` criada com `--internal`.
- Container `bths-issue65-mysql` iniciado nessa rede.
- Banco `bths_issue65` disponível.
- Variáveis temporárias `$BthsTestAppPassword` e `$BthsTestJwtSecret` previamente geradas no PowerShell.

Conferir as variáveis antes da execução:

```powershell
if ([string]::IsNullOrWhiteSpace($BthsTestAppPassword) -or
    [string]::IsNullOrWhiteSpace($BthsTestJwtSecret)) {
    throw "Credenciais temporárias ausentes."
}
```

Iniciar o backend:

```powershell
docker run -d `
  --name bths-issue65-backend `
  --network bths-issue65-isolated `
  -e "SPRING_PROFILES_ACTIVE=staging" `
  -e "BTHS_DB_URL=jdbc:mysql://bths-issue65-mysql:3306/bths_issue65" `
  -e "SPRING_DATASOURCE_URL=jdbc:mysql://bths-issue65-mysql:3306/bths_issue65" `
  -e "DB_USERNAME=bths_test" `
  -e "DB_PASSWORD=$BthsTestAppPassword" `
  -e "JWT_SECRET=$BthsTestJwtSecret" `
  -e "SPRING_FLYWAY_ENABLED=true" `
  -e "SPRING_JPA_HIBERNATE_DDL_AUTO=validate" `
  bths-backend:issue-65
```

Conferir a inicialização:

```powershell
docker logs --tail 120 bths-issue65-backend
```

Encerrar e remover o backend:

```powershell
docker stop bths-issue65-backend
docker rm bths-issue65-backend
```

Esses comandos pressupõem o ambiente temporário descrito anteriormente. Não devem ser executados com credenciais ou bancos de produção.

Não utilizar o valor padrão de datasource do `application.properties` durante testes de infraestrutura.

A receita completa de orquestração será consolidada na issue #66.

## 7. Resultados verificados

### Frontend

- Build Docker concluído: 16/16 etapas.
- Node.js 22.23.3.
- Usuário não-root confirmado.
- Next.js 16.3.5 inicializado.
- Rota `/login`: HTTP 200.

### Backend

- Imagem Docker construída com sucesso.
- Java Temurin 17.0.20.1.
- Usuário não-root confirmado.
- Spring Boot 4.1.0 inicializado.
- Tomcat na porta 8080.
- Conexão com MySQL 8.4.11 temporário.
- Flyway V1 e V2 executadas com sucesso.
- Hibernate inicializado com schema validado.
- `/api/auth/csrf`: HTTP 200.
- `/api/auth/me` sem autenticação: HTTP 401.

Os testes do backend foram realizados em banco temporário, não no MySQL original.

## 8. Segurança e limitações

- Os arquivos `.dockerignore` excluem dependências locais, builds e arquivos comuns de credenciais.
- Não embutir senhas ou JWT secrets nas imagens.
- Não utilizar valores reais de credenciais em comandos versionados.
- Em produção, utilizar gerenciamento seguro de segredos.
- As portas dos containers não foram publicadas durante os testes isolados.
- O frontend standalone foi validado, mas a integração completa com o backend ainda não foi testada.
- O reverse proxy de `/api` permanece pendente.
- CORS, cookies Secure, HTTPS e CSRF precisam de homologação antes da produção.
- Flyway ainda depende da homologação em staging real, prevista na issue #82.
- Os testes isolados não substituem os testes de integração e segurança em staging.

## 9. Próximas etapas

- Revisar os arquivos da issue #65.
- Executar verificações finais de lint e testes.
- Validar o diff do Git.
- Abrir PR para `develop`.
- Implementar Docker Compose na issue #66.
- Homologar o ambiente de staging e os requisitos de segurança antes da produção.
