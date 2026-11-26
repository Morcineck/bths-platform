# Roadmap - BTHS Platform

## 1. Objetivo

Este documento registra a evolução técnica e funcional do **BTHS Platform**, indicando o que já foi implementado, o que está em desenvolvimento e os próximos marcos do produto.

O roadmap é organizado em fases para preservar o histórico de construção da plataforma e permitir evolução incremental.

As fases concluídas representam a base atualmente existente no projeto. Funcionalidades futuras podem evoluir conforme necessidades reais da operação da Beat Trips.

---

# 2. Visão geral

```text id="i9w6yg"
FASE 0  — Planejamento e documentação          CONCLUÍDA
    ↓
FASE 1  — Fundação do backend                  CONCLUÍDA
    ↓
FASE 2  — Viagens                              CONCLUÍDA
    ↓
FASE 3  — Hóspedes                             CONCLUÍDA
    ↓
FASE 4  — Hospedagem operacional V1            CONCLUÍDA
    ↓
FASE 5  — Alocação e ocupação                  CONCLUÍDA
    ↓
FASE 6  — Traslados V1                         CONCLUÍDA
    ↓
FASE 7  — Check-in e QR Code V1                CONCLUÍDA
    ↓
FASE 8  — Segurança e usuários V1              CONCLUÍDA
    ↓
FASE 9  — Dashboard operacional V1             CONCLUÍDA
    ↓
FASE 10 — Frontend PWA                         PRÓXIMA
    ↓
FASE 11 — Testes, qualidade e infraestrutura   EM EVOLUÇÃO
    ↓
FASE 12 — Evolução do produto                  FUTURO
```

---

# 3. Fase 0 - Planejamento e documentação

**Status:** Concluída

Objetivo: estabelecer a base conceitual e documental do BTHS Platform.

* [x] Criar repositório do projeto
* [x] Criar `README.md`
* [x] Definir objetivo da plataforma
* [x] Definir arquitetura inicial
* [x] Criar `docs/arquitetura.md`
* [x] Definir regras de negócio
* [x] Criar `docs/regras-negocio.md`
* [x] Criar `docs/roadmap.md`
* [x] Evoluir documentação conforme implementação real
* [x] Atualizar documentação para arquitetura full-stack
* [x] Documentar separação entre backend e frontend

A documentação deve continuar evoluindo junto do produto.

---

# 4. Fase 1 - Fundação do backend

**Status:** Concluída

Objetivo: preparar a base técnica da API.

* [x] Criar projeto Spring Boot
* [x] Configurar Java 17
* [x] Configurar Maven
* [x] Configurar Maven Wrapper
* [x] Adicionar Spring Web
* [x] Adicionar Spring Data JPA
* [x] Adicionar Bean Validation
* [x] Adicionar MySQL Driver
* [x] Configurar conexão com MySQL
* [x] Definir organização inicial dos pacotes
* [x] Configurar variáveis de ambiente
* [x] Criar `.env.example`
* [x] Validar execução da aplicação
* [x] Evoluir organização para estrutura por domínio

---

# 5. Fase 2 - Viagens

**Status:** Concluída

Objetivo: implementar o primeiro domínio operacional da plataforma.

* [x] Criar entidade `Viagem`
* [x] Definir status da viagem
* [x] Criar `ViagemRepository`
* [x] Criar `ViagemService`
* [x] Criar `ViagemController`
* [x] Criar DTOs
* [x] Implementar cadastro
* [x] Implementar listagem
* [x] Implementar consulta por ID
* [x] Implementar atualização
* [x] Implementar remoção
* [x] Validar período da viagem
* [x] Implementar tratamento de erros
* [x] Criar testes automatizados

---

# 6. Fase 3 - Hóspedes

**Status:** Concluída

Objetivo: permitir o gerenciamento dos participantes das viagens.

* [x] Criar entidade `Hospede`
* [x] Criar `HospedeRepository`
* [x] Criar `HospedeService`
* [x] Criar `HospedeController`
* [x] Criar DTOs
* [x] Criar mapper
* [x] Implementar cadastro
* [x] Implementar listagem
* [x] Implementar consulta
* [x] Implementar atualização
* [x] Implementar remoção
* [x] Associar hóspede à viagem
* [x] Validar unicidade de CPF por viagem
* [x] Gerar identificador operacional de check-in
* [x] Implementar tratamento de erros
* [x] Criar testes automatizados

---

