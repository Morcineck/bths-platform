# Regras de Negócio - BTHS Platform

## 1. Objetivo

Este documento define as principais regras de negócio do **BTHS Platform**.

As regras aqui registradas orientam o comportamento da aplicação, as validações do backend, o desenvolvimento do frontend e a evolução funcional da plataforma.

O documento deve refletir a operação real da Beat Trips e o comportamento efetivamente esperado do sistema.

Sempre que uma regra relevante for criada, alterada ou removida, esta documentação deverá ser atualizada juntamente com a evolução correspondente do sistema.

---

## 2. Classificação das regras

Para diferenciar funcionalidades existentes de decisões futuras, as regras são classificadas em três estados.

### Implementada

Regra que já possui comportamento correspondente no backend atual.

### Definida

Regra de produto ou arquitetura já estabelecida para o BTHS Platform, mas cuja implementação completa pode depender de etapas posteriores, principalmente do frontend ou de novos endpoints.

### Planejada

Funcionalidade ou regra prevista para evolução futura e que ainda poderá sofrer alterações antes da implementação.

---

## 3. Princípios gerais

As regras de negócio do BTHS Platform seguem os seguintes princípios.

### 3.1 Simplicidade operacional

O sistema deve facilitar a execução da operação durante uma viagem.

A plataforma não deve introduzir complexidade desnecessária em processos que podem ser executados de maneira simples.

### 3.2 Consistência

O sistema deve impedir estados incompatíveis, duplicidades e operações que comprometam a integridade das informações.

### 3.3 Rastreabilidade

Operações que exigem histórico devem preservar informações suficientes para identificar alterações relevantes.

### 3.4 Segurança

Usuários devem acessar apenas recursos compatíveis com suas permissões.

O backend permanece responsável por autenticação, autorização, validações e regras de negócio, independentemente das restrições apresentadas pelo frontend.

### 3.5 Experiência do hóspede

As informações destinadas ao hóspede devem ser simples, claras e relacionadas à sua própria participação na viagem.

### 3.6 Autoridade do backend

O frontend não deve substituir ou duplicar regras críticas de negócio.

Validações realizadas na interface possuem finalidade de experiência do usuário, enquanto a decisão final sobre a validade de uma operação pertence ao backend.

---

# 4. Perfis de acesso

O BTHS Platform possui três perfis de usuário:

```text
ADMIN
STAFF
HOSPEDE
```

## 4.1 ADMIN

O perfil `ADMIN` representa usuários responsáveis pela administração da plataforma.

### Implementado

O ADMIN possui acesso aos recursos administrativos e operacionais protegidos pelo backend.

O gerenciamento de usuários é exclusivo do perfil ADMIN.

Atualmente:

```text
/api/usuarios/**
```

é acessível somente por ADMIN.

O ADMIN também possui acesso aos recursos operacionais compartilhados com STAFF.

---

## 4.2 STAFF

O perfil `STAFF` representa integrantes da equipe responsáveis pela execução da operação.

### Implementado

ADMIN e STAFF possuem acesso aos principais recursos operacionais:

```text
/api/viagens/**
/api/hospedes/**
/api/quartos/**
/api/alocacoes-quartos/**
/api/traslados/**
/api/check-in/qr/**
/api/dashboard/**
```

O STAFF não possui acesso ao gerenciamento administrativo de usuários.

---

## 4.3 HOSPEDE

O perfil `HOSPEDE` representa o participante da viagem.

### Definido

O hóspede utilizará a mesma plataforma, mas terá uma experiência diferente da área operacional.

Seu acesso deverá ser limitado às informações relacionadas à sua própria participação na viagem.

A área do hóspede deverá permitir acesso, conforme a evolução da plataforma, a informações como:

* própria viagem;
* hospedagem e quarto;
* identificação por QR Code;
* situação do check-in;
* chegada e traslado;
* avisos;
* orientações operacionais;
* dados do próprio perfil.

O frontend do hóspede será concentrado na área `/app`.

### Pendente de implementação

A autorização específica dos endpoints destinados ao perfil HOSPEDE ainda deverá ser desenvolvida conforme a construção da área do hóspede.

---

# 5. Autenticação

## 5.1 Login

### Implementado

A autenticação é realizada utilizando:

```text
e-mail
senha
```

Quando as credenciais são válidas, o backend gera um token JWT.

