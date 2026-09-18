# 🗺️ Roadmap - BTHS Platform

## 📌 Objetivo

Este documento apresenta o planejamento de desenvolvimento do **BTHS Platform**.

O roadmap está organizado em fases para permitir que a plataforma seja construída de forma progressiva, priorizando primeiro as funcionalidades essenciais da operação.

O documento também registra o que já foi implementado e os próximos marcos do projeto.

---

# 📊 Visão geral

```text
FASE 0 — Planejamento e Documentação       ✅
        ↓
FASE 1 — Fundação do Backend               ✅
        ↓
FASE 2 — Viagens                           ✅
        ↓
FASE 3 — Hóspedes                          ✅
        ↓
FASE 4 — Quartos / Hospedagem operacional  ✅ Parcial
        ↓
FASE 5 — Alocação e Ocupação               ✅
        ↓
FASE 6 — Traslados                         ✅ V1
        ↓
FASE 7 — Check-in e QR Code                ✅ V1
        ↓
FASE 8 — Segurança e Usuários              ✅ 
        ↓
FASE 9 — Dashboard e Operação              ⏳ Próxima
        ↓
FASE 10 — Frontend                         ⏳
        ↓
FASE 11 — Testes e Infraestrutura          🔄 Em evolução
        ↓
FASE 12 — Evolução                         💡
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

# ✅ Fase 1 — Fundação do Backend

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

# ✅ Fase 2 — Viagens

Objetivo: implementar o primeiro domínio principal da plataforma.

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
* [x] Implementar cancelamento
* [x] Implementar validações
* [x] Implementar tratamento de erros
* [x] Criar testes automatizados

---

# ✅ Fase 3 — Hóspedes

Objetivo: permitir o gerenciamento dos participantes das viagens.

* [x] Criar entidade `Hospede`
* [x] Criar `HospedeRepository`
* [x] Criar `HospedeService`
* [x] Criar `HospedeController`
* [x] Criar DTOs e mapper
* [x] Implementar cadastro
* [x] Implementar listagem
* [x] Implementar consulta
* [x] Implementar atualização
* [x] Associar hóspedes às viagens
* [x] Implementar validações e tratamento de erros
* [x] Criar testes automatizados

---

# ✅ Fase 4 — Hospedagem operacional — V1

Objetivo: representar a estrutura física utilizada durante uma viagem.

## Quartos

* [x] Criar entidade `Quarto`
* [x] Definir capacidade
* [x] Associar quarto à viagem
* [x] Definir tipo e status do quarto
* [x] Implementar cadastro
* [x] Implementar consulta
* [x] Implementar atualização
* [x] Validar capacidade e disponibilidade
* [x] Criar testes automatizados

## Hospedagem e camas

A modelagem original previa entidades independentes de `Hospedagem` e `Cama`. A primeira versão operacional foi simplificada para trabalhar diretamente com quartos e suas capacidades.

* [ ] Avaliar necessidade de entidade `Hospedagem`
* [ ] Avaliar necessidade de entidade `Cama`
* [ ] Evoluir a estrutura física caso a operação exija controle individual de camas

---

# ✅ Fase 5 — Alocação e ocupação

Objetivo: organizar a distribuição dos hóspedes nos quartos.

* [x] Criar `AlocacaoQuarto`
* [x] Associar hóspede, quarto e viagem
* [x] Impedir alocação duplicada
* [x] Impedir alocação acima da capacidade
* [x] Impedir alocação em quarto indisponível
* [x] Permitir troca de quarto
* [x] Permitir remoção da alocação
* [x] Consultar hóspedes por quarto
* [x] Calcular ocupação
* [x] Calcular vagas disponíveis
* [x] Criar testes automatizados

> A modelagem original previa uma entidade `Reserva`. Na V1 operacional, a necessidade principal foi atendida por `AlocacaoQuarto` e pelas consultas de ocupação.

---

# ✅ Fase 6 — Traslados — V1

Objetivo: controlar os traslados dos hóspedes vinculados às viagens.

## Traslados

* [x] Criar entidade `Traslado`
* [x] Criar tipos de traslado
* [x] Criar status do traslado
* [x] Criar enum de aeroporto
* [x] Associar traslado ao hóspede e à viagem
* [x] Definir origem e destino
* [x] Registrar data e horário previstos
* [x] Registrar informações de voo quando aplicável
* [x] Exigir aeroporto quando o tipo envolver aeroporto
* [x] Implementar cadastro
* [x] Implementar consulta por ID
* [x] Listar traslados por viagem
* [x] Listar traslados por hóspede
* [x] Implementar atualização
* [x] Implementar fluxo de status
* [x] Implementar cancelamento por status
* [x] Implementar correção administrativa de status com motivo
* [x] Registrar histórico de alterações de status
* [x] Consultar histórico de status
* [x] Criar testes automatizados de Service e Controller

### Status

```text
AGUARDANDO
EM_ANDAMENTO
CONCLUIDO
CANCELADO
```

### Evoluções futuras de transporte

* [ ] Criar entidade `Motorista`
* [ ] Criar entidade `Veiculo`
* [ ] Associar motorista ao traslado
* [ ] Associar veículo ao traslado
* [ ] Validar capacidade do veículo
* [ ] Implementar troca de motorista
* [ ] Implementar troca de veículo

---

# ✅ Fase 7 — Check-in e QR Code — V1

Objetivo: controlar a chegada dos hóspedes e agilizar identificação na operação.

## Check-in

* [x] Manter status de check-in no hóspede
* [x] Registrar data e horário do check-in
* [x] Registrar responsável
* [x] Suportar observação
* [x] Exigir alocação de quarto antes do check-in
* [x] Impedir check-in duplicado
* [x] Implementar realização do check-in
* [x] Implementar consulta da situação do check-in
* [x] Criar testes automatizados

## QR Code

* [x] Gerar identificador único e imprevisível para o hóspede
* [x] Persistir identificador
* [x] Identificar hóspede pelo código
* [x] Não expor dados pessoais no conteúdo do QR Code
* [x] Manter identificação separada da confirmação do check-in
* [x] Permitir identificação independentemente do status do check-in
* [x] Criar testes automatizados

---

# ✅ Fase 8 — Segurança e usuários

Objetivo: proteger a plataforma e definir diferentes níveis de acesso.

* [x] Criar entidade `Usuario`
* [x] Criar persistência e DTOs de usuário
* [x] Implementar armazenamento seguro de senhas
* [x] Configurar Spring Security
* [x] Implementar autenticação
* [x] Implementar login
* [x] Implementar JWT
* [x] Implementar autorização
* [x] Criar perfil `ADMIN`
* [x] Criar perfil `STAFF`
* [x] Criar perfil `HOSPEDE`
* [x] Definir permissões por perfil
* [x] Proteger endpoints
* [x] Criar testes automatizados de autenticação e autorização

---

# ✅ Fase 9 — Dashboard e operação — V1

Objetivo: fornecer uma visão centralizada da operação da viagem.

O dashboard deverá apresentar informações como:

* [x] Total de hóspedes
* [x] Hóspedes que chegaram
* [x] Hóspedes pendentes
* [x] Taxa de check-in
* [x] Ocupação dos quartos
* [x] Vagas disponíveis
* [x] Traslados aguardando
* [x] Traslados em andamento
* [x] Traslados concluídos
* [x] Próximos traslados

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
* [ ] Quartos
* [ ] Alocações e ocupação
* [ ] Traslados
* [ ] Check-in
* [ ] Leitura/identificação por QR Code

## Área do hóspede

* [ ] Login
* [ ] Minha viagem
* [ ] Meu quarto
* [ ] Meu traslado
* [ ] Horários
* [ ] Endereço da hospedagem
* [ ] Botão para abrir rota

---

# 🧪 Fase 11 — Testes e infraestrutura

Objetivo: aumentar a confiabilidade da plataforma e preparar o ambiente de execução.

## Testes

* [x] Adicionar JUnit
* [x] Adicionar Mockito
* [x] Criar testes de Service para os módulos implementados
* [x] Criar testes de Controller para os módulos implementados
* [x] Criar testes das principais regras de negócio
* [x] Criar testes de Repository quando houver necessidade real
* [ ] Expandir testes de integração

## Documentação da API

* [ ] Adicionar Swagger/OpenAPI
* [ ] Documentar endpoints
* [ ] Documentar DTOs
* [ ] Documentar respostas de erro

## Infraestrutura

* [ ] Criar `Dockerfile`
* [ ] Criar `docker-compose.yml`
* [ ] Configurar MySQL com Docker
* [x] Configurar variáveis de ambiente para desenvolvimento
* [ ] Preparar ambiente de produção
* [ ] Configurar CI/CD

---

# 🚀 Fase 12 — Evolução

Funcionalidades que poderão ser avaliadas depois da primeira versão funcional.

* [ ] Sistema de notificações
* [ ] Avisos para hóspedes
* [ ] Integração com Google Maps
* [ ] Integração com Waze
* [x] Histórico de alterações de status do traslado
* [ ] Auditoria global
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
feature/...
  ↓
Implementação
  ↓
Validação manual
  ↓
Pull Request para develop
  ↓
test/...
  ↓
Testes automatizados
  ↓
Pull Request para develop
  ↓
Integração
```

