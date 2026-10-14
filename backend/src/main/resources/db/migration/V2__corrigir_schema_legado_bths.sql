-- BTHS Platform - Flyway V2: saneamento do schema legado
-- Pre-requisitos para banco existente:
--   SELECT COUNT(*) FROM operacoes_traslado WHERE orientecao_hospede IS NOT NULL; -- deve ser 0
--   SELECT COUNT(*) FROM traslados WHERE status = 'CONFIRMADO'; -- deve ser 0
-- Ambos confirmados no banco local em 13/10/2026.
-- Em MySQL, DDL pode provocar COMMIT implicito. Fazer backup e ensaio antes de aplicar.
-- Nao executar este script manualmente; Flyway deve controlar a ordem e o historico.

-- A entidade Java OperacaoTraslado ja utiliza orientacao_hospede (grafia correta).
ALTER TABLE `operacoes_traslado`
  DROP COLUMN `orientecao_hospede`;

-- Remove o valor legado ausente no enum Java StatusTraslado.
ALTER TABLE `traslados`
  MODIFY COLUMN `status` ENUM('AGUARDANDO','CANCELADO','CONCLUIDO','EM_ANDAMENTO') NOT NULL;