Credenciais inválidas devem resultar em erro de autenticação sem informar se o problema ocorreu especificamente no e-mail ou na senha.

A mensagem funcional utilizada atualmente é equivalente a:

```text
E-mail ou senha inválidos.
```

---

## 5.2 Sessão

### Implementado

O backend utiliza autenticação stateless.

A API não depende de sessão HTTP armazenada no servidor.

Cada requisição protegida deve possuir autenticação válida.

---

## 5.3 Senhas

### Implementado

Senhas de usuários não devem ser armazenadas em texto puro.

O backend utiliza BCrypt para codificação das senhas.

---

## 5.4 Proteção dos recursos

### Implementado

Os endpoints de autenticação:

```text
/api/auth/**
```

são públicos.

Os demais recursos protegidos exigem autenticação e respeitam as permissões configuradas para cada perfil.

---

# 6. Usuários

Um usuário representa uma identidade autenticável dentro da plataforma.

## 6.1 Cadastro

### Implementado

Um usuário possui, entre outras informações:

* nome;
* e-mail;
* senha;
* perfil;
* situação de atividade.

Ao cadastrar um usuário:

1. o e-mail não pode estar previamente cadastrado;
2. a senha deve ser codificada antes da persistência;
3. o perfil informado deve ser associado ao usuário;
4. o usuário é criado como ativo.

O identificador utilizado para usuários é UUID.

---

## 6.2 Unicidade de e-mail

### Implementado

Não podem existir dois usuários cadastrados com o mesmo e-mail.

Uma tentativa de cadastro utilizando um e-mail já existente deve ser rejeitada.

---

## 6.3 Administração

### Implementado

O gerenciamento de usuários é uma operação administrativa protegida pelo perfil ADMIN.

---

# 7. Viagens

Uma viagem representa uma operação organizada pela Beat Trips.

Ela funciona como elemento central para associação de hóspedes, quartos, alocações, traslados e indicadores operacionais.

## 7.1 Cadastro e atualização

### Implementado

Uma viagem pode ser cadastrada, consultada, listada, atualizada e removida.

A data final da viagem não pode ser anterior à data inicial.

A mesma validação deve ser aplicada tanto no cadastro quanto na atualização.

---

## 7.2 Existência da viagem

### Implementado

Operações que dependem de uma viagem existente devem ser rejeitadas quando o identificador informado não corresponder a uma viagem cadastrada.

---

## 7.3 Relacionamentos

### Implementado

Uma viagem pode possuir:

* vários hóspedes;
* vários quartos;
* várias alocações;
* vários traslados;
* informações agregadas no dashboard.

Entidades operacionais relacionadas devem manter consistência com a viagem à qual pertencem.

---

# 8. Hóspedes

Um hóspede representa uma pessoa participante de uma viagem.

## 8.1 Associação com viagem

### Implementado

Todo hóspede cadastrado deve estar associado a uma viagem existente.

Não é permitido cadastrar ou atualizar um hóspede apontando para uma viagem inexistente.

---

## 8.2 Identificação por CPF dentro da viagem

### Implementado

O mesmo CPF não pode ser cadastrado duas vezes dentro da mesma viagem.

A verificação é realizada considerando:

```text
CPF + viagem
```

Portanto, a regra atual de unicidade é vinculada à viagem.

Na atualização de um hóspede, o próprio registro deve ser desconsiderado durante a verificação de duplicidade.

---

## 8.3 Código operacional de check-in

### Implementado

No cadastro de um hóspede, o sistema gera automaticamente um código único utilizando UUID.

Esse código é armazenado como `codigoCheckIn`.

Ele funciona como identificador operacional utilizado pelo módulo de QR Code.

---

## 8.4 Operações

### Implementado

O backend permite:

* cadastrar hóspede;
* consultar hóspede por identificador;
* listar hóspedes;
* atualizar hóspede;
* remover hóspede.

Operações sobre um hóspede inexistente devem ser rejeitadas.

---

# 9. Quartos

Um quarto representa uma unidade de acomodação utilizada na operação de uma viagem.

## 9.1 Associação com viagem

### Implementado

Todo quarto deve estar associado a uma viagem existente.

O quarto pertence operacionalmente à viagem informada em seu cadastro.

---

## 9.2 Capacidade

### Implementado

Cada quarto possui uma capacidade.

A quantidade de hóspedes alocados não pode ultrapassar essa capacidade.

