-- Perfis (Adicionando data_criacao e data_modificacao)
INSERT INTO perfis (id, nome, descricao, data_criacao, data_modificacao)
VALUES (1, 'ROLE_ADMIN', 'Administrador do sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO perfis (id, nome, descricao, data_criacao, data_modificacao)
VALUES (2, 'ROLE_PERSONAL', 'Profissional de educação física', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO perfis (id, nome, descricao, data_criacao, data_modificacao)
VALUES (3, 'ROLE_CLIENTE', 'Cliente da academia', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Planos (Adicionando data_criacao e data_modificacao)
INSERT INTO planos (id, nome, descricao, preco, data_criacao, data_modificacao)
VALUES (1, 'Fit', 'Acesso básico à academia.', 99.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO planos (id, nome, descricao, preco, data_criacao, data_modificacao)
VALUES (2, 'Pro', 'Acesso completo com aulas em grupo.', 129.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO planos (id, nome, descricao, preco, data_criacao, data_modificacao)
VALUES (3, 'Premium', 'Acesso VIP com acompanhamento.', 159.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);