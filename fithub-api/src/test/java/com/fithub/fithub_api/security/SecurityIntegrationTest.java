package com.fithub.fithub_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fithub.fithub_api.auth.dto.LoginRequestDto;
import com.fithub.fithub_api.pessoa.dto.PessoaCreateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioService usuarioService; // Injeção necessária para criar users

    @Autowired
    private UsuarioRepository usuarioRepository; // Para verificar se já existem

    @Autowired
    private UsuarioMapper usuarioMapper;

    // Tokens para usar nos testes
    private String tokenAdmin;
    private String tokenCliente;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Criar Utilizadores no Banco H2 antes de tentar logar
        // ID 1 = ROLE_ADMIN, ID 3 = ROLE_CLIENTE (conforme seu data.sql)
        criarUsuarioSeNaoExistir("admin@fithub.com", "senha123", 1L);
        criarUsuarioSeNaoExistir("joao.silva@email.com", "senha123", 3L);

        // 2. Obter Tokens (Agora vai funcionar porque os usuários existem)
        tokenAdmin = obterToken("admin@fithub.com", "senha123");
        tokenCliente = obterToken("joao.silva@email.com", "senha123");
    }

    /**
     * Método auxiliar para registar utilizador no banco de testes
     */
    private void criarUsuarioSeNaoExistir(String email, String senha, Long perfilId) {
        if (usuarioRepository.existsByUsername(email)) {
            return;
        }

        UsuarioCreateDto dto = new UsuarioCreateDto();
        dto.setUsername(email);
        dto.setPassword(senha);
        dto.setPerfil(perfilId);
        dto.setPlano(1L); // Assume que o plano ID 1 (Fit) existe no data.sql

        PessoaCreateDto pessoa = new PessoaCreateDto();
        pessoa.setNomeCompleto("Teste User " + perfilId);
        pessoa.setCpf("1234567890" + perfilId); // CPF único fictício
        pessoa.setTelefone("999999999");
        dto.setPessoa(pessoa);

        usuarioService.registrarUsuario(usuarioMapper.toUsuario(dto));
    }

    /**
     * Método auxiliar para fazer login e pegar o token JWT
     */
    private String obterToken(String username, String password) throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        return objectMapper.readTree(jsonResponse).get("token").asText();
    }

    // --- TESTES DE UTILIZADORES ---

    @Test
    @DisplayName("ADMIN deve conseguir listar todos os utilizadores (200 OK)")
    void adminPodeListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CLIENTE NÃO deve conseguir listar todos os utilizadores (403 Forbidden)")
    void clienteNaoPodeListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Ninguém deve conseguir alterar perfil sem ser ADMIN (403 Forbidden)")
    void clienteNaoPodeAlterarPerfil() throws Exception {
        // Tenta promover-se a si mesmo (ID 1 é um exemplo, não importa se existe ou não para o 403)
        mockMvc.perform(patch("/api/usuarios/1/alterar-perfil")
                        .param("novoPerfilId", "1")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    // --- TESTES DE EXERCÍCIOS ---

    @Test
    @DisplayName("CLIENTE não pode criar exercícios (403 Forbidden)")
    void clienteNaoPodeCriarExercicio() throws Exception {
        String novoExercicio = """
                {
                    "nome": "Supino Invertido",
                    "grupoMuscular": "PEITO",
                    "descricao": "Teste de segurança"
                }
                """;

        mockMvc.perform(post("/api/exercicios/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(novoExercicio)
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Visitante (sem token) não pode ver exercícios (403 ou 401)")
    void visitanteNaoPodeVerExercicios() throws Exception {
        // Dependendo da config, pode ser 401 ou 403.
        // Se o entrypoint não estiver customizado, geralmente é 403 ou 401.
        // O mais seguro é verificar se NÃO é 200.
        mockMvc.perform(get("/api/exercicios/buscar"))
                .andExpect(status().isForbidden());
    }
}