A ocupação real do quarto é determinada pelas alocações existentes.

---

## 9.3 Disponibilidade

### Implementado

Um quarto com status:

```text
INDISPONIVEL
```

não pode receber novas alocações.

Quartos indisponíveis também são desconsiderados no cálculo de capacidade utilizável apresentado pelo dashboard.

---

## 9.4 Operações

### Implementado

O backend permite:

* cadastrar quarto;
* consultar quarto;
* listar quartos;
* atualizar quarto;
* remover quarto.

Operações que dependem de um quarto inexistente devem ser rejeitadas.

---

# 10. Alocação de quartos

A alocação representa a associação operacional entre um hóspede e um quarto dentro de uma viagem.

Ela substitui, na versão atual do domínio, a necessidade de manter entidades independentes de reserva e cama.

## 10.1 Compatibilidade de viagem

### Implementado

Um hóspede somente pode ser alocado em um quarto pertencente à mesma viagem.

Se:

```text
viagem do hóspede != viagem do quarto
```

a operação deve ser rejeitada.

---

## 10.2 Alocação única

### Implementado

Um hóspede não pode possuir mais de uma alocação simultânea dentro da mesma viagem.

Antes de criar uma alocação, o sistema verifica se já existe uma associação entre o hóspede e a viagem.

---

## 10.3 Quarto disponível

### Implementado

Um hóspede não pode ser alocado em um quarto marcado como indisponível.

---

## 10.4 Limite de capacidade

### Implementado

Antes da alocação, o sistema calcula a ocupação atual do quarto.

Se:

```text
ocupação atual >= capacidade
```

a nova alocação deve ser rejeitada.

---

## 10.5 Troca de quarto

### Implementado

Uma alocação existente pode ser transferida para outro quarto.

O novo quarto:

1. deve existir;
2. deve pertencer à mesma viagem da alocação;
3. não pode estar indisponível;
4. deve possuir vaga disponível.

A troca altera o quarto associado à alocação existente.

---

## 10.6 Remoção

### Implementado

Uma alocação pode ser removida.

A operação deve ser rejeitada quando a alocação informada não existir.

---

## 10.7 Ocupação

### Implementado

O sistema pode consultar a ocupação de um quarto e apresentar:

* identificador do quarto;
* nome;
* capacidade;
* ocupação atual;
* vagas disponíveis.

As vagas disponíveis são calculadas por:

```text
capacidade - ocupação
```

---

# 11. Check-in

O check-in representa a confirmação operacional da chegada do hóspede.

## 11.1 Pré-requisito de alocação

### Implementado

Um hóspede somente pode realizar check-in quando possuir uma alocação de quarto correspondente à sua viagem.

Um hóspede sem alocação deve ter o check-in rejeitado.

---

## 11.2 Check-in único

### Implementado

Um hóspede cujo status de check-in seja:

```text
REALIZADO
```

não pode realizar novo check-in.

---

## 11.3 Registro

### Implementado

Ao realizar o check-in, o sistema registra:

* status `REALIZADO`;
* data e hora;
* responsável pelo check-in;
* observação, quando informada.

A data e hora são registradas pelo sistema no momento da operação.

---

## 11.4 Consulta

### Implementado

A situação de check-in de um hóspede pode ser consultada.

Quando houver alocação de quarto, as informações correspondentes podem fazer parte da resposta.

A consulta pode existir mesmo quando o hóspede ainda não possui alocação.

---

# 12. QR Code

O QR Code funciona como mecanismo de identificação operacional do hóspede.

## 12.1 Código

### Implementado

Cada hóspede recebe um `codigoCheckIn` no momento do cadastro.

Esse código é utilizado para identificação por QR Code.

---

## 12.2 Identificação

### Implementado

Ao receber um código válido, o sistema identifica o hóspede correspondente.

Quando existir alocação para aquele hóspede na viagem, ela também poderá ser retornada na identificação.

Um código inexistente deve ser tratado como QR Code inválido ou hóspede não encontrado.

---

## 12.3 Imagem do QR Code

### Implementado

O backend é capaz de gerar uma representação do QR Code utilizando o código operacional do hóspede.

---

## 12.4 Uso operacional

### Definido

O mesmo identificador deverá ser reutilizado sempre que possível em processos operacionais que exijam identificação rápida do hóspede, evitando a criação desnecessária de identificadores diferentes para cada operação.

---

