package com.fithub.fithub_api.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fithub.fithub_api.infraestructure.jwt.JwtTokenService;
import com.fithub.fithub_api.pessoa.dto.PessoaCreateDto;
import com.fithub.fithub_api.pessoa.service.PessoaService;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mocks dos serviços usados pelo Controller
    @MockitoBean private UsuarioService usuarioService;
    @MockitoBean private UsuarioMapper usuarioMapper;
    @MockitoBean private PessoaService pessoaService;
    @MockitoBean private JwtTokenService jwtTokenService;

    // Mockamos o contexto do JPA para satisfazer o @EnableJpaAuditing
    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;
    // -----------------------------

    @Test
    @DisplayName("Deve retornar Erro 422 quando criar utilizador com email inválido")
    void naoDeveCriarComEmailInvalido() throws Exception {
        UsuarioCreateDto dto = new UsuarioCreateDto();
        dto.setUsername("email-errado.com");
        dto.setPassword("senhaForte123");
        dto.setPerfil(1L);
        dto.setPlano(1L);

        PessoaCreateDto pessoa = new PessoaCreateDto();
        pessoa.setNomeCompleto("Teste");
        pessoa.setCpf("12345678900");
        pessoa.setTelefone("999999999");
        dto.setPessoa(pessoa);

        mockMvc.perform(post("/api/usuarios/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    @Test
    @DisplayName("Deve retornar Erro 422 quando a senha for muito curta")
    void naoDeveCriarComSenhaCurta() throws Exception {
        UsuarioCreateDto dto = new UsuarioCreateDto();
        dto.setUsername("teste@fithub.com");
        dto.setPassword("123");
        dto.setPerfil(1L);
        dto.setPlano(1L);

        PessoaCreateDto pessoa = new PessoaCreateDto();
        pessoa.setNomeCompleto("Teste");
        pessoa.setCpf("12345678900");
        pessoa.setTelefone("999999999");
        dto.setPessoa(pessoa);

        mockMvc.perform(post("/api/usuarios/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}