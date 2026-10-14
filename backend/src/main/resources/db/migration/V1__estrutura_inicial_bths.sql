-- BTHS Platform - Flyway V1: baseline do schema legado MySQL 8.4
-- Reproduz a estrutura preexistente de bths_platform, SEM os dados de teste.
-- Para bancos vazios: Flyway executa V1 e V2 sequencialmente.
-- Para bancos existentes: conferir equivalencia do schema antes de aplicar baseline na versao 1;
-- apos baseline, V2 executa os ajustes necessarios.
-- NAO executar V1 diretamente em um banco com tabelas existentes.
-- Manter intencionalmente em V1: orientecao_hospede (legada) e status CONFIRMADO.
-- Ambos sao corrigidos na V2; alteracoes em V1 apos publicacao quebram o checksum Flyway.

-- viagens
CREATE TABLE `viagens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cidade` varchar(255) DEFAULT NULL,
  `data_fim` date NOT NULL,
  `data_inicio` date NOT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `evento` varchar(255) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `status` enum('CANCELADA','EM_ANDAMENTO','FINALIZADA','PLANEJADA') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- usuarios
CREATE TABLE `usuarios` (
  `id` binary(16) NOT NULL,
  `ativo` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `perfil` enum('ADMIN','HOSPEDE','STAFF') NOT NULL,
  `senha` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkfsp0s1tflm1cwlj8idhqsad0` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- motoristas
CREATE TABLE `motoristas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `nome_completo` varchar(255) NOT NULL,
  `observacao` text,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- veiculos
CREATE TABLE `veiculos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `capacidade_passageiros` int NOT NULL,
  `modelo` varchar(255) NOT NULL,
  `observacao` text,
  `placa` varchar(7) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_veiculos_placa` (`placa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- hospedagens
CREATE TABLE `hospedagens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cidade` varchar(255) DEFAULT NULL,
  `contato_nome` varchar(255) DEFAULT NULL,
  `contato_telefone` varchar(255) DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `horario_check_in` time DEFAULT NULL,
  `horario_check_out` time DEFAULT NULL,
  `imagem_url` varchar(255) DEFAULT NULL,
  `localizacao_url` varchar(255) DEFAULT NULL,
  `nome` varchar(255) NOT NULL,
  `observacao_publica` varchar(1000) DEFAULT NULL,
  `wifi_nome` varchar(255) DEFAULT NULL,
  `wifi_senha` varchar(255) DEFAULT NULL,
  `viagens_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeoiyrql8oxr0j6drerjdd9kv0` (`viagens_id`),
  CONSTRAINT `FKeoiyrql8oxr0j6drerjdd9kv0` FOREIGN KEY (`viagens_id`) REFERENCES `viagens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- hospedes
CREATE TABLE `hospedes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cpf` varchar(11) NOT NULL,
  `data_nascimento` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `horario_previsto_chegada` datetime(6) DEFAULT NULL,
  `nome_completo` varchar(255) NOT NULL,
  `status_check_in` enum('NAO_COMPARECEU','PENDENTE','REALIZADO') NOT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `viagem_id` bigint NOT NULL,
  `data_hora_check_in` datetime(6) DEFAULT NULL,
  `observacao_check_in` varchar(255) DEFAULT NULL,
  `responsavel_check_in` varchar(255) DEFAULT NULL,
  `codigo_check_in` varchar(255) DEFAULT NULL,
  `data_hora_nao_comparecimento` datetime(6) DEFAULT NULL,
  `motivo_nao_comparecimento` varchar(255) DEFAULT NULL,
  `responsavel_nao_comparecimento` varchar(255) DEFAULT NULL,
  `usuario_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hospede_cpf_viagem` (`cpf`,`viagem_id`),
  UNIQUE KEY `UKr2h1wnscte9vfanssuq66darj` (`codigo_check_in`),
  KEY `FKikyg1vybtrgw8n9pihwt5rurt` (`viagem_id`),
  KEY `FKl01yjqxpea7a8vb9dl0w9s7o7` (`usuario_id`),
  CONSTRAINT `FKikyg1vybtrgw8n9pihwt5rurt` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`),
  CONSTRAINT `FKl01yjqxpea7a8vb9dl0w9s7o7` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- quartos
CREATE TABLE `quartos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `capacidade` int NOT NULL,
  `nome` varchar(255) NOT NULL,
  `status` enum('DISPONIVEL','INDISPONIVEL','LOTADO') NOT NULL,
  `tipo` enum('ALOJAMENTO','SUITE') NOT NULL,
  `viagem_id` bigint NOT NULL,
  `hospedagem_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsd70jso50jet0q2jipcpio7ia` (`viagem_id`),
  KEY `FKgwdn8416kl9c2eurc5r7877aa` (`hospedagem_id`),
  CONSTRAINT `FKgwdn8416kl9c2eurc5r7877aa` FOREIGN KEY (`hospedagem_id`) REFERENCES `hospedagens` (`id`),
  CONSTRAINT `FKsd70jso50jet0q2jipcpio7ia` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- agenda_viagem
CREATE TABLE `agenda_viagem` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `data_hora_fim` datetime(6) DEFAULT NULL,
  `data_hora_inicio` datetime(6) NOT NULL,
  `descricao` varchar(1000) DEFAULT NULL,
  `ordem` int NOT NULL,
  `tipo` enum('CHECK_IN','CHECK_OUT','CHEGADA','FESTIVAL','OUTRO','WELCOME_BEAT') NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `visivel_hospede` bit(1) NOT NULL,
  `viagem_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe7mduku3ard43txs7531hujo5` (`viagem_id`),
  CONSTRAINT `FKe7mduku3ard43txs7531hujo5` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- avisos
CREATE TABLE `avisos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `data_publicacao` datetime(6) NOT NULL,
  `mensagem` varchar(255) NOT NULL,
  `tipo` enum('EVENTO','HOSPEDAGEM','IMPORTANTE','INFORMATIVO','TRANSPORTE') NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `viagem_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoo7avlh876fg72tcp0o74tblt` (`viagem_id`),
  CONSTRAINT `FKoo7avlh876fg72tcp0o74tblt` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- alocacao_quartos
CREATE TABLE `alocacao_quartos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_alocacao` datetime(6) NOT NULL,
  `hospede_id` bigint NOT NULL,
  `quarto_id` bigint NOT NULL,
  `viagem_id` bigint NOT NULL,
  `tipo_cama` enum('BELICHE','CASAL') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_alocacao_hospede_viagem` (`hospede_id`,`viagem_id`),
  KEY `FKlca335a1bnvsb27ui2u6bgg7l` (`quarto_id`),
  KEY `FKhckp2f6rxus62ub9txhjokckn` (`viagem_id`),
  CONSTRAINT `FKfng9an1aaeprur3r6tmfkcuuj` FOREIGN KEY (`hospede_id`) REFERENCES `hospedes` (`id`),
  CONSTRAINT `FKhckp2f6rxus62ub9txhjokckn` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`),
  CONSTRAINT `FKlca335a1bnvsb27ui2u6bgg7l` FOREIGN KEY (`quarto_id`) REFERENCES `quartos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- operacoes_traslado
CREATE TABLE `operacoes_traslado` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `motorista_id` bigint DEFAULT NULL,
  `veiculo_id` bigint DEFAULT NULL,
  `viagem_id` bigint NOT NULL,
  `aeroporto` enum('CGH','GRU','VCP') DEFAULT NULL,
  `local_destino` varchar(255) NOT NULL,
  `local_origem` varchar(255) NOT NULL,
  `observacao` text,
  `status` enum('AGUARDANDO','CANCELADO','CONCLUIDO','EM_ANDAMENTO') NOT NULL,
  `tipo` enum('AEROPORTO_PARA_HOSPEDAGEM','HOSPEDAGEM_PARA_AEROPORTO','OUTRO') NOT NULL,
  `data_hora_prevista` datetime NOT NULL,
  `orientecao_hospede` text,
  `orientacao_hospede` text,
  PRIMARY KEY (`id`),
  KEY `FK59lkg71nbs08qwo2lbrhlshnj` (`motorista_id`),
  KEY `FK6ifa9ou121yx7vx2q785sdd8s` (`veiculo_id`),
  KEY `FKe9uebljcnx6vdfw35hfa26y0f` (`viagem_id`),
  CONSTRAINT `FK59lkg71nbs08qwo2lbrhlshnj` FOREIGN KEY (`motorista_id`) REFERENCES `motoristas` (`id`),
  CONSTRAINT `FK6ifa9ou121yx7vx2q785sdd8s` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`),
  CONSTRAINT `FKe9uebljcnx6vdfw35hfa26y0f` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- traslados
CREATE TABLE `traslados` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aeroporto` enum('CGH','GRU','VCP') DEFAULT NULL,
  `companhia_aerea` varchar(255) DEFAULT NULL,
  `data_hora_prevista` datetime(6) NOT NULL,
  `local_destino` varchar(255) NOT NULL,
  `local_origem` varchar(255) NOT NULL,
  `numero_voo` varchar(255) DEFAULT NULL,
  `observacao` text,
  `status` enum('AGUARDANDO','CANCELADO','CONCLUIDO','CONFIRMADO','EM_ANDAMENTO') NOT NULL,
  `tipo` enum('AEROPORTO_PARA_HOSPEDAGEM','HOSPEDAGEM_PARA_AEROPORTO','OUTRO') NOT NULL,
  `hospede_id` bigint NOT NULL,
  `viagem_id` bigint NOT NULL,
  `motorista_id` bigint DEFAULT NULL,
  `veiculo_id` bigint DEFAULT NULL,
  `operacao_traslado_id` bigint DEFAULT NULL,
  `orientacao_hospede` text,
  PRIMARY KEY (`id`),
  KEY `FK1xy9eis9xml1qw2tg28vthey4` (`hospede_id`),
  KEY `FKiepqemkb8e9qyg6ijeh3sdajw` (`viagem_id`),
  KEY `FKr1fcpxfiy14atpkkulumc6qn1` (`motorista_id`),
  KEY `FK4vaejevbw9x2j584qyv5p6w1v` (`veiculo_id`),
  KEY `FK7wfsyv4b6oev2vcbvly2yvji7` (`operacao_traslado_id`),
  CONSTRAINT `FK1xy9eis9xml1qw2tg28vthey4` FOREIGN KEY (`hospede_id`) REFERENCES `hospedes` (`id`),
  CONSTRAINT `FK4vaejevbw9x2j584qyv5p6w1v` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`),
  CONSTRAINT `FK7wfsyv4b6oev2vcbvly2yvji7` FOREIGN KEY (`operacao_traslado_id`) REFERENCES `operacoes_traslado` (`id`),
  CONSTRAINT `FKiepqemkb8e9qyg6ijeh3sdajw` FOREIGN KEY (`viagem_id`) REFERENCES `viagens` (`id`),
  CONSTRAINT `FKr1fcpxfiy14atpkkulumc6qn1` FOREIGN KEY (`motorista_id`) REFERENCES `motoristas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- historico_status_operacao_traslado
CREATE TABLE `historico_status_operacao_traslado` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_hora` datetime(6) NOT NULL,
  `motivo` varchar(255) NOT NULL,
  `novo_status` enum('AGUARDANDO','CANCELADO','CONCLUIDO','EM_ANDAMENTO') NOT NULL,
  `status_anterior` enum('AGUARDANDO','CANCELADO','CONCLUIDO','EM_ANDAMENTO') NOT NULL,
  `operacao_traslado_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtfw6v4ndelrcx50u626gk3ujm` (`operacao_traslado_id`),
  CONSTRAINT `FKtfw6v4ndelrcx50u626gk3ujm` FOREIGN KEY (`operacao_traslado_id`) REFERENCES `operacoes_traslado` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- historico_status_traslado
CREATE TABLE `historico_status_traslado` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_hora` datetime(6) NOT NULL,
  `motivo` varchar(255) NOT NULL,
  `novo_status` enum('AGUARDANDO','CANCELADO','CONCLUIDO','EM_ANDAMENTO') NOT NULL,
  `traslado_anterior` enum('AGUARDANDO','CANCELADO','CONCLUIDO','EM_ANDAMENTO') NOT NULL,
  `traslado_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc5r2md5hb9rg3vprj7pipf1x3` (`traslado_id`),
  CONSTRAINT `FKc5r2md5hb9rg3vprj7pipf1x3` FOREIGN KEY (`traslado_id`) REFERENCES `traslados` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