# 13. Traslados

Um traslado representa uma necessidade de deslocamento associada a um hóspede dentro de uma viagem.

Na implementação atual, cada registro de traslado está associado diretamente a:

* um hóspede;
* uma viagem.

## 13.1 Compatibilidade

### Implementado

O hóspede informado no traslado deve pertencer à mesma viagem informada.

Se as viagens forem diferentes, o cadastro deve ser rejeitado.

---

## 13.2 Tipos relacionados a aeroporto

### Implementado

Os tipos:

```text
AEROPORTO_PARA_HOSPEDAGEM
HOSPEDAGEM_PARA_AEROPORTO
```

exigem a identificação de um aeroporto.

Quando um desses tipos for utilizado sem aeroporto, a operação deve ser rejeitada.

A mesma validação é aplicada no cadastro e na atualização.

---

## 13.3 Informações operacionais

### Implementado

Um traslado pode armazenar informações como:

* tipo;
* aeroporto;
* data e hora prevista;
* número do voo;
* companhia aérea;
* local de origem;
* local de destino;
* observações.

---

## 13.4 Status inicial

### Implementado

Todo novo traslado é criado com o status:

```text
AGUARDANDO
```

---

## 13.5 Fluxo normal de status

### Implementado

As transições normais permitidas são:

```text
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

Assim:

```text
AGUARDANDO -> EM_ANDAMENTO
AGUARDANDO -> CANCELADO
EM_ANDAMENTO -> CONCLUIDO
EM_ANDAMENTO -> CANCELADO
```

Os estados:

```text
CONCLUIDO
CANCELADO
```

não permitem transição normal para outro estado.

---

## 13.6 Correção excepcional de status

### Implementado

O sistema possui uma operação específica para corrigir determinados estados finais quando ocorreu erro operacional.

As correções permitidas atualmente são:

```text
CONCLUIDO -> EM_ANDAMENTO
CANCELADO -> AGUARDANDO
```

Uma correção exige obrigatoriamente um motivo.

Motivo ausente ou vazio deve resultar na rejeição da operação.

Outras combinações de correção não são permitidas.

---

## 13.7 Histórico de status

### Implementado

Toda alteração normal ou correção de status registra histórico.

O histórico contém:

* traslado;
* status anterior;
* novo status;
* motivo;
* data e hora.

Nas alterações normais, o sistema registra como motivo uma indicação de alteração normal de status.

Nas correções, utiliza o motivo informado pelo operador.

---

## 13.8 Consultas

### Implementado

O sistema permite:

* consultar traslado por identificador;
* listar traslados de uma viagem;
* listar traslados de um hóspede;
* consultar histórico de status.

---

## 13.9 Motoristas e veículos

### Planejada

O gerenciamento estruturado de motoristas e veículos não faz parte do domínio implementado atualmente.

Essa funcionalidade poderá ser adicionada posteriormente para permitir recursos como:

* cadastro de motoristas;
* cadastro de veículos;
* capacidade dos veículos;
* associação entre traslado, motorista e veículo;
* acompanhamento operacional;
* validação de conflitos de agenda.

---

# 14. Dashboard operacional

O dashboard fornece uma visão consolidada da operação de uma viagem.

## 14.1 Viagem obrigatória

### Implementado

O dashboard somente pode ser consultado para uma viagem existente.

---

## 14.2 Resumo de hóspedes

### Implementado

O dashboard apresenta:

* total de hóspedes;
* hóspedes presentes;
* hóspedes pendentes;
* taxa de check-in.

A taxa é calculada por:

```text
(presentes * 100) / total
```

Quando não existem hóspedes, a taxa deve ser `0`.

---

## 14.3 Resumo de hospedagem

### Implementado

O dashboard apresenta:

* vagas totais utilizáveis;
* vagas ocupadas;
* vagas disponíveis.

Quartos com status `INDISPONIVEL` não entram no cálculo de vagas totais utilizáveis.

As vagas ocupadas correspondem às alocações existentes na viagem.

As vagas disponíveis são calculadas por:

```text
vagas totais - vagas ocupadas
```

O resultado não deve ser inferior a zero.

---

## 14.4 Resumo de traslados

### Implementado

O dashboard apresenta a quantidade de traslados nos estados:

```text
AGUARDANDO
EM_ANDAMENTO
CONCLUIDO
```

Também apresenta os próximos traslados aguardando execução.

Atualmente são retornados no máximo cinco próximos traslados.

Para cada item podem ser apresentadas informações como:

* identificador;
* hóspede;
* tipo;
* data e hora prevista;
* origem;
* destino.

---

# 15. Integridade entre domínios

## 15.1 Referências existentes

### Implementado

Quando uma operação depende de outra entidade, a entidade referenciada deve existir.

Exemplos:

* hóspede depende de viagem;
* quarto depende de viagem;
* alocação depende de hóspede e quarto;
* check-in depende de hóspede;
* traslado depende de hóspede e viagem;
* dashboard depende de viagem.

---

## 15.2 Compatibilidade de viagem

### Implementado

Entidades relacionadas operacionalmente devem pertencer à mesma viagem quando essa relação for exigida.

Essa regra é aplicada atualmente, entre outros casos, em:

* hóspede e quarto durante a alocação;
* alocação e novo quarto durante troca;
* hóspede e viagem durante cadastro de traslado.

---

# 16. Hospedagem e acomodação

### Definido

Na versão atual, a hospedagem operacional é representada principalmente através das informações da viagem, dos quartos e das alocações.

Não existem atualmente domínios independentes implementados para:

```text
Hospedagem
Cama
Reserva
```

Esses conceitos não devem ser tratados pela documentação como funcionalidades existentes.

### Planejada

Caso a operação futura exija múltiplas propriedades, identificação individual de camas ou reservas independentes, esses conceitos poderão ser introduzidos como novos domínios.

A necessidade deverá ser validada com a operação real antes da implementação.

---

# 17. Experiência do hóspede

### Definida

O BTHS Platform terá uma área específica para o hóspede dentro da mesma aplicação utilizada pela equipe.

A interface deverá apresentar somente informações relevantes ao hóspede autenticado.

A estrutura prevista inclui:

```text
/app
/app/minha-viagem
/app/minha-hospedagem
/app/meu-traslado
/app/meu-qr
/app/avisos
/app/perfil
```

A equipe administra a operação.

O hóspede acompanha sua própria experiência de viagem.

---

## 17.1 Próximo passo

### Definida

A área inicial do hóspede deverá priorizar informações acionáveis, indicando o que ele precisa saber ou fazer naquele momento da viagem.

---

## 17.2 Meu QR

### Definida

O hóspede deverá conseguir acessar sua identificação operacional por QR Code pela própria área autenticada.

O frontend deverá utilizar o identificador gerenciado pelo backend.

---

## 17.3 Minha hospedagem

### Definida

O hóspede deverá conseguir visualizar as informações de hospedagem disponíveis para sua viagem, incluindo sua alocação quando existente.

---

## 17.4 Meu transporte

### Definida

O hóspede deverá visualizar somente os traslados relacionados à sua própria participação.

---

## 17.5 Avisos

### Planejada

A plataforma deverá evoluir para centralizar avisos e orientações relevantes aos participantes da viagem.

Notificações push poderão ser incorporadas posteriormente.

---

# 18. Funcionalidades planejadas

Os seguintes recursos fazem parte da evolução prevista, mas não devem ser tratados como funcionalidades atualmente implementadas:

* frontend PWA completo;
* endpoints específicos para experiência do HOSPEDE;
* motoristas;
* veículos;
* notificações;
* push notifications;
* integração com mapas;
* relatórios adicionais;
* métricas operacionais adicionais;
* recursos avançados de auditoria;
* infraestrutura completa com Docker e Docker Compose.

A inclusão de novas funcionalidades deverá preservar os princípios e regras já estabelecidos.

---

# 19. Evolução das regras

Este documento é evolutivo.

Novas regras podem surgir conforme:

* o desenvolvimento avançar;
* o frontend expuser novas necessidades;
* a operação real identificar novos cenários;
* o sistema começar a ser utilizado em produção;
* novos módulos forem incorporados.

Uma regra planejada não deve ser considerada implementada apenas por estar documentada.

Sempre que possível, mudanças relevantes nas regras de negócio devem ser atualizadas neste documento junto da implementação correspondente.

---

# 20. Regra principal

> **O sistema deve refletir a operação real da viagem e não obrigar a operação a se adaptar a uma complexidade desnecessária do sistema.**

Essa regra orienta a evolução do BTHS Platform.

A tecnologia deve servir à operação da Beat Trips e à experiência do hóspede, e não o contrário.