---

# 📌 Status atual

| Fase | Status        |
| --- |---------------|
| Fase 0 — Planejamento | ✅ Concluída   |
| Fase 1 — Fundação Backend | ✅ Concluída   |
| Fase 2 — Viagens | ✅ Concluída   |
| Fase 3 — Hóspedes | ✅ Concluída   |
| Fase 4 — Hospedagem operacional | ✅ V1 concluída |
| Fase 5 — Alocação e Ocupação | ✅ Concluída   |
| Fase 6 — Traslados | ✅ V1 concluída |
| Fase 7 — Check-in e QR Code | ✅ V1 concluída |
| Fase 8 — Segurança e Usuários | ✅ V1 concluída |
| Fase 9 — Dashboard | ✅ V1 concluída |
| Fase 10 — Frontend | ⏳ Próxima     |
| Fase 11 — Testes e Infraestrutura | 🔄 Em evolução |
| Fase 12 — Evolução | 💡 Futuro     |

---

## 🎯 Próximo marco

Com os principais domínios operacionais do backend, a segurança e o Dashboard V1 implementados, o próximo objetivo é:

> **Iniciar o desenvolvimento do frontend do BTHS Platform, integrando as interfaces operacionais à API já existente.**

A próxima etapa deverá começar pela estrutura base do frontend, autenticação e acesso ao dashboard operacional, evoluindo progressivamente para os demais módulos da plataforma.