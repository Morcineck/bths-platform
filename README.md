# BTHS Platform

Plataforma web para gestão operacional e experiência do hóspede da Beat Trips.

O **BTHS Platform** nasceu de uma necessidade real da operação da Beat Trips: centralizar em um único sistema informações relacionadas às viagens, hóspedes, hospedagem, quartos, check-in, traslados e demais processos operacionais.

O projeto está evoluindo para uma aplicação **full-stack, mobile-first e PWA**, permitindo que a equipe administre a operação enquanto o hóspede acompanha sua experiência de viagem por meio da mesma plataforma.

---

## Visão geral

O BTHS Platform possui duas grandes áreas de atuação: **gestão operacional** e **experiência do hóspede**.

### Gestão operacional

A plataforma fornece à equipe da Beat Trips recursos para administrar:

* viagens;
* hóspedes;
* quartos e alocações;
* check-in;
* traslados;
* usuários e permissões;
* informações operacionais;
* indicadores por meio de dashboard.

### Experiência do hóspede

A área do hóspede será desenvolvida com foco em dispositivos móveis e permitirá consultar informações relacionadas à própria viagem, incluindo:

* identificação por QR Code;
* situação do check-in;
* hospedagem;
* quarto;
* chegada e traslado;
* informações da viagem;
* avisos e orientações.

A equipe administra a operação enquanto o hóspede utiliza a mesma plataforma para acompanhar sua experiência de viagem.

---

## Status do projeto

**Em desenvolvimento ativo.**

O backend da primeira versão operacional possui uma base funcional implementada e testada.

Atualmente estão disponíveis no backend:

* autenticação e autorização;
* gerenciamento de usuários;
* controle de acesso baseado em perfis;
* gerenciamento de viagens;
* gerenciamento de hóspedes;
* gerenciamento de quartos;
* alocação de hóspedes;
* check-in;
* identificação operacional por QR Code;
* gerenciamento de traslados;
* histórico operacional de status de traslado;
* dashboard operacional;
* tratamento centralizado de exceções;
* testes automatizados.

O repositório está sendo reorganizado para comportar backend, frontend e documentação do produto em uma estrutura full-stack.

A próxima grande etapa é o desenvolvimento do frontend utilizando Next.js, React e TypeScript.

---

## Perfis de acesso

O sistema possui três perfis de acesso.

### ADMIN

Perfil responsável pela administração da plataforma.

Possui acesso às funções administrativas, operacionais e ao gerenciamento de usuários.

### STAFF

Perfil destinado à equipe responsável pela execução da operação.

Seu acesso é concentrado nas atividades necessárias durante as viagens, incluindo hóspedes, check-in, quartos e traslados.

### HOSPEDE

Perfil destinado ao participante da viagem.

A interface é voltada para a experiência individual do hóspede, disponibilizando apenas informações e operações relacionadas à sua participação na viagem.

---

## Arquitetura

O BTHS Platform utiliza uma arquitetura baseada na separação entre frontend, API REST e banco de dados.

```text
                         BTHS PLATFORM
                               |
                 +-------------+-------------+
                 |                           |
                 v                           v
          ADMIN / STAFF                  HOSPEDE
                 |                           |
                 +-------------+-------------+
                               |
                               v
                    Next.js / React / PWA
                         FRONTEND
                               |
                           REST / HTTP
                               |
                               v
                   Java + Spring Boot
                          BACKEND
                               |
             +-----------------+-----------------+
             |                 |                 |
             v                 v                 v
        Segurança         Regras de         Persistência
                           negócio
             |                 |                 |
             +-----------------+-----------------+
                               |
                               v
                             MySQL
```

O frontend é responsável pela interface e experiência de utilização.

O backend permanece como autoridade sobre:

* autenticação;
* autorização;
* regras de negócio;
* validações;
* segurança;
* persistência;
* acesso aos dados.

Essa separação evita a duplicação de regras de negócio entre frontend e backend.

---

## Backend

O backend é uma API REST desenvolvida em Java com Spring Boot.

Principais responsabilidades:

* autenticar usuários;
* controlar permissões;
* executar regras de negócio;
* validar operações;
* acessar e persistir dados;
* disponibilizar recursos através da API REST;
* proteger os recursos da aplicação.

### Organização por domínio

A aplicação é organizada por domínios de negócio.

Entre os principais módulos estão:

```text
alocacao
checkin
dashboard
hospede
qrcode
quarto
security
traslado
usuario
viagem
```

Os domínios concentram seus respectivos componentes, como:

```text
controller
service
repository
dto
mapper
exception
```

