# Arquitetura - BTHS Platform

## 1. Visão geral

O **BTHS Platform** é uma plataforma full-stack desenvolvida para centralizar a operação das viagens da Beat Trips e oferecer uma experiência digital aos hóspedes.

O sistema é composto por:

* backend REST desenvolvido em Java e Spring Boot;
* frontend web mobile-first desenvolvido com Next.js;
* banco de dados relacional MySQL;
* autenticação e autorização baseadas em Spring Security e JWT;
* uma única aplicação frontend com experiências adaptadas aos perfis ADMIN, STAFF e HOSPEDE.

A arquitetura prioriza simplicidade, separação de responsabilidades e evolução incremental.

O backend permanece como autoridade sobre:

* autenticação;
* autorização;
* regras de negócio;
* validações;
* persistência;
* integridade dos dados.

O frontend é responsável pela experiência de uso e pelo consumo dos recursos disponibilizados pelo backend.

---

# 2. Arquitetura geral

A arquitetura atual e planejada do produto pode ser representada da seguinte forma:

```text
                         BTHS PLATFORM
                               |
                +--------------+--------------+
                |                             |
                v                             v
         FRONTEND PWA                    BACKEND REST
           Next.js                       Spring Boot
           React                             |
         TypeScript                          |
       Tailwind CSS                          |
                |                            |
                +-------- HTTP/JSON ---------+
                                             |
                                   Spring Security + JWT
                                             |
                                             v
                                      Controllers
                                             |
                                             v
                                        Services
                                             |
                                             v
                                      Repositories
                                             |
                                             v
                                           MySQL
```

A aplicação é desenvolvida como um produto full-stack, mantendo frontend e backend separados por responsabilidade, mas versionados no mesmo repositório.

---

# 3. Estrutura do repositório

A organização do repositório segue a estrutura:

```text
bths-platform/
|
├── backend/
│   ├── .mvn/
│   ├── src/
│   ├── .env.example
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   └── ...
│
├── docs/
│
├── .github/
│
├── .gitignore
├── CHANGELOG.md
└── README.md
```

O diretório `backend/` contém exclusivamente a aplicação Spring Boot.

O diretório `frontend/` contém a aplicação Next.js.

O diretório `docs/` centraliza documentação técnica e funcional do projeto.

O diretório `.github/` concentra configurações relacionadas ao GitHub e aos workflows de integração contínua.

Arquivos de documentação geral do produto permanecem na raiz.

---

# 4. Backend

## 4.1 Stack

O backend utiliza atualmente:

* Java 17;
* Spring Boot 4.1.0;
* Spring Web;
* Spring Data JPA;
* Spring Security;
* JWT;
* Bean Validation;
* Hibernate;
* MySQL;
* Lombok;
* Maven;
* JUnit;
* Mockito.

O Maven Wrapper é utilizado para manter consistência na execução do projeto independentemente da instalação global do Maven.

---

# 5. Organização do backend

O backend utiliza uma organização orientada por domínio.

Estrutura conceitual:

```text
com.bths.platform
|
├── alocacao/
├── checkin/
├── dashboard/
├── handler/
├── hospede/
├── qrcode/
├── quarto/
├── security/
├── traslado/
├── usuario/
└── viagem/
```

Cada domínio pode conter seus próprios:

```text
Controller
Service
Repository
Entity
DTO
Mapper
Enum
Exception
```

conforme a necessidade.

Essa organização evita concentrar todos os controllers, services ou repositories da aplicação em pacotes globais e facilita a localização do código relacionado a cada contexto funcional.

---

# 6. Camadas do backend

Embora organizado por domínio, o backend mantém separação clara de responsabilidades.

## 6.1 Controller

O Controller representa a camada HTTP da aplicação.

Responsabilidades:

* receber requisições;
* receber parâmetros e payloads;
* delegar operações para Services;
* retornar respostas HTTP;
* definir os endpoints da API.

O Controller não deve concentrar regras complexas de negócio.

Fluxo típico:

