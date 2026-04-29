-- ============================================================
-- V2: Otimização da tabela agendamento
-- - Remove colunas redundantes (empreendimento_id, duracao_minutos)
-- - Adiciona colunas para dados do participante e auditoria
-- - Adiciona FK para tipo_evento
-- ============================================================

-- Remove colunas redundantes
ALTER TABLE agendamento DROP INDEX idx_agendamento_empreendimento;
ALTER TABLE agendamento DROP COLUMN empreendimento_id;
ALTER TABLE agendamento DROP COLUMN duracao_minutos;

-- Adiciona dados do participante
ALTER TABLE agendamento ADD COLUMN nome_participante VARCHAR(150) AFTER corretor_id;
ALTER TABLE agendamento ADD COLUMN email_participante VARCHAR(255) AFTER nome_participante;

-- Adiciona observações e motivo
ALTER TABLE agendamento ADD COLUMN observacoes TEXT AFTER email_participante;
ALTER TABLE agendamento ADD COLUMN motivo VARCHAR(500) AFTER observacoes;

-- FK para tipo_evento (integridade referencial)
ALTER TABLE agendamento ADD CONSTRAINT fk_agendamento_tipo_evento
    FOREIGN KEY (event_type_id) REFERENCES tipo_evento(id);