O tratamento transversal das exceções da API é centralizado pelo `GlobalExceptionHandler`.

---

## Frontend

O frontend será desenvolvido como uma **Progressive Web App (PWA)** utilizando abordagem **mobile-first**.

Stack definida:

* Next.js;
* React;
* TypeScript;
* Tailwind CSS.

Será utilizada uma única aplicação frontend.

A interface, navegação e operações disponíveis serão adaptadas de acordo com o perfil do usuário autenticado.

### Área administrativa e operacional

A estrutura planejada inclui rotas como:

```text
/login
/dashboard
/hospedes
/hospedes/{id}
/check-in
/quartos
/quartos/{id}
/traslados
/traslados/{id}
/viagem
/usuarios
/usuarios/{id}
/minha-conta
```

### Área do hóspede

A área destinada ao hóspede deverá incluir:

```text
/app
/app/minha-viagem
/app/minha-hospedagem
/app/meu-traslado
/app/meu-qr
/app/avisos
/app/perfil
```

O frontend consumirá a API REST existente.

Regras de negócio, autenticação, autorização e persistência continuarão sob responsabilidade do backend.

---

## Segurança

A camada de segurança utiliza:

* Spring Security;
* JWT;
* BCrypt;
* autenticação por e-mail e senha;
* autorização baseada em perfil.

Perfis suportados:

```text
ADMIN
STAFF
HOSPEDE
```

Informações sensíveis não devem ser armazenadas diretamente no código-fonte.

Credenciais, tokens, chaves e configurações privadas são fornecidos externamente através de variáveis de ambiente.

Um modelo das variáveis necessárias ao backend está disponível em:

```text
backend/.env.example
```

Entre as configurações utilizadas estão:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

---

## Testes

O backend possui testes automatizados cobrindo diferentes camadas da aplicação.

Tecnologias utilizadas:

* JUnit;
* Mockito;
* Spring Test.

A suíte de testes contempla componentes relacionados a:

* controllers;
* services;
* repositories;
* segurança;
* validações;
* regras de negócio.

A reorganização estrutural do backend foi validada executando novamente a suíte de testes a partir do diretório `backend/`, mantendo o build da aplicação com sucesso.

---

## Integração contínua e qualidade

O projeto utiliza GitHub Actions para automação do processo de build e análise.

Após a reorganização estrutural do repositório, o workflow do backend passou a executar o Maven a partir de:

```text
backend/
```

O pipeline utiliza Java 17.

O projeto também possui configuração de integração com SonarQube/SonarCloud para análise estática e acompanhamento da qualidade do código.

---

## Tecnologias

### Backend

* Java 17
* Spring Boot 4
* Spring Web
* Spring Data JPA
* Spring Security
* Bean Validation
* JWT
* Hibernate
* Maven

### Frontend

* Next.js
* React
* TypeScript
* Tailwind CSS
* PWA

### Banco de dados

* MySQL

### Testes

* JUnit
* Mockito
* Spring Test

### Desenvolvimento e qualidade

* Git
* GitHub
* GitHub Actions
* SonarQube / SonarCloud

### Tecnologias planejadas

* Swagger / OpenAPI
* Docker
* Docker Compose

---

## Estrutura do projeto

O BTHS Platform utiliza um único repositório para organizar backend, frontend e documentação.

```text
bths-platform/
|
+-- backend/
|   +-- .mvn/
|   +-- src/
|   |   +-- main/
|   |   +-- test/
|   +-- .env.example
|   +-- mvnw
|   +-- mvnw.cmd
|   +-- pom.xml
|
+-- frontend/
|   +-- # Next.js / React / TypeScript
|
+-- docs/
|   +-- arquitetura.md
|   +-- regras-negocio.md
|   +-- roadmap.md
|
+-- .github/
|   +-- workflows/
|
+-- .gitignore
+-- CHANGELOG.md
+-- README.md
```

O backend e o frontend pertencem ao mesmo produto, mas são aplicações independentes em execução.

---

## Executando o backend

### Pré-requisitos

Para executar o backend localmente é necessário possuir:

* Java 17;
* MySQL;
* Git.

O Maven Wrapper está incluído no projeto, portanto não é necessário instalar o Maven separadamente.

### Acessar o backend

A partir da raiz do repositório:

```bash
cd backend
```

### Configurar o ambiente

Utilize como referência:

```text
.env.example
```

As principais configurações necessárias são:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

O banco de dados utilizado pela configuração atual é:

```text
bths_platform
```

### Executar no Windows

```powershell
.\mvnw spring-boot:run
```

