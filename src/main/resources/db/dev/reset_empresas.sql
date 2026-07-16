TRUNCATE TABLE empresa RESTART IDENTITY;

INSERT INTO empresa (
    razao_social,
    nome_fantasia,
    cnpj,
    ramo_atividade,
    data_fundacao,
    tipo_empresa
) VALUES
(
    'Tech Solucoes Digitais Ltda',
    'TechSol',
    '11222333000181',
    'Tecnologia da Informacao',
    '2015-03-12',
    'LTDA'
),
(
    'Comercio Bom Preco ME',
    'Bom Preco',
    '22333444000172',
    'Varejo',
    '2018-07-01',
    'MEI'
),
(
    'Industria Norte Alimentos SA',
    'Norte Alimentos',
    '33444555000163',
    'Alimentos',
    '2008-11-20',
    'SA'
),
(
    'Consultoria Horizonte EIRELI',
    'Horizonte Consultoria',
    '44555666000154',
    'Consultoria Empresarial',
    '2019-01-15',
    'EIRELI'
);