# 7. Fase 4 — Hospedagem operacional V1

**Status:** Concluída

Objetivo: representar a estrutura de acomodação necessária para a operação da viagem.

## 7.1 Quartos

* [x] Criar entidade `Quarto`
* [x] Associar quarto à viagem
* [x] Definir capacidade
* [x] Definir tipo
* [x] Definir status
* [x] Implementar cadastro
* [x] Implementar consulta
* [x] Implementar atualização
* [x] Implementar remoção
* [x] Considerar disponibilidade operacional
* [x] Criar testes automatizados

## 7.2 Simplificação da modelagem

A modelagem inicial previa entidades independentes para:

```text id="mxv0zs"
Hospedagem
Cama
Reserva
```

A V1 foi simplificada para utilizar:

```text id="k6dtwi"
Viagem
   |
   v
Quarto
   |
   v
AlocacaoQuarto
```

Essa estrutura atende atualmente à necessidade operacional sem introduzir complexidade desnecessária.

### Evoluções condicionais

* [ ] Avaliar entidade `Hospedagem` caso existam múltiplas propriedades ou necessidade operacional específica
* [ ] Avaliar controle individual de camas caso necessário
* [ ] Avaliar novos modelos de reserva caso a operação futura exija

Essas evoluções não são requisitos da V1.

---

# 8. Fase 5 — Alocação e ocupação

**Status:** Concluída

Objetivo: organizar a distribuição dos hóspedes entre os quartos.

* [x] Criar `AlocacaoQuarto`
* [x] Associar hóspede, quarto e viagem
* [x] Validar compatibilidade de viagem
* [x] Impedir alocação duplicada
* [x] Impedir alocação acima da capacidade
* [x] Impedir alocação em quarto indisponível
* [x] Permitir troca de quarto
* [x] Validar capacidade durante troca
* [x] Permitir remoção da alocação
* [x] Consultar hóspedes por quarto
* [x] Calcular ocupação
* [x] Calcular vagas disponíveis
* [x] Criar testes automatizados

A necessidade originalmente atribuída a uma entidade `Reserva` é atendida atualmente pela alocação e pelas consultas de ocupação.

---

# 9. Fase 6 — Traslados V1

**Status:** Concluída

Objetivo: controlar deslocamentos relacionados aos hóspedes e às viagens.

## 9.1 Base operacional

* [x] Criar entidade `Traslado`
* [x] Criar tipos de traslado
* [x] Criar status de traslado
* [x] Criar enum de aeroporto
* [x] Associar traslado ao hóspede
* [x] Associar traslado à viagem
* [x] Validar compatibilidade entre hóspede e viagem
* [x] Registrar origem
* [x] Registrar destino
* [x] Registrar data e horário previstos
* [x] Registrar número do voo
* [x] Registrar companhia aérea
* [x] Suportar observações
* [x] Exigir aeroporto quando o tipo de traslado envolver aeroporto

## 9.2 Operações

* [x] Implementar cadastro
* [x] Implementar consulta por ID
* [x] Listar traslados por viagem
* [x] Listar traslados por hóspede
* [x] Implementar atualização

## 9.3 Fluxo de status

Status atuais:

```text id="r3dvnq"
AGUARDANDO
EM_ANDAMENTO
CONCLUIDO
CANCELADO
```

Fluxo normal:

```text id="jv86cl"
AGUARDANDO
   |
   +--> EM_ANDAMENTO
   |         |
   |         +--> CONCLUIDO
   |         |
   |         +--> CANCELADO
   |
   +--> CANCELADO
```

* [x] Implementar transições normais
* [x] Impedir transições inválidas
* [x] Implementar cancelamento por status
* [x] Implementar correção administrativa de status
* [x] Exigir motivo para correção
* [x] Registrar histórico de alteração
* [x] Consultar histórico

## 9.4 Testes

* [x] Criar testes de Service
* [x] Criar testes de Controller
* [x] Testar transições de status
* [x] Testar correções administrativas

## 9.5 Evoluções futuras de transporte

* [ ] Criar domínio de motoristas
* [ ] Criar domínio de veículos
* [ ] Associar motorista ao traslado
* [ ] Associar veículo ao traslado
* [ ] Controlar capacidade do veículo
* [ ] Permitir troca de motorista
* [ ] Permitir troca de veículo
* [ ] Avaliar conflitos operacionais e de agenda

---

# 10. Fase 7 — Check-in e QR Code V1