```text
HTTP Request
     |
     v
Controller
     |
     v
Service
```

---

## 6.2 Service

O Service concentra a lógica de aplicação e as principais regras de negócio.

Responsabilidades:

* validar condições do domínio;
* coordenar operações;
* consultar repositories;
* aplicar regras de consistência;
* lançar exceções de negócio;
* preparar operações de persistência.

Exemplo:

```text
AlocacaoService
      |
      +--> HospedeRepository
      |
      +--> QuartoRepository
      |
      +--> AlocacaoQuartoRepository
```

Um Service pode utilizar múltiplos repositories quando uma regra atravessa diferentes entidades.

---

## 6.3 Repository

Repositories são responsáveis pelo acesso aos dados persistidos.

São implementados utilizando Spring Data JPA.

Responsabilidades:

* consultas;
* persistência;
* verificações de existência;
* contagens;
* filtros;
* consultas específicas do domínio.

Exemplo:

```text
Service
   |
   v
Repository
   |
   v
JPA / Hibernate
   |
   v
MySQL
```

---

## 6.4 Entity

Entities representam objetos persistidos no banco de dados.

As entidades refletem o modelo atual necessário à operação.

O projeto não deve criar entidades apenas porque determinado conceito existe no mundo real.

Um conceito somente deve se tornar entidade independente quando houver necessidade funcional ou de persistência que justifique essa separação.

---

## 6.5 DTO

DTOs controlam os dados de entrada e saída da API.

O backend não deve depender da exposição direta das entidades persistidas para comunicação com clientes.

O projeto utiliza DTOs específicos conforme o contexto, como:

```text
Request
Response
UpdateRequest
StatusRequest
```

Essa separação permite que a API evolua sem acoplar diretamente seu contrato HTTP ao modelo de persistência.

---

## 6.6 Mapper

Mappers realizam a conversão entre:

```text
Entity <-> DTO
```

Essa responsabilidade é mantida fora dos Controllers para reduzir acoplamento e duplicação.

---

## 6.7 Exceptions

As exceções de negócio são organizadas junto aos domínios aos quais pertencem.

Exemplo:

```text
hospede/
└── exception/
    ├── HospedeJaCadastradoException
    └── HospedeNaoEncontradoException

quarto/
└── exception/
    ├── QuartoIndisponivelException
    ├── QuartoLotadoException
    └── QuartoNaoEncontradoException

traslado/
└── exception/
    ├── AeroportoObrigatorioException
    ├── MotivoCorrecaoObrigatorioException
    ├── TransicaoStatusTrasladoInvalidaException
    └── TrasladoNaoEncontradoException
```

Exceções transversais são tratadas centralmente por:

```text
handler/
└── GlobalExceptionHandler
```

Essa estrutura mantém a origem semântica da exceção próxima ao domínio e centraliza sua tradução para respostas HTTP.

---

# 7. Domínios atuais

A arquitetura atual do backend possui os seguintes contextos principais:

```text
                       VIAGEM
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
       HOSPEDE          QUARTO         TRASLADO
          |               |
          +-------+-------+
                  |
                  v
               ALOCACAO
                  |
                  v
               CHECK-IN
                  |
                  v
                QR CODE

          +-----------------------+
          |                       |
          v                       v
       USUARIO                DASHBOARD
          |
          v
       SECURITY
```

O diagrama é conceitual e não representa necessariamente relacionamentos físicos de banco de dados.

---

# 8. Viagem como contexto operacional

A `Viagem` funciona como um dos principais elementos agregadores da operação.

Hóspedes, quartos, alocações e traslados são relacionados à viagem correspondente.

Essa associação permite separar operações de eventos ou edições diferentes.

Exemplo:

```text
Viagem
|
├── Hospedes
├── Quartos
│   └── Alocacoes
├── Traslados
└── Dashboard
```

A arquitetura deve impedir associações incompatíveis entre objetos pertencentes a viagens diferentes.

As regras específicas estão documentadas em `regras-negocio.md`.

