CREATE TABLE IF NOT EXISTS empresa (
    id              BIGSERIAL    PRIMARY KEY,
    razao_social    VARCHAR(120) NOT NULL,
    nome_fantasia   VARCHAR(120) NOT NULL,
    cnpj            VARCHAR(14)  NOT NULL,
    ramo_atividade  VARCHAR(80)  NOT NULL,
    data_fundacao   DATE         NOT NULL,
    tipo_empresa    VARCHAR(30)  NOT NULL,
    CONSTRAINT uk_empresa_cnpj UNIQUE (cnpj)
);

COMMENT ON COLUMN empresa.tipo_empresa IS 'Valores do enum Java, ex.: MEI, EIRELI, LTDA, SA';
