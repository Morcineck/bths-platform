# 🗺️ Roadmap - BTHS Platform

## 📌 Objetivo

Este documento apresenta o planejamento de desenvolvimento do **BTHS Platform**.

O roadmap está organizado em fases para permitir que a plataforma seja construída de forma progressiva, priorizando primeiro as funcionalidades essenciais da operação.

Cada fase poderá posteriormente ser dividida em **Issues no GitHub**, permitindo acompanhar a evolução do projeto.

---

# 📊 Visão geral

```text
FASE 0 — Planejamento e Documentação
        ↓
FASE 1 — Fundação do Backend
        ↓
FASE 2 — Viagens
        ↓
FASE 3 — Hóspedes
        ↓
FASE 4 — Hospedagem
        ↓
FASE 5 — Reservas e Acomodações
        ↓
FASE 6 — Traslados
        ↓
FASE 7 — Check-in
        ↓
FASE 8 — Segurança e Usuários
        ↓
FASE 9 — Dashboard e Operação
        ↓
FASE 10 — Frontend
        ↓
FASE 11 — Testes e Infraestrutura
        ↓
FASE 12 — Evolução
```

---

# ✅ Fase 0 — Planejamento e documentação

Objetivo: definir o escopo inicial e documentar as principais decisões antes da implementação.

* [x] Criar repositório do projeto
* [x] Criar `README.md`
* [x] Definir objetivo inicial da plataforma
* [x] Definir arquitetura inicial
* [x] Criar `docs/arquitetura.md`
* [x] Definir regras de negócio
* [x] Criar `docs/regras-negocio.md`
* [x] Criar `docs/roadmap.md`

---

# ☕ Fase 1 — Fundação do Backend

Objetivo: preparar a base técnica da aplicação.

* [x] Criar projeto com Spring Boot
* [x] Configurar Java 17
* [x] Configurar Maven
* [x] Adicionar Spring Web
* [x] Adicionar Spring Data JPA
* [x] Adicionar Bean Validation
* [x] Adicionar MySQL Driver
* [x] Configurar conexão com MySQL
* [x] Definir estrutura inicial dos pacotes
* [x] Configurar variáveis de ambiente
* [x] Validar execução inicial da aplicação

---

# ✈️ Fase 2 — Viagens

Objetivo: implementar o primeiro domínio principal da plataforma.

* [ ] Criar entidade `Viagem`
* [ ] Definir status da viagem
* [ ] Criar `ViagemRepository`
* [ ] Criar `ViagemService`
* [ ] Criar `ViagemController`
* [ ] Criar DTO de entrada
* [ ] Criar DTO de saída
* [ ] Implementar cadastro de viagem
* [ ] Implementar listagem de viagens
* [ ] Implementar consulta por ID
* [ ] Implementar atualização
* [ ] Implementar cancelamento
* [ ] Implementar validações
* [ ] Implementar tratamento de erros

### Endpoints previstos

```http
POST   /api/viagens
GET    /api/viagens
GET    /api/viagens/{id}
PUT    /api/viagens/{id}
DELETE /api/viagens/{id}
```

---

# 👤 Fase 3 — Hóspedes

Objetivo: permitir o gerenciamento dos participantes das viagens.

* [ ] Criar entidade `Hospede`
* [ ] Criar `HospedeRepository`
* [ ] Criar `HospedeService`
* [ ] Criar `HospedeController`
* [ ] Criar DTOs
* [ ] Implementar cadastro
* [ ] Implementar listagem
* [ ] Implementar consulta
* [ ] Implementar atualização
* [ ] Implementar validação de documento
* [ ] Evitar cadastros duplicados
* [ ] Associar hóspedes às viagens

### Endpoints previstos

```http
POST /api/hospedes
GET  /api/hospedes
GET  /api/hospedes/{id}
PUT  /api/hospedes/{id}
```

---

# 🏠 Fase 4 — Hospedagem

Objetivo: representar a estrutura física utilizada durante uma viagem.

## Hospedagem

* [ ] Criar entidade `Hospedagem`
* [ ] Criar endereço da hospedagem
* [ ] Associar hospedagem à viagem
* [ ] Implementar cadastro
* [ ] Implementar consulta
* [ ] Implementar atualização

## Quartos

