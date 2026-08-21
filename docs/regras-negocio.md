# 📋 Regras de Negócio - BTHS Platform

## 📌 Objetivo

Este cpf define as principais regras de negócio do **BTHS Platform**.

As regras descritas aqui servem como referência para a modelagem do banco de dados, desenvolvimento da API e implementação das funcionalidades da plataforma.

As regras poderão ser revisadas conforme novas necessidades forem identificadas durante o desenvolvimento e utilização do sistema.

---

# ✈️ 1. Viagens

Uma **viagemNome** representa uma experiência organizada pela empresa.

Cada viagemNome deverá possuir, inicialmente:

* nome;
* evento;
* data de início;
* data de término;
* endereço da hospedagem;
* cidade;
* estado;
* status.

### Regras

1. Uma viagemNome deve possuir um nome.
2. Uma viagemNome deve possuir uma data de início.
3. Uma viagemNome deve possuir uma data de término.
4. A data de término não pode ser anterior à data de início.
5. Uma viagemNome poderá possuir vários hóspedes.
6. Uma viagemNome poderá possuir uma ou mais hospedagens.
7. Uma viagemNome poderá possuir vários traslados.
8. Uma viagemNome poderá possuir vários registros de check-in.
9. Uma viagemNome deverá possuir um status que permita identificar sua situação.

### Status previstos

```text
PLANEJADA
EM_ANDAMENTO
FINALIZADA
CANCELADA
```

---

# 👤 2. Hóspedes

O **hóspede** representa uma pessoa que participa de uma viagemNome.

### Informações previstas

* nome;
* e-mail;
* telefone;
* cpf;
* data de nascimento;
* status.

### Regras

1. Um hóspede deve possuir nome.
2. Um hóspede deve possuir um cpf de identificação.
3. Um hóspede poderá participar de várias viagens.
4. Um hóspede poderá possuir diferentes reservas em viagens diferentes.
5. O cadastro do hóspede não deverá ser duplicado quando a pessoa participar de uma nova viagemNome.
6. O hóspede deverá estar associado a uma viagemNome para participar da operação daquela viagemNome.

---

# 🏠 3. Hospedagem

Uma **hospedagem** representa o local onde os hóspedes ficarão durante uma viagemNome.

### Informações previstas

* nome;
* endereço;
* cidade;
* estado;
* CEP;
* informações adicionais.

### Regras

1. Uma hospedagem deve possuir um endereço.
2. Uma hospedagem poderá possuir vários quartos.
3. Uma hospedagem poderá possuir várias camas através dos seus quartos.
4. Uma hospedagem deverá estar associada a uma viagemNome.
5. Uma viagemNome poderá possuir mais de uma hospedagem.

---

# 🛏️ 4. Quartos

Um **quarto** representa uma unidade de acomodação dentro de uma hospedagem.

### Informações previstas

* identificação;
* nome;
* capacidade;
* tipo.

### Regras

1. Um quarto pertence a uma hospedagem.
2. Um quarto pode possuir uma ou várias camas.
3. A quantidade de hóspedes alocados ao quarto não deve ultrapassar sua capacidade.
4. Um quarto não pode ser associado a uma hospedagem inexistente.

### Exemplos

```text
Suíte 01
Suíte 02
Quarto 01
Quarto 02
```

---

# 🛌 5. Camas

Uma **cama** representa o local individual destinado ao hóspede dentro de um quarto.

### Informações previstas

* identificação;
* tipo;
* quarto.

### Tipos previstos

```text
SOLTEIRO
BELICHE_INFERIOR
BELICHE_SUPERIOR
CASAL
```

### Regras

1. Uma cama pertence a apenas um quarto.
2. Uma cama não pode estar simultaneamente ocupada por dois hóspedes.
3. Uma cama poderá estar disponível ou ocupada.
4. A cama deverá ser identificada dentro do quarto.

Exemplo:

```text
Quarto 01

Beliche 01 - Inferior
Beliche 01 - Superior

Beliche 02 - Inferior
Beliche 02 - Superior
```

---