---

# 9. Hospedagem

Na arquitetura atual, hospedagem não é um domínio persistido independente.

A necessidade operacional da V1 é atendida principalmente por:

```text
Viagem
   |
   v
Quartos
   |
   v
Alocacoes
   |
   v
Hospedes
```

O `Quarto` possui capacidade e estado operacional.

A `AlocacaoQuarto` representa a associação do hóspede ao quarto.

Não existem atualmente entidades independentes para:

```text
Hospedagem
Cama
Reserva
```

Essa decisão reduz complexidade enquanto esses conceitos não forem necessários como objetos independentes de negócio.

Caso a operação futura exija múltiplas propriedades, controle individual de camas ou reservas independentes, o modelo poderá evoluir.

---

# 10. Check-in e QR Code

Check-in e QR Code são contextos relacionados, mas possuem responsabilidades diferentes.

## 10.1 QR Code

O QR Code é utilizado como mecanismo de identificação operacional.

Fluxo conceitual:

```text
Hospede cadastrado
       |
       v
codigoCheckIn
       |
       v
QR Code
       |
       v
Identificacao do hospede
```

O código é gerado para o hóspede e utilizado para recuperar sua identificação operacional.

---

## 10.2 Check-in

O check-in registra a chegada do hóspede.

Fluxo:

```text
Identificacao do hospede
          |
          v
Verificacao de alocacao
          |
          v
Validacao do status
          |
          v
Registro do check-in
```

O backend permanece responsável por decidir se o check-in pode ser realizado.

---

# 11. Traslados

O domínio `traslado` representa deslocamentos relacionados aos hóspedes.

Estrutura conceitual atual:

```text
Viagem
   |
   v
Traslado
   |
   +--> Hospede
   |
   +--> Tipo
   |
   +--> Aeroporto
   |
   +--> Data/hora prevista
   |
   +--> Origem
   |
   +--> Destino
   |
   +--> Status
   |
   └--> Historico de status
```

O domínio possui controle de transições de status e histórico das alterações.

Motoristas e veículos não são domínios implementados atualmente.

Eles poderão ser adicionados posteriormente caso a operação necessite de gerenciamento estruturado desses recursos.

---

# 12. Dashboard

O Dashboard é uma camada de leitura agregada da operação.

Ele não substitui os domínios que fornecem os dados.

Sua função é consolidar informações existentes.

Estrutura conceitual:

```text
                  DashboardService
                         |
        +----------------+----------------+
        |                |                |
        v                v                v
 HospedeRepository  QuartoRepository  TrasladoRepository
        |                |
        |         AlocacaoRepository
        |
        +----------------+----------------+
                         |
                         v
                 DashboardResponse
```

Atualmente o dashboard consolida:

* situação dos hóspedes e check-ins;
* ocupação da hospedagem;
* situação dos traslados;
* próximos traslados.

---

# 13. Usuários e segurança

Usuário e hóspede representam conceitos diferentes.

Um `Hospede` representa uma pessoa participante de uma viagem.

Um `Usuario` representa uma identidade autenticável no sistema.

Essa separação permite que a plataforma evolua sem acoplar diretamente os dados operacionais do hóspede ao mecanismo de autenticação.

---

# 14. Spring Security

A segurança já faz parte da arquitetura atual.

O backend utiliza:

* Spring Security;
* autenticação por e-mail e senha;
* BCrypt;
* JWT;
* filtro JWT;
* autorização baseada em roles;
* respostas específicas para falha de autenticação e acesso negado;
* sessão stateless.

Fluxo simplificado:

```text
Login
  |
  v
AuthController
  |
  v
AuthService
  |
  v
AuthenticationManager
  |
  v
JWT
```

Para requisições autenticadas:

```text
HTTP Request
     |
     v
JwtAuthenticationFilter
     |
     v
Spring Security
     |
     +--> autorizado --> Controller
     |
     └--> rejeitado
```

---

# 15. Perfis e autorização

Os perfis da aplicação são:

