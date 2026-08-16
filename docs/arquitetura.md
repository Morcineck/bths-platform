# 🏗️ Arquitetura - BTHS Platform

## 📌 Visão geral

O **BTHS Platform** será desenvolvido inicialmente como uma aplicação web baseada em uma **API REST**, responsável por centralizar as informações operacionais relacionadas às viagens.

A primeira versão terá como foco:

* gerenciamento de hóspedes;
* gerenciamento de viagens;
* organização da hospedagem;
* divisão de quartos e camas;
* gerenciamento de traslados;
* cadastro de motoristas e veículos;
* controle de chegada e check-in.

A arquitetura será construída de forma modular, permitindo que novas funcionalidades sejam adicionadas conforme a necessidade do projeto.

---

## 🧱 Arquitetura geral

A aplicação será organizada inicialmente da seguinte forma:

```text
                    BTHS PLATFORM
                         │
                         ▼
                    REST API
                         │
                         ▼
                  Spring Boot
                         │
        ┌────────────────┼────────────────┐
        │                │                │
     Hóspedes        Hospedagem       Traslados
        │                │                │
        │          ┌─────┴─────┐          │
        │       Quartos      Camas        │
        │                                 │
        └────────────────┬────────────────┘
                         │
                         ▼
                       MySQL
```

O frontend será desenvolvido posteriormente e consumirá os endpoints disponibilizados pelo backend.

---

# ☕ Backend

O backend será desenvolvido utilizando:

* **Java 17**
* **Spring Boot**
* **Spring Web**
* **Spring Data JPA**
* **Bean Validation**
* **MySQL**

Em etapas posteriores poderão ser adicionados:

* Spring Security;
* JWT;
* Swagger/OpenAPI;
* JUnit;
* Mockito;
* Docker.

---

# 🌐 API REST

O backend disponibilizará uma API REST responsável pela comunicação entre o frontend e o banco de dados.

Exemplo:

```text
Frontend
   │
   │ HTTP Request
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
MySQL
```

Exemplo de uma requisição:

```http
GET /api/viagens
```

O fluxo esperado será:

```text
Cliente
   │
   ▼
ViagemController
   │
   ▼
ViagemService
   │
   ▼
ViagemRepository
   │
   ▼
MySQL
```

---

# 🧩 Camadas da aplicação

A aplicação utilizará uma separação por responsabilidades.

## Controller

Responsável por receber as requisições HTTP e retornar as respostas da API.

Exemplo:

```text
ViagemController
```

Responsabilidades:

* receber requisições;
* validar parâmetros básicos;
* chamar o Service;
* retornar respostas HTTP.

O Controller não deverá conter regras de negócio complexas.

---

## Service

Responsável pelas regras de negócio da aplicação.

Exemplo:

```text
ViagemService
```

Responsabilidades:

* executar regras de negócio;
* coordenar operações;
* validar condições do domínio;
* utilizar os repositories necessários.

---

## Repository

Responsável pela comunicação com o banco de dados.

Exemplo:

```text
ViagemRepository
```

Será implementado utilizando o **Spring Data JPA**.

---

## Entity

Representará as principais entidades persistidas no banco de dados.

Exemplos:

```text
Viagem
Hospede
Quarto
Cama
Reserva
Motorista
Veiculo
Traslado
CheckIn
```

---

## DTO

Os DTOs serão utilizados para controlar os dados recebidos e enviados pela API.

Exemplo:

```text
ViagemRequestDTO
ViagemResponseDTO
```

A utilização de DTOs evita que as entidades do banco sejam expostas diretamente pela API.

---

# 🗂️ Organização dos módulos

O sistema será dividido inicialmente nos seguintes módulos:

```text
BTHS Platform
│
├── Viagens
│
├── Hóspedes
│
├── Hospedagem
│   ├── Quartos
│   └── Camas
│
├── Reservas
│
├── Traslados
│   ├── Motoristas
│   └── Veículos
│
└── Check-in
```

---

# 🧠 Domínio da aplicação

A entidade central do sistema será a **Viagem**.

Uma viagem representa uma experiência organizada pela empresa e poderá possuir diversos hóspedes, hospedagens, traslados e registros de check-in.

A estrutura conceitual inicial será:

```text
                         VIAGEM
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
         HÓSPEDES      HOSPEDAGEM       TRASLADOS
                            │              │
                       ┌────┴────┐     ┌───┴────┐
                       ▼         ▼     ▼        ▼
                    QUARTOS    CAMAS MOTORISTA VEÍCULO
             │
             ▼
          RESERVAS
             │
             ▼
          CHECK-IN
```

---

# 🏠 Hospedagem

A hospedagem será responsável por representar o local onde os hóspedes ficarão durante a viagem.

Ela poderá possuir:

* endereço;
* quartos;
* camas;
* capacidade;
* informações adicionais.

A divisão dos hóspedes será realizada através das reservas.

Exemplo:

```text
Viagem
└── Hospedagem
    ├── Quarto 01
    │   ├── Cama 01
    │   ├── Cama 02
    │   ├── Cama 03
    │   └── Cama 04
    │
    └── Quarto 02
        ├── Cama 01
        ├── Cama 02
        ├── Cama 03
        └── Cama 04
```

---

# 🚐 Traslados

Os traslados representarão os deslocamentos realizados durante a viagem.

Um traslado poderá possuir:

* origem;
* destino;
* data;
* horário de saída;
* motorista;
* veículo;
* status.

Exemplo:

```text
Aeroporto de Guarulhos
          │
          │ 09:30
          ▼
    Chácara
```

O hóspede poderá consultar as informações do seu traslado através da aplicação.

---

# ✅ Check-in

O check-in será utilizado para registrar a chegada do hóspede.

O sistema deverá permitir identificar:

* hóspede;
* viagem;
* data e horário da chegada;
* status do check-in.

Exemplo:

```text
Hóspede: João da Silva
Viagem: Tomorrowland Brasil 2027
Status: CHECK-IN REALIZADO
Horário: 11:42
```

---

# 🔐 Segurança

A autenticação e autorização serão implementadas em uma etapa posterior utilizando **Spring Security**.

A arquitetura deverá permitir diferentes níveis de acesso.

Inicialmente serão considerados:

```text
ADMIN
STAFF
HOSPEDE
```

As permissões específicas serão definidas posteriormente nas regras de negócio.

---

# 🗄️ Banco de dados

O banco de dados inicial será o **MySQL**.

As principais entidades previstas são:

```text
Viagem
Hospede
Hospedagem
Quarto
Cama
Reserva
Motorista
Veiculo
Traslado
CheckIn
```

Os relacionamentos serão definidos e documentados antes da implementação das entidades Java.

---

# 📁 Estrutura prevista do backend

A estrutura inicial do backend seguirá uma organização por domínio:

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── bths/
    │           └── platform/
    │               │
    │               ├── viagem/
    │               ├── hospede/
    │               ├── hospedagem/
    │               ├── reserva/
    │               ├── traslado/
    │               ├── motorista/
    │               ├── veiculo/
    │               └── checkin/
    │
    └── resources/
        ├── application.properties
        └── ...
```

A estrutura poderá ser ajustada durante o desenvolvimento caso novas necessidades arquiteturais apareçam.

---

# 🔄 Fluxo básico da aplicação

Um fluxo simples de utilização será:

```text
1. Administrador cria uma viagem
             ↓
2. Hóspedes são cadastrados
             ↓
3. Hóspedes são associados à viagem
             ↓
4. Hospedagem é configurada
             ↓
5. Quartos e camas são cadastrados
             ↓
6. Hóspedes são distribuídos
             ↓
7. Traslados são cadastrados
             ↓
8. Motoristas e veículos são associados
             ↓
9. Hóspedes consultam suas informações
             ↓
10. Hóspedes chegam à hospedagem
             ↓
11. Equipe realiza o check-in
```

---

# 📈 Evolução da arquitetura

A arquitetura inicial será mantida simples para facilitar o desenvolvimento e a evolução do projeto.

Novas tecnologias e componentes serão adicionados conforme houver necessidade real, evitando complexidade prematura.

Possíveis evoluções:

* autenticação com JWT;
* documentação completa da API;
* testes automatizados;
* Docker;
* sistema de notificações;
* integração com mapas;
* dashboard operacional;
* QR Code para check-in;
* frontend web;
* aplicativo mobile.

---

## 📌 Princípio arquitetural

> **Construir primeiro o que a operação precisa, mantendo o código simples, organizado e preparado para evoluir.**

A arquitetura do BTHS Platform deverá acompanhar a evolução do produto, evitando decisões complexas antes que elas sejam necessárias.
