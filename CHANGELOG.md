# Changelog

Todas as mudanças relevantes do **BTHS Platform** serão documentadas neste arquivo.

O projeto utiliza [Versionamento Semântico](https://semver.org/lang/pt-BR/) para organização de suas versões.

---

## [Unreleased]

### Adicionado

### Alterado

### Corrigido

---

## [0.2.0] - 2026-09-18

### Adicionado

- Módulo de traslados V1.
- Associação de traslados a hóspedes e viagens.
- Tipos, status e informações operacionais de traslado.
- Histórico de alterações de status dos traslados.
- Correção administrativa de status com registro de motivo.
- Autenticação utilizando JWT.
- Autorização de endpoints com Spring Security.
- Perfis de usuário `ADMIN`, `STAFF` e `HOSPEDE`.
- Cadastro e gerenciamento de usuários.
- Proteção de senhas utilizando BCrypt.
- Dashboard Operacional V1 por viagem.
- Indicadores de hóspedes, check-in, hospedagem e traslados.
- Consulta dos próximos traslados aguardando.
- Testes específicos de Repository.
- Testes de autenticação, autorização e segurança do Dashboard.
- Testes de validação HTTP para Usuários e Traslados.

### Alterado

- Ampliação da cobertura automatizada dos módulos existentes.
- Reorganização dos enums em pacotes específicos.
- Ampliação das regras de autorização dos endpoints.
- Atualização do roadmap para refletir o estado atual do backend.
- Hospedagem Operacional definida como V1 concluída, mantendo entidades específicas de `Hospedagem` e `Cama` como possíveis evoluções futuras.
- Validações de entrada dos módulos de Usuários e Traslados.

### Corrigido

- Correção da nomenclatura `dataHoraPrvista` para `dataHoraPrevista` no módulo de traslados.
- Tratamento de payloads inválidos de Usuários e Traslados para retorno adequado de erro de validação.
- Validação do motivo de correção de status do traslado para impedir valores vazios ou compostos apenas por espaços.
- Inconsistências de status e checklists no roadmap.

### Qualidade

- Suíte completa validada com **223 testes**.
- **0 falhas, 0 erros e 0 testes ignorados**.
- `BUILD SUCCESS`.

---

## [0.1.0] - 2026-08-27

### Adicionado

- Estrutura inicial do backend com Java e Spring Boot.
- Módulo de viagens.
- Cadastro e gerenciamento de hóspedes.
- Gestão de quartos.
- Alocação de hóspedes em quartos.
- Controle de ocupação.
- Fluxo de check-in de hóspedes.
- Consulta do status de check-in.
- Identificação de hóspedes por QR Code.
- Geração automática de UUID para identificação no check-in.
- Geração dinâmica de QR Code em PNG utilizando ZXing.
- Consulta de hóspedes através do código de check-in.
- Integração entre hóspedes, viagens, quartos e check-in.
- Tratamento centralizado de exceções.
- Testes automatizados dos principais fluxos.
- Testes do módulo de QR Code.
- Validação automatizada da geração de imagens PNG.

### Tecnologias principais

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- JUnit
- Mockito
- ZXing

---

[0.2.0]: https://github.com/Morcineck/bths-platform/releases/tag/v0.2.0
[0.1.0]: https://github.com/Morcineck/bths-platform/releases/tag/v0.1.0