```text
ADMIN
STAFF
HOSPEDE
```

Na configuração atual do backend:

```text
/api/auth/**
```

é público.

```text
/api/usuarios/**
```

é restrito ao ADMIN.

Os principais endpoints operacionais são acessíveis por:

```text
ADMIN
STAFF
```

A autorização específica para a futura área do HOSPEDE será desenvolvida conforme forem criados os endpoints destinados à experiência do participante.

A interface não substitui essa proteção.

Mesmo que um recurso esteja oculto no frontend, o backend deve validar a autorização correspondente.

---

# 16. Frontend

O frontend do BTHS Platform será desenvolvido como uma aplicação web mobile-first.

Stack definida:

* Next.js;
* React;
* TypeScript;
* Tailwind CSS;
* PWA.

O frontend consumirá a API REST do backend.

---

# 17. Aplicação única e experiência por perfil

Não serão mantidos aplicativos separados para ADMIN, STAFF e HOSPEDE.

Uma única aplicação será responsável pelas diferentes experiências.

Após a autenticação, navegação, telas e ações disponíveis serão adaptadas ao perfil do usuário.

Estrutura conceitual:

```text
                       LOGIN
                         |
                         v
                  Usuario autenticado
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
        ADMIN          STAFF         HOSPEDE
          |              |              |
          v              v              v
     /dashboard      /dashboard        /app
```

---

# 18. Área operacional

ADMIN e STAFF compartilham a base da interface operacional.

Rotas previstas:

```text
/dashboard
/hospedes
/hospedes/{id}
/check-in
/quartos
/quartos/{id}
/traslados
/traslados/{id}
/viagem
/minha-conta
```

ADMIN poderá possuir adicionalmente recursos administrativos, como:

```text
/usuarios
/usuarios/{id}
```

As ações disponíveis dentro de cada tela devem respeitar as permissões do usuário autenticado.

---

# 19. Área do hóspede

O perfil HOSPEDE terá uma experiência simplificada e orientada à própria viagem.

Rotas planejadas:

```text
/app
/app/minha-viagem
/app/minha-hospedagem
/app/meu-traslado
/app/meu-qr
/app/avisos
/app/perfil
```

A arquitetura deverá garantir que o hóspede acesse somente informações autorizadas relacionadas à sua própria experiência.

---

# 20. Responsabilidades frontend x backend

A separação entre frontend e backend deve permanecer clara.

## Frontend

Responsável por:

* interface;
* navegação;
* formulários;
* experiência mobile;
* estados visuais;
* consumo da API;
* apresentação de informações;
* tratamento de respostas da API.

## Backend

Responsável por:

* autenticação;
* autorização;
* regras de negócio;
* persistência;
* integridade;
* validações críticas;
* geração e validação dos dados operacionais;
* controle de acesso.

O Next.js não deve se transformar em um segundo backend contendo cópias das regras existentes no Spring Boot.

---

# 21. Estratégia de autenticação do frontend

## Arquitetura definida

O backend atual retorna JWT após autenticação válida.

Para a integração com o frontend, a arquitetura deverá evoluir preservando o backend como autoridade da autenticação.

A direção definida para a aplicação web é utilizar cookie `HttpOnly` para reduzir a exposição direta do token ao JavaScript do navegador.

Também estão previstos:

* endpoint para recuperar o usuário autenticado;
* logout com expiração da autenticação;
* redirecionamento conforme perfil;
* tratamento de `401 Unauthorized`;
* tratamento de `403 Forbidden`.

Esses recursos devem ser implementados incrementalmente durante a integração do frontend.

A arquitetura de autenticação poderá ser refinada durante essa etapa sem duplicar o mecanismo de segurança do backend.

---

# 22. Banco de dados

O banco de dados utilizado pelo backend é MySQL.

A persistência é realizada através de:

```text
Spring Data JPA
      |
      v
Hibernate
      |
      v
MySQL
```

O modelo de banco deve acompanhar os domínios realmente necessários à operação.

