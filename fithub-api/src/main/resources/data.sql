-- src/main/resources/data.sql

-- 1. Insere os Perfis
INSERT INTO public."perfis" ("id", "nome", "descricao", "data_criacao", "data_modificacao", "criado_por", "modificado_por")
VALUES (1, 'ROLE_ADMIN', 'Administrador do sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRACAO', 'MIGRACAO')
ON CONFLICT ("id") DO NOTHING;

INSERT INTO public."perfis" ("id", "nome", "descricao", "data_criacao", "data_modificacao", "criado_por", "modificado_por")
VALUES (2, 'ROLE_PERSONAL', 'Profissional de educação física', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRACAO', 'MIGRACAO')
ON CONFLICT ("id") DO NOTHING;

INSERT INTO public."perfis" ("id", "nome", "descricao", "data_criacao", "data_modificacao", "criado_por", "modificado_por")
VALUES (3, 'ROLE_CLIENTE', 'Cliente da academia', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRACAO', 'MIGRACAO')
ON CONFLICT ("id") DO NOTHING;

--------------------------------------------------------------------------------

-- 2. Insere os Planos
INSERT INTO public."planos" ("id", "nome", "descricao", "preco", "data_criacao", "data_modificacao", "criado_por", "modificado_por")
VALUES (1, 'Fit', 'Acesso básico à academia.', 99.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRACAO', 'MIGRACAO')
ON CONFLICT ("id") DO NOTHING;

INSERT INTO public."planos" ("id", "nome", "descricao", "preco", "data_criacao", "data_modificacao", "criado_por", "modificado_por")
VALUES (2, 'Pro', 'Acesso completo com aulas em grupo.', 129.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRACAO', 'MIGRACAO')
ON CONFLICT ("id") DO NOTHING;

INSERT INTO public."planos" ("id", "nome", "descricao", "preco", "data_criacao", "data_modificacao", "criado_por", "modificado_por")
VALUES (3, 'Premium', 'Acesso VIP com acompanhamento.', 4.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRACAO', 'MIGRACAO')
ON CONFLICT ("id") DO NOTHING;