**Status:** Concluída

Objetivo: controlar a chegada dos hóspedes e agilizar sua identificação.

## 10.1 Check-in

* [x] Manter status de check-in
* [x] Registrar data e horário
* [x] Registrar responsável
* [x] Suportar observação
* [x] Exigir alocação antes do check-in
* [x] Impedir check-in duplicado
* [x] Implementar realização do check-in
* [x] Implementar consulta da situação
* [x] Criar testes automatizados

## 10.2 QR Code

* [x] Gerar identificador operacional único para o hóspede
* [x] Utilizar UUID como código operacional
* [x] Persistir identificador
* [x] Identificar hóspede pelo código
* [x] Recuperar alocação quando existente
* [x] Não armazenar dados pessoais diretamente no conteúdo do QR Code
* [x] Manter identificação separada da confirmação do check-in
* [x] Permitir identificação independentemente do status do check-in
* [x] Gerar imagem do QR Code
* [x] Criar testes automatizados

### Evolução definida

O identificador operacional poderá ser reutilizado em outros processos que necessitem de identificação rápida do hóspede.

---

# 11. Fase 8 — Segurança e usuários V1

**Status:** Concluída

Objetivo: proteger a plataforma e estabelecer níveis de acesso.

## 11.1 Usuários

* [x] Criar entidade `Usuario`
* [x] Criar persistência
* [x] Criar DTOs
* [x] Utilizar UUID como identificador
* [x] Garantir unicidade de e-mail
* [x] Criar usuário ativo por padrão
* [x] Implementar codificação de senha com BCrypt

## 11.2 Autenticação

* [x] Configurar Spring Security
* [x] Implementar autenticação por e-mail e senha
* [x] Implementar login
* [x] Implementar JWT
* [x] Configurar autenticação stateless
* [x] Implementar filtro JWT
* [x] Tratar falhas de autenticação
* [x] Tratar acesso negado

## 11.3 Autorização

Perfis existentes:

```text id="5xzyqm"
ADMIN
STAFF
HOSPEDE
```

* [x] Criar perfil ADMIN
* [x] Criar perfil STAFF
* [x] Criar perfil HOSPEDE
* [x] Proteger endpoints administrativos
* [x] Proteger endpoints operacionais
* [x] Restringir gerenciamento de usuários ao ADMIN
* [x] Compartilhar recursos operacionais entre ADMIN e STAFF
* [x] Criar testes automatizados de autenticação e autorização

### Evolução necessária

* [ ] Criar autorização específica para os endpoints da experiência do HOSPEDE
* [ ] Integrar autenticação à aplicação web
* [ ] Implementar recuperação do usuário autenticado para o frontend
* [ ] Implementar logout da aplicação web

---

# 12. Fase 9 — Dashboard operacional V1

**Status:** Concluída

Objetivo: fornecer uma visão consolidada da operação de uma viagem.

## 12.1 Hóspedes

* [x] Total de hóspedes
* [x] Hóspedes presentes
* [x] Hóspedes pendentes
* [x] Taxa de check-in

## 12.2 Hospedagem

* [x] Calcular capacidade utilizável
* [x] Desconsiderar quartos indisponíveis
* [x] Calcular vagas ocupadas
* [x] Calcular vagas disponíveis

## 12.3 Traslados

* [x] Contabilizar traslados aguardando
* [x] Contabilizar traslados em andamento
* [x] Contabilizar traslados concluídos
* [x] Consultar próximos traslados
* [x] Limitar consulta aos cinco próximos registros

Estrutura conceitual:

```text id="mv2l6c"
DASHBOARD

Hospedes
├── Total
├── Presentes
├── Pendentes
└── Taxa de check-in

Hospedagem
├── Vagas totais
├── Ocupadas
└── Disponiveis

Traslados
├── Aguardando
├── Em andamento
├── Concluidos
└── Proximos traslados
```

---

# 13. Marco estrutural — Reorganização full-stack

**Status:** Concluído

Antes do início do frontend, o repositório foi reorganizado para representar corretamente um produto full-stack.

## 13.1 Estrutura

```text id="71bsl5"
bths-platform/
├── backend/
├── frontend/
├── docs/
├── .github/
├── .gitignore
├── CHANGELOG.md
└── README.md
```

O diretório `frontend/` representa a posição arquitetural definida para a nova aplicação e será criado efetivamente durante a Fase 10.

## 13.2 Backend