Novas tabelas e relacionamentos devem ser introduzidos conforme novos requisitos sejam validados.

---

# 23. Tratamento de erros

Exceções de negócio são geradas nos Services e tratadas de maneira centralizada.

Fluxo:

```text
Service
   |
   v
Domain Exception
   |
   v
GlobalExceptionHandler
   |
   v
HTTP Response
```

Essa abordagem evita espalhar lógica de tratamento HTTP pelos Services.

Erros de autenticação e autorização possuem tratamento específico dentro da camada de segurança.

---

# 24. Testes

Testes automatizados fazem parte da arquitetura atual do backend.

O projeto utiliza:

```text
JUnit
Mockito
```

A suíte atual cobre Services, Controllers, Security, Repositories e outros comportamentos relevantes do backend.

Os testes devem acompanhar a evolução das regras de negócio.

Alterações estruturais não devem ser consideradas concluídas quando provocarem regressões na suíte existente.

---

# 25. Integração contínua

O projeto possui workflow de integração contínua em:

```text
.github/workflows/
```

A execução do backend considera sua localização dentro do diretório:

```text
backend/
```

O pipeline realiza build e análise automatizada do projeto.

A integração contínua deverá evoluir junto da estrutura full-stack para incluir as verificações necessárias ao frontend quando ele for incorporado.

---

# 26. Configuração e variáveis de ambiente

Informações sensíveis não devem ser versionadas diretamente no repositório.

O backend utiliza variáveis de ambiente para configurações como:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Um arquivo de exemplo pode documentar as variáveis necessárias sem armazenar valores reais.

O arquivo `.env` real permanece ignorado pelo Git.

---

# 27. PWA e mobile-first

O frontend será construído priorizando dispositivos móveis.

Essa decisão reflete o contexto de uso do produto, principalmente durante viagens e eventos.

A aplicação deverá funcionar como PWA, permitindo uma experiência próxima à de um aplicativo sem exigir, na primeira versão, aplicações nativas independentes.

### Definido para V1

Não será desenvolvido inicialmente um aplicativo nativo ou híbrido separado.

A prioridade é:

```text
Web
  |
  v
Mobile-first
  |
  v
PWA
```

A necessidade de aplicações nativas poderá ser reavaliada futuramente.

---

# 28. Evoluções planejadas

A arquitetura está preparada para evolução incremental.

Entre os recursos ainda planejados estão:

* Swagger/OpenAPI;
* Docker;
* Docker Compose;
* frontend completo;
* endpoints específicos para HOSPEDE;
* motoristas;
* veículos;
* notificações;
* push notifications;
* integração com mapas;
* relatórios;
* métricas adicionais;
* expansão da integração contínua;
* testes de integração adicionais.

Essas tecnologias ou módulos somente devem ser incorporados quando trouxerem benefício real ao produto.

---

# 29. Princípios arquiteturais

A evolução do BTHS Platform deve respeitar alguns princípios.

## Simplicidade

Evitar abstrações e componentes sem necessidade funcional atual.

## Separação de responsabilidades

Cada camada e domínio deve possuir responsabilidades claras.

## Backend como autoridade

Regras críticas, segurança e integridade permanecem no backend.

## Organização por domínio

Código relacionado ao mesmo contexto funcional deve permanecer próximo sempre que possível.

## Evolução incremental

A arquitetura deve crescer conforme necessidades reais do produto.

## Segurança por padrão

Permissões não devem depender apenas da interface.

## Mobile-first

A experiência principal do frontend deve considerar primeiro o uso em dispositivos móveis.

---

# 30. Princípio central

> **Construir primeiro o que a operação precisa, mantendo o código simples, organizado e preparado para evoluir.**

A arquitetura do BTHS Platform deve acompanhar a evolução da Beat Trips sem introduzir complexidade antes que ela seja necessária.

O objetivo não é construir a arquitetura mais complexa possível, mas uma arquitetura suficientemente sólida para resolver o problema atual e evoluir com segurança.