# 📑 6. Reservas

A **reserva** representa a associação do hóspede com uma viagemNome e sua acomodação.

Ela será responsável por registrar onde determinado hóspede ficará durante uma viagemNome.

### Informações previstas

* hóspede;
* viagemNome;
* hospedagem;
* quarto;
* cama;
* status.

### Regras

1. Uma reserva deve possuir um hóspede.
2. Uma reserva deve possuir uma viagemNome.
3. Uma reserva deverá estar associada à hospedagem.
4. Uma reserva poderá possuir um quarto.
5. Uma reserva poderá possuir uma cama.
6. Uma cama ocupada não poderá ser atribuída simultaneamente a outro hóspede na mesma viagemNome.
7. A alteração de quarto ou cama deverá manter o histórico necessário caso essa funcionalidade seja implementada.
8. Uma reserva poderá possuir status.

### Status previstos

```text
PENDENTE
CONFIRMADA
CANCELADA
FINALIZADA
```

---

# 🚐 7. Traslados

Um **traslado** representa um deslocamento organizado durante uma viagemNome.

Exemplos:

```text
Aeroporto → Chácara
Chácara → Aeroporto
Chácara → Evento
Evento → Chácara
```

### Informações previstas

* viagemNome;
* origem;
* destino;
* data;
* horário de saída;
* motorista;
* veículo;
* status.

### Regras

1. Um traslado deve estar associado a uma viagemNome.
2. Um traslado deve possuir origem.
3. Um traslado deve possuir destino.
4. Um traslado deve possuir data.
5. Um traslado deve possuir horário de saída.
6. Um traslado poderá possuir um motorista.
7. Um traslado poderá possuir um veículo.
8. Um traslado poderá transportar vários hóspedes.
9. Um hóspede poderá participar de diferentes traslados durante uma viagemNome.
10. O status do traslado deverá permitir acompanhar sua situação operacional.

### Status previstos

```text
AGENDADO
CONFIRMADO
EM_ANDAMENTO
CONCLUIDO
CANCELADO
```

---

# 👨‍✈️ 8. Motoristas

O **motorista** representa o responsável pela condução de um veículo durante um traslado.

### Informações previstas

* nome;
* telefone;
* cpf;
* status.

### Regras

1. Um motorista deve possuir nome.
2. Um motorista deve possuir telefone.
3. Um motorista poderá realizar vários traslados.
4. Um motorista poderá utilizar diferentes veículos em momentos diferentes.
5. Um motorista deverá estar associado ao traslado quando sua participação estiver confirmada.

---

# 🚗 9. Veículos

O **veículo** representa o transporte utilizado em um traslado.

### Informações previstas

* placa;
* modelo;
* capacidade;
* tipo;
* status.

### Regras

1. Um veículo deve possuir placa.
2. Um veículo deve possuir modelo.
3. Um veículo deve possuir capacidade.
4. Um veículo poderá participar de vários traslados.
5. Um veículo não poderá ser utilizado em dois traslados conflitantes no mesmo horário.

---

# 👥 10. Hóspedes e Traslados

Um traslado poderá transportar vários hóspedes.

Por isso, a relação entre hóspedes e traslados será considerada **muitos-para-muitos**.

Exemplo:

```text
Traslado 01
│
├── João
├── Maria
├── Pedro
└── Robson
```

E um mesmo hóspede poderá participar de:

```text
João
│
├── Aeroporto → Chácara
├── Chácara → Evento
└── Evento → Chácara
```

Essa relação deverá ser representada adequadamente no banco de dados.

---

# ✅ 11. Check-in

O **check-in** representa o registro de chegada de um hóspede à hospedagem.

### Informações previstas

* hóspede;
* viagemNome;
* data e hora;
* status.

### Regras

1. O check-in deve estar associado a um hóspede.
2. O check-in deve estar associado a uma viagemNome.
3. O sistema deve registrar a data e hora da chegada.
4. Um hóspede poderá possuir apenas um check-in ativo por viagemNome.
5. Um check-in realizado não deverá ser excluído sem autorização adequada.
6. A equipe deverá conseguir consultar quais hóspedes já chegaram.
7. O sistema deverá permitir identificar a quantidade de hóspedes presentes na hospedagem.

