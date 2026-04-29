-- ============================================================
-- V1: Estrutura base do cal-service
-- Tabelas: tipo_evento, agendamento
-- ============================================================

-- Tabela de tipos de evento (sincronizada com Cal.com)
CREATE TABLE IF NOT EXISTS tipo_evento (
    id BIGINT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(500),
    duracao_minutos INT NOT NULL DEFAULT 60,
    antecedencia_minima INT NOT NULL DEFAULT 120,
    oculto TINYINT(1) NOT NULL DEFAULT 0,
    empreendimento_id BIGINT NOT NULL,
    INDEX idx_tipo_evento_empreendimento (empreendimento_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela de agendamentos (integração com Cal.com)
CREATE TABLE IF NOT EXISTS agendamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_uid VARCHAR(255) UNIQUE,
    event_type_id BIGINT,
    cliente_id BIGINT NOT NULL,
    corretor_id BIGINT NOT NULL,
    empreendimento_id BIGINT NOT NULL,
    duracao_minutos INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_agendamento_cliente (cliente_id),
    INDEX idx_agendamento_corretor (corretor_id),
    INDEX idx_agendamento_empreendimento (empreendimento_id),
    INDEX idx_agendamento_status (status),
    INDEX idx_agendamento_booking_uid (booking_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