* [x] Mover aplicação Spring Boot para `backend/`
* [x] Mover Maven Wrapper
* [x] Mover `pom.xml`
* [x] Mover `.env.example`
* [x] Preservar execução dos testes após reorganização
* [x] Validar build após mudança estrutural

## 13.3 Organização de exceptions

* [x] Reorganizar exceptions por domínio
* [x] Manter `GlobalExceptionHandler` como componente transversal
* [x] Validar suíte após refatoração

## 13.4 Git e ambiente

* [x] Atualizar `.gitignore`
* [x] Preparar regras para Node e Next.js
* [x] Manter arquivos sensíveis fora do versionamento
* [x] Remover artefatos gerados desnecessários da raiz

## 13.5 Integração contínua

* [x] Adaptar workflow ao novo diretório `backend/`
* [x] Preservar build Maven na integração contínua

## 13.6 Documentação

* [x] Reconstruir `README.md`
* [x] Atualizar `docs/regras-negocio.md`
* [x] Atualizar `docs/arquitetura.md`
* [x] Atualizar `docs/roadmap.md`

---

# 14. Fase 10 — Frontend PWA

**Status:** Próxima

Objetivo: construir a interface operacional e a experiência digital do hóspede sobre a API existente.

## 14.1 Stack definida

* [ ] Next.js
* [ ] React
* [ ] TypeScript
* [ ] Tailwind CSS
* [ ] PWA
* [ ] arquitetura mobile-first

A V1 utilizará uma aplicação web responsiva e instalável.

Não será criado inicialmente um aplicativo nativo ou híbrido separado.

---

## 14.2 Fundação do frontend

* [ ] Criar aplicação Next.js em `frontend/`
* [ ] Configurar TypeScript
* [ ] Configurar Tailwind CSS
* [ ] Utilizar App Router
* [ ] Definir estrutura de diretórios
* [ ] Definir componentes base
* [ ] Definir layout global
* [ ] Configurar identidade visual da Beat Trips
* [ ] Preparar configuração PWA
* [ ] Configurar variáveis de ambiente
* [ ] Definir cliente de comunicação com a API

---

## 14.3 Autenticação web

* [ ] Criar tela de login
* [ ] Integrar login ao backend
* [ ] Definir sessão web utilizando autenticação fornecida pelo backend
* [ ] Evoluir armazenamento do JWT para estratégia adequada à aplicação web
* [ ] Utilizar cookie HttpOnly conforme arquitetura definida
* [ ] Criar mecanismo para recuperar usuário autenticado
* [ ] Implementar logout
* [ ] Implementar redirecionamento por perfil
* [ ] Tratar `401 Unauthorized`
* [ ] Criar tela/tratamento para `403 Forbidden`

Fluxo previsto:

```text id="xtq4nc"
Login
  |
  v
Backend
  |
  v
Autenticacao
  |
  v
Perfil
  |
  +--> ADMIN   -> /dashboard
  |
  +--> STAFF   -> /dashboard
  |
  └--> HOSPEDE -> /app
```

---

## 14.4 Aplicação única por perfil

ADMIN, STAFF e HOSPEDE utilizarão a mesma aplicação.

A interface será adaptada conforme o perfil autenticado.

```text id="wklvfr"
BTHS FRONTEND
      |
      v
Autenticacao
      |
      +----------------+----------------+
      |                |                |
      v                v                v
    ADMIN            STAFF           HOSPEDE
      |                |                |
      +------ Operacao +                |
                                       v
                                Experiencia da viagem
```

---

## 14.5 Área ADMIN

Rotas e recursos previstos:

* [ ] `/dashboard`
* [ ] `/hospedes`
* [ ] `/hospedes/{id}`
* [ ] `/check-in`
* [ ] `/quartos`
* [ ] `/quartos/{id}`
* [ ] `/traslados`
* [ ] `/traslados/{id}`
* [ ] `/viagem`
* [ ] `/usuarios`
* [ ] `/usuarios/{id}`
* [ ] `/minha-conta`

O ADMIN possui acesso às funcionalidades administrativas e operacionais permitidas pelo backend.

---

## 14.6 Área STAFF

Recursos previstos:

* [ ] Dashboard operacional
* [ ] Busca de hóspedes
* [ ] Check-in
* [ ] Identificação por QR Code
* [ ] Quartos e ocupação
* [ ] Traslados
* [ ] Informações da viagem
* [ ] Minha conta