### Status previstos

```text
PENDENTE
REALIZADO
CANCELADO
```

---

# 📍 12. Endereço

O endereço será utilizado principalmente para facilitar a localização da hospedagem.

### Informações previstas

* logradouro;
* número;
* complemento;
* bairro;
* cidade;
* estado;
* CEP;
* referência;
* latitude;
* longitude.

### Regras

1. Uma hospedagem deve possuir endereço.
2. O endereço deverá conter informações suficientes para localizar a hospedagem.
3. Latitude e longitude poderão ser utilizadas futuramente para integração com serviços de mapas.
4. O sistema poderá disponibilizar um link externo para navegação.

---

# 🔄 13. Alterações operacionais

Durante uma viagemNome podem ocorrer alterações de última hora.

Exemplos:

* troca de quarto;
* troca de cama;
* alteração de motorista;
* alteração de veículo;
* alteração de horário;
* cancelamento de traslado.

O sistema deverá permitir essas alterações de maneira controlada.

Sempre que necessário, alterações relevantes deverão preservar informações suficientes para auditoria ou histórico.

A implementação detalhada de histórico será definida posteriormente.

---

# 🔐 14. Usuários e permissões

A plataforma deverá possuir controle de acesso.

Os perfis inicialmente previstos são:

```text
ADMIN
STAFF
HOSPEDE
```

### ADMIN

Responsável pelo gerenciamento completo da plataforma.

Poderá:

* cadastrar viagens;
* cadastrar hóspedes;
* organizar hospedagens;
* organizar quartos;
* organizar camas;
* cadastrar motoristas;
* cadastrar veículos;
* organizar traslados;
* realizar alterações operacionais;
* acompanhar check-ins.

### STAFF

Usuário operacional da equipe.

Poderá executar tarefas relacionadas à operação, de acordo com suas permissões.

### HOSPEDE

Usuário destinado ao participante da viagemNome.

Poderá consultar informações relacionadas à sua própria viagemNome, como:

* hospedagem;
* quarto;
* cama;
* traslado;
* motorista;
* endereço;
* informações da viagemNome.

---

# 📊 15. Status operacionais

O sistema deverá utilizar status padronizados para facilitar o acompanhamento da operação.

Os status deverão ser representados no backend utilizando valores controlados, evitando textos livres para informações que possuem estados definidos.

Exemplo:

```text
VIAGEM
PLANEJADA
EM_ANDAMENTO
FINALIZADA
CANCELADA
```

```text
TRASLADO
AGENDADO
CONFIRMADO
EM_ANDAMENTO
CONCLUIDO
CANCELADO
```

```text
CHECK-IN
PENDENTE
REALIZADO
CANCELADO
```

---

# 🧠 16. Princípios das regras de negócio

As regras do sistema deverão seguir alguns princípios:

### Simplicidade

A operação deve ser simples para a equipe utilizar durante uma viagemNome.

### Consistência

O sistema deve impedir informações conflitantes sempre que possível.

### Rastreabilidade

Alterações operacionais importantes deverão poder ser identificadas quando houver necessidade de histórico.

### Segurança

Cada usuário deverá acessar apenas as informações permitidas para seu perfil.

### Experiência do hóspede

As informações apresentadas ao hóspede devem ser claras e fáceis de encontrar.

---

# 🔄 17. Evolução das regras

Este cpf não é definitivo.

Novas regras poderão ser adicionadas conforme:

* o desenvolvimento avançar;
* novas necessidades forem identificadas;
* a plataforma começar a ser utilizada;
* situações reais da operação forem observadas.

Alterações relevantes nas regras de negócio deverão ser documentadas neste arquivo antes ou junto da implementação.

---

## 📌 Regra principal

> **O sistema deve refletir a operação real da viagemNome, e não obrigar a operação a se adaptar a uma complexidade desnecessária do sistema.**
