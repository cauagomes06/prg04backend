package com.fithub.fithub_api.web.dto.mapper;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.perfil.repository.PerfilRepository;
import com.fithub.fithub_api.pessoa.entity.Pessoa;
import com.fithub.fithub_api.plano.repository.PlanoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.web.dto.PessoaResponseDto;
import com.fithub.fithub_api.web.dto.UsuarioCreateDto;
import com.fithub.fithub_api.web.dto.UsuarioResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component // 1. Torne-o um bean do Spring
@RequiredArgsConstructor // 2. Para injetar as dependências
public class UsuarioMapper {

    private final ModelMapper modelMapper;
    private final PerfilRepository perfilRepository;
    private final PlanoRepository planoRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario toUsuario(UsuarioCreateDto createDto) {
        // Mapeia os campos simples primeiro
        Usuario usuario = modelMapper.map(createDto, Usuario.class);

        //objeto pessoa é criado junto com o usuario
        Pessoa pessoa = new Pessoa();
        pessoa.setNomeCompleto(createDto.getPessoa().getNomeCompleto());
        pessoa.setCpf(createDto.getPessoa().getCpf());
        pessoa.setTelefone(createDto.getPessoa().getTelefone());
        usuario.setPessoa(pessoa);

        usuario.setPerfil(perfilRepository.findById(createDto.getPerfil())
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado")));

        usuario.setPlano(planoRepository.findById(createDto.getPlano())
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado")));
        // 4. Codifica a senha
        usuario.setPassword(passwordEncoder.encode(createDto.getPassword()));

        return usuario;
    }


        public UsuarioResponseDto toDto(Usuario usuario) {
            if (usuario == null) {
                return null;
            }

            // Mapeia os dados da Pessoa
            PessoaResponseDto pessoaDto = new PessoaResponseDto();
            pessoaDto.setNomeCompleto(usuario.getPessoa().getNomeCompleto());
            pessoaDto.setTelefone(usuario.getPessoa().getTelefone());

            // Mapeia os dados principais do Usuario
            UsuarioResponseDto responseDto = new UsuarioResponseDto();
            responseDto.setId(usuario.getId());
            responseDto.setUsername(usuario.getUsername());
            responseDto.setPessoa(pessoaDto);

            // Pega APENAS o nome do Plano e do Perfil
            responseDto.setNomePlano(usuario.getPlano().getNome());
            responseDto.setNomePerfil(usuario.getPerfil().getNome());

            return responseDto;
        }


    public List<UsuarioResponseDto> toListDto(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toDto).collect(Collectors.toList());
    }
}