O STAFF não deverá receber acesso administrativo ao gerenciamento de usuários.

---

## 14.7 Área HOSPEDE

A experiência do hóspede será concentrada em `/app`.

Rotas previstas:

* [ ] `/app`
* [ ] `/app/minha-viagem`
* [ ] `/app/minha-hospedagem`
* [ ] `/app/meu-traslado`
* [ ] `/app/meu-qr`
* [ ] `/app/avisos`
* [ ] `/app/perfil`

Funcionalidades previstas:

* [ ] apresentar próximo passo relevante;
* [ ] visualizar informações da viagem;
* [ ] visualizar hospedagem;
* [ ] visualizar quarto quando alocado;
* [ ] visualizar situação do check-in;
* [ ] acessar QR Code pessoal;
* [ ] visualizar informações de chegada e traslado;
* [ ] acessar endereço e orientações;
* [ ] receber avisos;
* [ ] consultar informações do próprio perfil.

---

## 14.8 Design e experiência

Direção visual definida:

```text id="1efhwp"
Dark SaaS
Travel Operations
Festival Tech
Beat Trips
```

Princípios:

* [ ] mobile-first
* [ ] interface clara durante operação
* [ ] navegação adaptada ao perfil
* [ ] identidade visual consistente
* [ ] foco em ações prioritárias
* [ ] componentes reutilizáveis
* [ ] estados de loading
* [ ] estados vazios
* [ ] tratamento visual de erros
* [ ] feedback das operações

---

## 14.9 PWA

* [ ] Configurar manifest
* [ ] Definir ícones
* [ ] Definir comportamento instalável
* [ ] Avaliar estratégia de cache
* [ ] Avaliar funcionamento limitado em condições de conexão instável
* [ ] Preparar base para notificações push futuras

---

# 15. Fase 11 — Testes, qualidade e infraestrutura

**Status:** Em evolução

Essa fase acompanha continuamente o desenvolvimento das demais.

## 15.1 Testes backend

* [x] Adicionar JUnit
* [x] Adicionar Mockito
* [x] Criar testes de Service
* [x] Criar testes de Controller
* [x] Criar testes de Security
* [x] Criar testes de Repository quando necessário
* [x] Testar principais regras de negócio
* [x] Manter suíte automatizada durante refatorações
* [ ] Expandir testes de integração

Marco atual de referência do backend:

```text id="zdvp43"
223 testes
0 failures
0 errors
0 skipped
BUILD SUCCESS
```

---

## 15.2 Testes frontend

### Planejado

* [ ] Definir estratégia de testes do frontend
* [ ] Testar componentes críticos
* [ ] Testar autenticação
* [ ] Testar controle de acesso
* [ ] Testar principais fluxos operacionais
* [ ] Testar experiência do hóspede

---

## 15.3 Documentação da API

* [ ] Adicionar Swagger/OpenAPI
* [ ] Documentar endpoints
* [ ] Documentar DTOs
* [ ] Documentar respostas de erro
* [ ] Manter documentação alinhada às regras de negócio

---

## 15.4 Infraestrutura

* [ ] Criar Dockerfile do backend
* [ ] Criar configuração necessária para frontend
* [ ] Criar `docker-compose.yml`
* [ ] Configurar MySQL com Docker
* [x] Configurar variáveis de ambiente para desenvolvimento
* [ ] Preparar ambiente de produção

---

## 15.5 CI/CD

* [x] Criar workflow de integração contínua
* [x] Executar build automatizado do backend
* [x] Adaptar workflow à estrutura `backend/`
* [ ] Incorporar verificações do frontend
* [ ] Definir estratégia de deploy
* [ ] Automatizar verificações full-stack

---

## 15.6 Qualidade

* [x] Integrar análise estática ao workflow
* [ ] Revisar configuração de análise conforme limitações do ambiente utilizado
* [ ] Expandir verificações de qualidade conforme o projeto evoluir

---

# 16. Fase 12 — Evolução do produto

**Status:** Futuro

Funcionalidades que poderão ser avaliadas depois da base full-stack funcional.

## 16.1 Comunicação

* [ ] Sistema de avisos
* [ ] Notificações
* [ ] Notificações push
* [ ] Segmentação de avisos quando necessário

## 16.2 Transporte

* [ ] Motoristas
* [ ] Veículos
* [ ] Capacidade de veículos
* [ ] Associação de motoristas e veículos aos traslados
* [ ] Gestão operacional avançada de transporte

