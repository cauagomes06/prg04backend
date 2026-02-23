    package com.fithub.fithub_api.treino.service;

    import com.fithub.fithub_api.conquista.service.ConquistaIService;
    import com.fithub.fithub_api.exception.EntityNotFoundException;
    import com.fithub.fithub_api.exercicio.entity.Exercicio;
    import com.fithub.fithub_api.exercicio.service.ExercicioService;
    import com.fithub.fithub_api.infraestructure.SecurityUtils;
    import com.fithub.fithub_api.itemtreino.entity.ItemTreino;
    import com.fithub.fithub_api.treino.dto.TreinoResponseDto;
    import com.fithub.fithub_api.treino.entity.StatusTreino;
    import com.fithub.fithub_api.treino.entity.Treino;
    import com.fithub.fithub_api.treino.mapper.TreinoMapper;
    import com.fithub.fithub_api.treino.repository.TreinoRepository;
    import com.fithub.fithub_api.usuario.entity.Usuario;
    import com.fithub.fithub_api.treino.dto.ItemTreinoCreateDto;
    import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
    import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class TreinoService  implements TreinoIService{

        private final TreinoRepository treinoRepository;
        private final ExercicioService exercicioService;
        private final UsuarioRepository usuarioRepository;
        private final SecurityUtils    securityUtils;
        private final ConquistaIService conquistaIService;

        @Override
        @Transactional
        public Treino criarTreino(TreinoCreateDto dto, Usuario criador) {

            // 1. Criar o Treino básico
            Treino treino = new Treino();
            treino.setNome(dto.getNome());
            treino.setDescricao(dto.getDescricao());
            treino.setCriador(criador);
            treino.setStatus(StatusTreino.PRIVADO);

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                List<ItemTreino> listaItens = new ArrayList<>();

                for (ItemTreinoCreateDto itemDto : dto.getItems()) {
                    ItemTreino novoItem = new ItemTreino();
                    novoItem.setSeries(itemDto.getSeries());
                    novoItem.setRepeticoes(itemDto.getRepeticoes());
                    novoItem.setDescanso(itemDto.getDescanso());
                    novoItem.setOrdem(itemDto.getOrdem());

                    Exercicio exercicio = exercicioService.buscarPorId(itemDto.getExercicioId());
                    if (exercicio == null) {
                        // Ajustado o erro: se é null, não conseguimos pegar o nome dele
                        throw new EntityNotFoundException("Exercício com ID " + itemDto.getExercicioId() + " não encontrado");
                    }
                    novoItem.setExercicio(exercicio);
                    novoItem.setTreino(treino);
                    listaItens.add(novoItem);
                }
                treino.setItensTreino(listaItens);
            }

            // 2. Salvar primeiro para garantir que o treino exista no banco
            Treino treinoSalvo = treinoRepository.save(treino);

            //  GATILHO DE CONQUISTAS ===================================================
            try {
                // Agora buscamos o total atualizado (já incluindo o salvo acima)
                long totalCriados = treinoRepository.countByCriadorId(criador.getId());

                conquistaIService.processarProgresso(criador, "TREINOS_CRIADOS", (double) totalCriados);
            } catch (Exception e) {
                // Logamos o erro mas não interrompemos o fluxo do usuário
                System.err.println("Erro ao processar conquista de criador: " + e.getMessage());
            }
            // ============================================================================

            return treinoSalvo;
        }

        @Override
        @Transactional
        public void deletarTreino(Long idTreino, Usuario usuarioLogado) {
            Treino treino = buscarTreinoPorId(idTreino);

            // VERIFICAÇÃO DE POSSE
            //criador ou admin podem apagar
            if (!treino.getCriador().getId().equals(usuarioLogado.getId())
                    && !usuarioLogado.getPerfil().getNome().equals("ROLE_ADMIN")) {
                throw new AccessDeniedException("Você não tem permissão para apagar este treino.");
            }

            treinoRepository.delete(treino);
        }

        @Transactional(readOnly = true)
        public Treino buscarTreinoPorId(Long id) {
            return treinoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Treino com ID #" + id + " não encontrado."));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<Treino> buscarTodosTreinosPublicos(Pageable pageable) {

            return treinoRepository.findPublicos(pageable);
        }

        @Override
        @Transactional
        public Treino editarTreino(TreinoCreateDto dto, Usuario usuarioLogado, Long id) {

            Treino treino = buscarTreinoPorId(id);

            if(!treino.getCriador().getId().equals(usuarioLogado.getId())) {
                throw new AccessDeniedException("Voce nao tem permissao para editar o treino.");
            }

            treino.setNome(dto.getNome());
            treino.setDescricao(dto.getDescricao());

            if (dto.getItems() != null) {
                for (ItemTreinoCreateDto item : dto.getItems()) {

                    Exercicio exercicio = exercicioService.buscarPorId(item.getExercicioId());
                    ItemTreino itemTreino = new ItemTreino();
                    itemTreino.setExercicio(exercicio);
                    itemTreino.setTreino(treino);
                    itemTreino.setSeries(item.getSeries());
                    itemTreino.setDescanso(item.getDescanso());
                    itemTreino.setOrdem(item.getOrdem());
                    itemTreino.setRepeticoes(item.getRepeticoes());

                    treino.getItensTreino().add(itemTreino);
                }
            }

            return treinoRepository.save(treino);
        }

        @Override
        @Transactional(readOnly = true)
        public List<Treino> buscarPorUsuarioId(Long id) {
            return treinoRepository.findByCriadorId(id);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<Treino> buscarTodos(Pageable pageable) {

            return treinoRepository.findAll(pageable);
        }

        @Transactional
        @Override
        public Treino publicarTreino(Long idTreino, Usuario usuarioLogado) {
            Treino treino = buscarTreinoPorId(idTreino);

            boolean ehCriador = treino.getCriador().getId().equals(usuarioLogado.getId());
            boolean ehPersonal = "ROLE_PERSONAL".equals(usuarioLogado.getPerfil().getNome());

            // Se não for criador E não for personal → bloquear
            if (!ehCriador && !ehPersonal) {
                throw new AccessDeniedException("Você não tem permissão para publicar este treino.");
            }

            treino.setStatus(StatusTreino.PUBLICO);
            return treinoRepository.save(treino);
        }

        // No seu TreinoService.java

        @Transactional
        @Override
        public Treino clonarTreino(Long treinoId, Usuario usuarioLogado) {
            // 1. Buscar o treino original
            Treino treinoOriginal = treinoRepository.findById(treinoId)
                    .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

            // 2. Validar se é público (segurança)
            if (!treinoOriginal.getStatus().equals(StatusTreino.PUBLICO)
                    && !treinoOriginal.getCriador().equals(usuarioLogado)) {
                throw new AccessDeniedException("Você não pode copiar este treino privado.");
            }

            // 3. Criar a "Casca" do novo treino
            Treino novoTreino = new Treino();
            novoTreino.setNome(treinoOriginal.getNome() + " (Cópia)"); // Adiciona sufixo para diferenciar
            novoTreino.setDescricao(treinoOriginal.getDescricao());
            novoTreino.setCriador(usuarioLogado); // O dono agora é quem está logado
            novoTreino.setStatus(StatusTreino.PRIVADO); // Começa privado para o usuário editar
            novoTreino.setDataModificacao(LocalDateTime.now());

            // Salva o pai primeiro para ter um ID (dependendo da estratégia de cascade)
            novoTreino = treinoRepository.save(novoTreino);

            // 4. Copiar os Exercícios (Deep Copy dos relacionamentos)
            // Supondo que você tenha uma lista de TreinoExercicio dentro de Treino
            if (treinoOriginal.getItensTreino() != null) {
                List<ItemTreino> novosExercicios = new ArrayList<>();

                for (ItemTreino itemOriginal : treinoOriginal.getItensTreino()) {
                    ItemTreino novoItem = new ItemTreino();

                    // Associa ao novo treino (Pai)
                    novoItem.setTreino(novoTreino);

                    // Mantém a referência ao exercício base (Supino, Agachamento, etc)
                    novoItem.setExercicio(itemOriginal.getExercicio());

                    // Copia as métricas
                    novoItem.setSeries(itemOriginal.getSeries());
                    novoItem.setRepeticoes(itemOriginal.getRepeticoes());
                    novoItem.setOrdem(itemOriginal.getOrdem());
                    novoItem.setDescanso(itemOriginal.getDescanso());


                    novosExercicios.add(novoItem);
                }

                // Se tiver Cascade.ALL, basta setar a lista. Se não, salve os itens via repository.
                novoTreino.setItensTreino(novosExercicios);
                // treinoExercicioRepository.saveAll(novosExercicios); // Caso precise salvar manual
            }

            return treinoRepository.save(novoTreino);
        }

        @Transactional
        @Override
        public void seguirTreino(Long treinoId, Usuario usuarioLogado) {
            Treino treino = buscarTreinoPorId(treinoId);

            // Validações
            if (treino.getStatus() == StatusTreino.PRIVADO && !treino.getCriador().equals(usuarioLogado)) {
                throw new AccessDeniedException("Não é possível seguir um treino privado.");
            }
            if (treino.getCriador().equals(usuarioLogado)) {
                throw new IllegalArgumentException("Você já é o dono deste treino.");
            }

            // Persiste o seguimento
            usuarioLogado.getTreinosAssinados().add(treino);
            usuarioRepository.save(usuarioLogado);

            // GATILHO DE CONQUISTA PARA O CRIADOR ======================================
            try {
                Usuario criador = treino.getCriador();
                long totalSeguidores = treinoRepository.countSeguidoresByTreinoId(treinoId);

                // O motor processa e retorna se houve novas conquistas
                conquistaIService.processarProgresso(criador, "SEGUIDORES_POR_TREINO", (double) totalSeguidores);

            } catch (Exception e) {
                System.err.println("Erro ao processar conquista de popularidade: " + e.getMessage());
            }
            // ===========================================================================
        }

        @Transactional
        @Override
        public void deixarDeSeguirTreino(Long treinoId, Usuario usuarioLogado) {
            // Não precisamos buscar o objeto inteiro no banco só para remover
            // Basta remover da lista qualquer item que tenha esse ID

            boolean removeu = usuarioLogado.getTreinosAssinados()
                    .removeIf(treino -> treino.getId().equals(treinoId));

            if (removeu) {
                usuarioRepository.save(usuarioLogado);
            } else {
                // Opcional: Logar ou lançar erro se tentar deixar de seguir algo que não segue
                throw new EntityNotFoundException("Você não segue este treino.");
            }
        }
        @Override
        @Transactional(readOnly = true)
        public Page<Treino> buscarTreinosPorFiltro(
                String filtro,
                String termo,
                Pageable pageable,
                Usuario usuarioLogado
        ) {

            // Normalização
            String termoNormalizado = (termo != null) ? termo.trim() : null;
            String filtroNormalizado = (filtro != null) ? filtro.toUpperCase() : "RECENTES";

            if (termoNormalizado != null && !termoNormalizado.isEmpty()) {
                return treinoRepository.buscarPorTermo(
                        termoNormalizado,
                        StatusTreino.PUBLICO,
                        pageable
                );
            }

            switch (filtroNormalizado) {

                case "MAIS_SEGUIDOS":
                    return treinoRepository.buscarMaisSeguidos(pageable);

                case "MELHORES_AVALIADOS":
                    return treinoRepository.buscarMelhoresAvaliados(pageable);

                case "SEGUINDO":
                    if (usuarioLogado == null) {
                        return Page.empty(pageable);
                    }
                    return treinoRepository.buscarTreinosDeQuemSigo(
                            usuarioLogado.getId(),
                            pageable
                    );

                case "RECENTES":
                default:
                    return treinoRepository.buscarRecentes(pageable);
            }
        }
        @Override
        @Transactional(readOnly = true)
        public Page<TreinoResponseDto> buscarTreinosPublicosDoUsuario(Long usuarioId, Pageable pageable) {

            // 1. Pega o usuário logado para manter o contexto social (botão de seguir)
            Usuario usuarioLogado = securityUtils.getUsuarioLogado();
            // 2. Procura na base de dados apenas os treinos com status PUBLICO
            Page<Treino> paginaDeTreinos = treinoRepository.findByCriadorIdAndStatus(usuarioId, StatusTreino.PUBLICO, pageable);

            // 3. Converte a Entidade para DTO usando o Mapper injetado no Service
            return paginaDeTreinos.map(treino -> TreinoMapper.toDto(treino, usuarioLogado));
        }

    }
