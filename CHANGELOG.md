# Changelog

Todas as mudanças relevantes do **BTHS Platform** serão documentadas neste arquivo.

O projeto utiliza [Versionamento Semântico](https://semver.org/lang/pt-BR/) para organização de suas versões.

---

## [Unreleased]

### Adicionado

### Alterado

### Corrigido

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

[0.1.0]: https://github.com/Morcineck/bths-platform/releases/tag/v0.1.0