## 16.3 Mapas

* [ ] Integração com mapas
* [ ] Abrir rotas externas
* [ ] Avaliar Google Maps
* [ ] Avaliar Waze

## 16.4 Dados e gestão

* [ ] Auditoria global
* [ ] Relatórios
* [ ] Exportação de informações
* [ ] Métricas operacionais adicionais
* [ ] Evolução do dashboard

## 16.5 Hospedagem

* [ ] Avaliar múltiplas propriedades
* [ ] Avaliar controle individual de camas
* [ ] Evoluir estrutura somente quando houver necessidade operacional

## 16.6 QR Code

* [ ] Avaliar reutilização do identificador para embarque
* [ ] Avaliar identificação em outros processos da viagem

## 16.7 Aplicações nativas

A estratégia atual é utilizar PWA.

Aplicativos nativos ou híbridos somente deverão ser avaliados futuramente caso existam necessidades que a aplicação web não consiga atender adequadamente.

---

# 17. Estratégia de desenvolvimento

O desenvolvimento deve continuar utilizando mudanças pequenas e rastreáveis.

Fluxo recomendado:

```text id="y3mjs9"
Necessidade
    |
    v
Issue / planejamento
    |
    v
Branch
    |
    v
Implementacao
    |
    v
Validacao
    |
    v
Testes
    |
    v
Pull Request
    |
    v
Review
    |
    v
Integracao
```

A ordem pode variar conforme a natureza da alteração.

Testes devem acompanhar a implementação sempre que a funcionalidade possuir comportamento verificável.

---

# 18. Estratégia de branches

A organização principal utiliza:

```text id="b7zz2b"
main
  |
  v
develop
  |
  +--> feature/...
  +--> fix/...
  +--> refactor/...
  +--> chore/...
  +--> docs/...
```

`main` representa a linha estável.

`develop` concentra a integração do desenvolvimento.

Mudanças devem chegar à branch apropriada através de Pull Requests conforme o fluxo adotado pelo projeto.

---

# 19. Status atual

| Fase                                         | Status      |
| -------------------------------------------- | ----------- |
| Fase 0 — Planejamento e documentação         | Concluída   |
| Fase 1 — Fundação do backend                 | Concluída   |
| Fase 2 — Viagens                             | Concluída   |
| Fase 3 — Hóspedes                            | Concluída   |
| Fase 4 — Hospedagem operacional V1           | Concluída   |
| Fase 5 — Alocação e ocupação                 | Concluída   |
| Fase 6 — Traslados V1                        | Concluída   |
| Fase 7 — Check-in e QR Code V1               | Concluída   |
| Fase 8 — Segurança e usuários V1             | Concluída   |
| Fase 9 — Dashboard operacional V1            | Concluída   |
| Marco — Reorganização full-stack             | Concluído   |
| Fase 10 — Frontend PWA                       | Próxima     |
| Fase 11 — Testes, qualidade e infraestrutura | Em evolução |
| Fase 12 — Evolução do produto                | Futuro      |

---

# 20. Próximo marco

Com o backend operacional V1 implementado, segurança configurada, Dashboard V1 concluído e repositório reorganizado para a arquitetura full-stack, o próximo marco é:

> **Iniciar a implementação do frontend PWA do BTHS Platform.**

A sequência inicial prevista é:

```text id="lt49mx"
Validar ambiente Node.js / npm
        |
        v
Criar frontend Next.js
        |
        v
Configurar estrutura base
        |
        v
Configurar identidade visual
        |
        v
Implementar autenticação
        |
        v
Integrar perfil do usuário
        |
        v
Dashboard ADMIN / STAFF
        |
        v
Módulos operacionais
        |
        v
Experiência HOSPEDE
        |
        v
PWA
```

O primeiro passo técnico da Fase 10 será criar a aplicação Next.js dentro de:

```text id="umof2b"
frontend/
```

sem alterar a responsabilidade atual do backend Spring Boot.

---

# 21. Princípio do roadmap

> **Evoluir o produto de acordo com necessidades reais da operação, preservando simplicidade, segurança e capacidade de evolução.**

O roadmap orienta o desenvolvimento, mas não deve forçar funcionalidades que deixaram de fazer sentido para a operação.

Mudanças arquiteturais ou funcionais relevantes devem ser refletidas no roadmap, nas regras de negócio e na documentação da arquitetura.
