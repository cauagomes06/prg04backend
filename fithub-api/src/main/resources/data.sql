-- Inserir os perfis/funções básicos na tabela 'perfis'
INSERT INTO perfis (id, nome, descricao) VALUES (1, 'ROLE_ADMIN', 'Administrador do sistema') ON DUPLICATE KEY UPDATE nome=nome;
INSERT INTO perfis (id, nome, descricao) VALUES (2, 'ROLE_PERSONAL', 'Profissional de educação física') ON DUPLICATE KEY UPDATE nome=nome;
INSERT INTO perfis (id, nome, descricao) VALUES (3, 'ROLE_CLIENTE', 'Cliente da academia') ON DUPLICATE KEY UPDATE nome=nome;

-- Inserir os planos básicos na tabela 'planos'
INSERT INTO planos (id, nome, descricao, preco) VALUES (1, 'Fit', 'Acesso básico à academia.', 99.90) ON DUPLICATE KEY UPDATE nome=nome;
INSERT INTO planos (id, nome, descricao, preco) VALUES (2, 'Pro', 'Acesso completo com aulas em grupo.', 129.90) ON DUPLICATE KEY UPDATE nome=nome;
INSERT INTO planos (id, nome, descricao, preco) VALUES (3, 'Premium', 'Acesso VIP com acompanhamento.', 159.90) ON DUPLICATE KEY UPDATE nome=nome;