* [ ] Criar entidade `Quarto`
* [ ] Definir capacidade
* [ ] Associar quarto à hospedagem
* [ ] Implementar cadastro
* [ ] Implementar consulta
* [ ] Validar capacidade

## Camas

* [ ] Criar entidade `Cama`
* [ ] Definir tipos de cama
* [ ] Associar cama ao quarto
* [ ] Implementar cadastro
* [ ] Implementar consulta
* [ ] Controlar disponibilidade

---

# 🛏️ Fase 5 — Reservas e acomodações

Objetivo: organizar a distribuição dos hóspedes dentro das hospedagens.

* [ ] Criar entidade `Reserva`
* [ ] Associar reserva ao hóspede
* [ ] Associar reserva à viagem
* [ ] Associar reserva à hospedagem
* [ ] Associar reserva ao quarto
* [ ] Associar reserva à cama
* [ ] Criar status da reserva
* [ ] Implementar criação de reserva
* [ ] Implementar alteração de quarto
* [ ] Implementar alteração de cama
* [ ] Impedir ocupação duplicada de camas
* [ ] Validar capacidade dos quartos
* [ ] Consultar ocupação da hospedagem

---

# 🚐 Fase 6 — Traslados

Objetivo: gerenciar os transportes utilizados durante as viagens.

## Motoristas

* [ ] Criar entidade `Motorista`
* [ ] Criar cadastro
* [ ] Implementar consulta
* [ ] Implementar atualização

## Veículos

* [ ] Criar entidade `Veiculo`
* [ ] Definir capacidade
* [ ] Criar cadastro
* [ ] Implementar consulta
* [ ] Implementar atualização

## Traslados

* [ ] Criar entidade `Traslado`
* [ ] Criar status do traslado
* [ ] Definir origem
* [ ] Definir destino
* [ ] Definir data
* [ ] Definir horário
* [ ] Associar motorista
* [ ] Associar veículo
* [ ] Associar traslado à viagem
* [ ] Associar hóspedes ao traslado
* [ ] Implementar consulta dos passageiros
* [ ] Validar capacidade do veículo
* [ ] Implementar alteração de horário
* [ ] Implementar troca de motorista
* [ ] Implementar troca de veículo
* [ ] Implementar cancelamento

---

# ✅ Fase 7 — Check-in

Objetivo: controlar a chegada dos hóspedes.

* [ ] Criar entidade `CheckIn`
* [ ] Criar status do check-in
* [ ] Associar check-in ao hóspede
* [ ] Associar check-in à viagem
* [ ] Registrar data e horário automaticamente
* [ ] Implementar realização do check-in
* [ ] Impedir check-in duplicado
* [ ] Consultar hóspedes presentes
* [ ] Consultar hóspedes pendentes
* [ ] Calcular quantidade de hóspedes presentes

### Evolução

* [ ] Gerar QR Code do hóspede
* [ ] Implementar leitura de QR Code
* [ ] Realizar check-in através de QR Code

---

# 🔐 Fase 8 — Segurança e usuários

Objetivo: proteger a plataforma e definir diferentes níveis de acesso.

* [ ] Criar entidade `Usuario`
* [ ] Implementar autenticação
* [ ] Configurar Spring Security
* [ ] Implementar JWT
* [ ] Implementar login
* [ ] Implementar autorização
* [ ] Criar perfil `ADMIN`
* [ ] Criar perfil `STAFF`
* [ ] Criar perfil `HOSPEDE`
* [ ] Definir permissões por perfil
* [ ] Proteger endpoints
* [ ] Implementar armazenamento seguro de senhas

---

# 📊 Fase 9 — Dashboard e operação

Objetivo: fornecer uma visão centralizada da operação da viagem.

O dashboard deverá apresentar informações como:

* [ ] Total de hóspedes
* [ ] Hóspedes que chegaram
* [ ] Hóspedes pendentes
* [ ] Taxa de check-in
* [ ] Ocupação dos quartos
* [ ] Camas disponíveis
* [ ] Traslados programados
* [ ] Traslados em andamento
* [ ] Traslados concluídos
* [ ] Próximas saídas

Exemplo:

```text
BTHS PLATFORM — OPERAÇÃO

Hóspedes
69 cadastrados
57 presentes
12 aguardando

Hospedagem
69 vagas
57 ocupadas
12 disponíveis

Traslados
4 programados
2 concluídos
1 em andamento
1 aguardando
```

---

# 💻 Fase 10 — Frontend

Objetivo: criar as interfaces utilizadas pela equipe e pelos hóspedes.

Tecnologias inicialmente previstas:

* Next.js
* React
* TypeScript

## Área administrativa

* [ ] Login
* [ ] Dashboard
* [ ] Viagens
* [ ] Hóspedes
* [ ] Hospedagens
* [ ] Quartos
* [ ] Camas
* [ ] Reservas
* [ ] Motoristas
* [ ] Veículos
* [ ] Traslados
* [ ] Check-in

## Área do hóspede

* [ ] Login
* [ ] Minha viagem
* [ ] Minha hospedagem
* [ ] Meu quarto
* [ ] Minha cama
* [ ] Meu traslado
* [ ] Informações do motorista
* [ ] Horários
* [ ] Endereço da hospedagem
* [ ] Botão para abrir rota

---

# 🧪 Fase 11 — Testes e infraestrutura

Objetivo: aumentar a confiabilidade da plataforma e preparar o ambiente de execução.

## Testes

* [ ] Adicionar JUnit
* [ ] Adicionar Mockito
* [ ] Criar testes de Service
* [ ] Criar testes de Controller
* [ ] Criar testes de Repository quando necessário
* [ ] Criar testes das principais regras de negócio

## Documentação da API

* [ ] Adicionar Swagger/OpenAPI
* [ ] Documentar endpoints
* [ ] Documentar DTOs
* [ ] Documentar respostas de erro

## Infraestrutura

* [ ] Criar `Dockerfile`
* [ ] Criar `docker-compose.yml`
* [ ] Configurar MySQL com Docker
* [ ] Configurar variáveis de ambiente
* [ ] Preparar ambiente de produção
* [ ] Configurar CI/CD

---

# 🚀 Fase 12 — Evolução

Funcionalidades que poderão ser avaliadas depois da primeira versão funcional.

* [ ] Sistema de notificações
* [ ] Avisos para hóspedes
* [ ] Integração com Google Maps
* [ ] Integração com Waze
* [ ] Histórico de alterações
* [ ] Auditoria
* [ ] Relatórios
* [ ] Exportação de informações
* [ ] Métricas operacionais
* [ ] Aplicativo mobile
* [ ] Notificações push

---

# 🌱 Estratégia de desenvolvimento

Cada funcionalidade deverá seguir, sempre que fizer sentido, o fluxo:

```text
Issue
  ↓
Branch
  ↓
Implementação
  ↓
Testes
  ↓
Pull Request
  ↓
Review
  ↓
Merge
```

Exemplos de branches:

```text
feature/configuracao-spring
feature/cadastro-viagem
feature/cadastro-hospede
feature/hospedagem
feature/reserva
feature/traslado
feature/checkin
feature/autenticacao
```

---

# 📌 Status atual

| Fase                              | Status      |
| --------------------------------- | ----------- |
| Fase 0 — Planejamento             | ✅ Concluída |
| Fase 1 — Fundação Backend         | ⏳ Próxima   |
| Fase 2 — Viagens                  | ⏳ Planejada |
| Fase 3 — Hóspedes                 | ⏳ Planejada |
| Fase 4 — Hospedagem               | ⏳ Planejada |
| Fase 5 — Reservas                 | ⏳ Planejada |
| Fase 6 — Traslados                | ⏳ Planejada |
| Fase 7 — Check-in                 | ⏳ Planejada |
| Fase 8 — Segurança                | ⏳ Planejada |
| Fase 9 — Dashboard                | ⏳ Planejada |
| Fase 10 — Frontend                | ⏳ Planejada |
| Fase 11 — Testes e Infraestrutura | ⏳ Planejada |
| Fase 12 — Evolução                | 💡 Futuro   |

---

## 🎯 Próximo marco

Com a documentação inicial concluída, o próximo objetivo do projeto será:

> **Criar e configurar a base do backend do BTHS Platform utilizando Java 17 e Spring Boot.**

A partir dessa fundação, o primeiro domínio implementado será **Viagens**.