### Executar no Linux ou macOS

```bash
./mvnw spring-boot:run
```

---

## Executando os testes

Os comandos devem ser executados dentro do diretório `backend/`.

### Windows

```powershell
.\mvnw clean test
```

### Linux ou macOS

```bash
./mvnw clean test
```

Um build bem-sucedido deve finalizar com:

```text
BUILD SUCCESS
```

---

## Roadmap

O roadmap abaixo apresenta uma visão resumida do estágio atual do projeto.

### Fundação do backend

* [x] Criar projeto Spring Boot
* [x] Configurar MySQL
* [x] Estruturar API REST
* [x] Organizar aplicação por domínio
* [x] Implementar tratamento global de exceções
* [x] Implementar testes automatizados

### Segurança e usuários

* [x] Implementar Spring Security
* [x] Implementar JWT
* [x] Implementar BCrypt
* [x] Criar gerenciamento de usuários
* [x] Criar perfis `ADMIN`, `STAFF` e `HOSPEDE`
* [x] Implementar controle de acesso por perfil

### Viagens e hóspedes

* [x] Implementar gerenciamento de viagens
* [x] Implementar gerenciamento de hóspedes
* [x] Relacionar hóspedes à operação da viagem

### Hospedagem e check-in

* [x] Implementar gerenciamento de quartos
* [x] Implementar controle de capacidade
* [x] Implementar alocação de hóspedes
* [x] Implementar troca de quarto
* [x] Implementar check-in
* [x] Implementar identificação operacional por QR Code

### Traslados

* [x] Implementar gerenciamento de traslados
* [x] Implementar controle de status
* [x] Implementar histórico de alterações de status
* [x] Implementar validações operacionais

### Dashboard

* [x] Implementar indicadores de hóspedes
* [x] Implementar indicadores de hospedagem
* [x] Implementar indicadores de traslados
* [x] Implementar consulta de próximos traslados

### Frontend

* [ ] Criar aplicação Next.js
* [ ] Configurar TypeScript
* [ ] Configurar Tailwind CSS
* [ ] Configurar estrutura PWA
* [ ] Implementar identidade visual
* [ ] Implementar login
* [ ] Integrar autenticação com backend
* [ ] Implementar navegação baseada em perfil
* [ ] Criar dashboard para `ADMIN` e `STAFF`
* [ ] Criar fluxos operacionais
* [ ] Criar área do hóspede
* [ ] Implementar experiência mobile-first

### Próximas evoluções

* [ ] Documentar API com Swagger / OpenAPI
* [ ] Adicionar Docker
* [ ] Adicionar Docker Compose
* [ ] Ampliar CI/CD para o frontend
* [ ] Evoluir gerenciamento de motoristas e veículos
* [ ] Implementar notificações
* [ ] Integrar serviços de mapas
* [ ] Implementar relatórios
* [ ] Expandir métricas operacionais
* [ ] Implementar push notifications

---

## Documentação

A documentação técnica e funcional do projeto está organizada no diretório:

```text
docs/
```

Principais documentos:

* [Arquitetura](./docs/arquitetura.md)
* [Regras de negócio](./docs/regras-negocio.md)
* [Roadmap](./docs/roadmap.md)

O `README.md` fornece uma visão geral do produto e do estado do desenvolvimento.

Detalhes de arquitetura, decisões técnicas e regras de negócio devem permanecer nos documentos específicos do projeto.

---

## Contexto do projeto

O BTHS Platform está sendo desenvolvido inicialmente para atender às necessidades operacionais da **Beat Trips**, empresa voltada à organização de experiências de viagem relacionadas a grandes eventos e festivais de música eletrônica.

O projeto surgiu a partir de processos e necessidades reais da operação.

Seu objetivo é substituir informações dispersas entre diferentes meios por uma plataforma centralizada, permitindo maior organização operacional e uma experiência mais simples para os hóspedes.

Além de atender a uma necessidade real de negócio, o desenvolvimento do BTHS Platform também é utilizado para aplicação prática e evolução de conhecimentos relacionados a:

* desenvolvimento backend com Java;
* Spring Boot;
* APIs REST;
* segurança de aplicações;
* bancos de dados;
* testes automatizados;
* arquitetura de software;
* desenvolvimento frontend;
* React e Next.js;
* TypeScript;
* PWA;
* Git e GitHub;
* integração contínua;
* qualidade de software.

---

## Licença

O projeto encontra-se em desenvolvimento.

A licença de distribuição e utilização será definida posteriormente.

---

**BTHS Platform**

*Tecnologia para organizar experiências.*
