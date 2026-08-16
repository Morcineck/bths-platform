# BTHS Platform

Plataforma de gestão operacional para viagens, hospedagens, traslados e recepção de hóspedes.

O **BTHS Platform** nasceu a partir de uma necessidade real da operação da Beat Trips: centralizar informações importantes de uma viagem em um único sistema e facilitar tanto o trabalho da equipe quanto a experiência dos hóspedes.

---

## 🎯 Objetivo

O BTHS Platform tem como objetivo centralizar as principais informações operacionais de uma viagem em uma única plataforma.

A primeira versão permitirá que a equipe gerencie:

* 👤 Hóspedes
* 🏠 Hospedagens
* 🛏️ Quartos e camas
* 🚐 Traslados
* 👨‍✈️ Motoristas
* 🚗 Veículos
* 📍 Endereços e localização
* ✅ Check-in dos hóspedes
* 📊 Informações operacionais da viagem

Para o hóspede, a plataforma deverá disponibilizar de forma simples todas as informações necessárias para sua experiência.

---

## 💡 Problema

Durante a organização de uma viagem, informações importantes podem ficar distribuídas entre conversas de WhatsApp, planilhas, documentos e outros meios de comunicação.

Isso pode gerar dificuldades como:

* localizar informações sobre a hospedagem;
* organizar a divisão dos quartos;
* identificar a cama de cada hóspede;
* controlar a chegada dos participantes;
* localizar o endereço da hospedagem;
* encontrar informações sobre traslados;
* identificar motoristas e veículos;
* acompanhar quem já realizou o check-in.

O BTHS Platform busca centralizar essas informações e tornar a operação mais organizada e eficiente.

---

## 🚀 Funcionalidades

### 👤 Hóspedes

* Cadastro de hóspedes
* Consulta de hóspedes
* Dados pessoais
* Associação com viagens
* Status da chegada

### 🏠 Hospedagem

* Cadastro de hospedagens
* Endereço
* Informações da propriedade
* Cadastro de quartos
* Cadastro de camas
* Capacidade dos quartos
* Divisão dos hóspedes

### 🚐 Traslados

* Cadastro de motoristas
* Cadastro de veículos
* Origem e destino
* Horário de saída
* Informações do motorista
* Status do traslado

### 📍 Localização

O hóspede poderá visualizar o endereço da hospedagem e acessar serviços externos de mapas para facilitar sua chegada.

### ✅ Check-in

A equipe poderá registrar a chegada dos hóspedes e acompanhar a quantidade de pessoas que já chegaram à hospedagem.

---

## 🧩 Arquitetura

A primeira versão da plataforma será desenvolvida utilizando uma arquitetura baseada em API REST.

```text
                    BTHS PLATFORM
                         │
                         ▼
                    REST API
                         │
                    Spring Boot
                         │
        ┌────────────────┼────────────────┐
        │                │                │
     Hóspedes        Hospedagem       Traslados
        │                │                │
        │          ┌─────┴─────┐          │
        │       Quartos      Camas        │
        │                                │
        └────────────────┬───────────────┘
                         │
                       MySQL
```

A arquitetura poderá evoluir conforme as necessidades do projeto.

---

## 🛠️ Tecnologias

### Backend

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* Bean Validation
* Spring Security
* JWT

### Banco de dados

* MySQL

### Testes

* JUnit
* Mockito

### Documentação da API

* Swagger / OpenAPI

### Infraestrutura

* Docker
* Docker Compose

### Versionamento

* Git
* GitHub

### Frontend

Planejado para uma etapa posterior:

* Next.js
* React
* TypeScript

---

## 📂 Estrutura do projeto

```text
bths-platform/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── test/
│
├── docs/
│   ├── arquitetura.md
│   ├── regras-negocio.md
│   └── roadmap.md
│
├── README.md
├── pom.xml
├── docker-compose.yml
└── .gitignore
```

---

## 🗺️ Roadmap

### Fase 1 — Fundação

* [ ] Criar projeto Spring Boot
* [ ] Configurar banco de dados MySQL
* [ ] Configurar estrutura inicial
* [ ] Criar entidade `Viagem`
* [ ] Criar entidade `Hospede`
* [ ] Criar API REST
* [ ] Documentar endpoints

### Fase 2 — Hospedagem

* [ ] Criar quartos
* [ ] Criar camas
* [ ] Criar reservas
* [ ] Associar hóspedes aos quartos
* [ ] Associar hóspedes às camas

### Fase 3 — Traslados

* [ ] Criar motoristas
* [ ] Criar veículos
* [ ] Criar traslados
* [ ] Associar hóspedes aos traslados
* [ ] Controle de horários
* [ ] Status do traslado

### Fase 4 — Check-in

* [ ] Registrar chegada
* [ ] Consultar hóspedes presentes
* [ ] Criar dashboard operacional
* [ ] Implementar QR Code para check-in

### Fase 5 — Autenticação

* [ ] Implementar Spring Security
* [ ] Implementar JWT
* [ ] Criar usuários administrativos
* [ ] Criar perfis de acesso
* [ ] Implementar controle de permissões

### Fase 6 — Frontend

* [ ] Criar portal administrativo
* [ ] Criar área do hóspede
* [ ] Criar dashboard
* [ ] Criar área de hospedagem
* [ ] Criar área de traslados
* [ ] Criar área de check-in

### Fase 7 — Evolução

* [ ] Implementar notificações
* [ ] Integração com mapas
* [ ] Criar relatórios
* [ ] Criar métricas operacionais
* [ ] Avaliar aplicativo mobile

---

## 📚 Documentação

A documentação técnica e funcional do projeto está organizada no diretório [`docs`](./docs).

* [Arquitetura](./docs/arquitetura.md)
* [Regras de negócio](./docs/regras-negocio.md)
* [Roadmap](./docs/roadmap.md)

---

## 🔐 Segurança

Informações sensíveis não devem ser armazenadas diretamente no código-fonte.

Credenciais, chaves de API, tokens e configurações privadas deverão utilizar variáveis de ambiente ou mecanismos apropriados de gerenciamento de secrets.

---

## 📌 Status

🚧 **Em desenvolvimento**

O projeto encontra-se em fase inicial de planejamento e desenvolvimento.

---

## 🎧 Contexto

O BTHS Platform está sendo desenvolvido inicialmente para atender às necessidades operacionais da **Beat Trips**, empresa especializada em experiências de viagem relacionadas a grandes eventos e festivais de música eletrônica.

O sistema será utilizado como ferramenta para organizar e centralizar as operações relacionadas às viagens.

---

## 👨‍💻 Desenvolvimento

Projeto desenvolvido com foco na aplicação prática de conhecimentos em:

* Java
* Spring Boot
* APIs REST
* Banco de dados
* Arquitetura de software
* Testes automatizados
* Segurança
* Git e GitHub
* Docker

---

## 📄 Licença

Este projeto encontra-se em desenvolvimento e sua licença será definida posteriormente.

---

**BTHS Platform**
*Tecnologia para organizar experiências.*
