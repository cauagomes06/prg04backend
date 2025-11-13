package com.fithub.fithub_api.usuario.mapper;

import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.perfil.repository.PerfilRepository;
import com.fithub.fithub_api.pessoa.entity.Pessoa;
import com.fithub.fithub_api.plano.repository.PlanoRepository;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.pessoa.dto.PessoaResponseDto;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component // 1. Torne-o um bean do Spring
@RequiredArgsConstructor // 2. Para injetar as dependências
public class UsuarioMapper {

    public static final ModelMapper modelMapper = new ModelMapper();
    private final PerfilRepository perfilRepository;
    private final PlanoRepository planoRepository;
    private final PasswordEncoder passwordEncoder;

    public  Usuario toUsuario(UsuarioCreateDto createDto) {
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

            UsuarioResponseDto responseDto = new UsuarioResponseDto();
            responseDto.setId(usuario.getId());
            responseDto.setUsername(usuario.getUsername());
            responseDto.setScoreTotal(usuario.getScoreTotal());
            responseDto.setDataCriacao(usuario.getDataCriacao());

            // Verificação de segurança para Pessoa
            if (usuario.getPessoa() != null) {
                PessoaResponseDto pessoaDto = new PessoaResponseDto();
                pessoaDto.setNomeCompleto(usuario.getPessoa().getNomeCompleto());
                pessoaDto.setTelefone(usuario.getPessoa().getTelefone());
                responseDto.setPessoa(pessoaDto);
            }

            // Verificação de segurança para Plano
            if (usuario.getPlano() != null) {
                responseDto.setNomePlano(usuario.getPlano().getNome());
                responseDto.setPlanoId(usuario.getPlano().getId());
            }

            // Verificação de segurança para Perfil
            if (usuario.getPerfil() != null) {
                responseDto.setNomePerfil(usuario.getPerfil().getNome());
            }

            return responseDto;
        }


    public List<UsuarioResponseDto> toListDto(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toDto).collect(Collectors.toList());
    }

    public static UsuarioRankingDto toRankingDto(Usuario usuario) {
        // Mapeamento automático dos campos com nomes iguais
        UsuarioRankingDto dto = modelMapper.map(usuario, UsuarioRankingDto.class);

        // Mapeamento manual de campos aninhados ou com nomes diferentes
        if (usuario.getPessoa() != null) {
            dto.setNomeCompleto(usuario.getPessoa().getNomeCompleto());
        }

        dto.setUsuarioId(usuario.getId());

        return dto;
    }

    public static List<UsuarioRankingDto> toRankingListDto(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioMapper::toRankingDto)
                .collect(Collectors.toList());
    }

    public static InstrutorResponseDto toInstrutorDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        InstrutorResponseDto dto = new InstrutorResponseDto();
        dto.setId(usuario.getId());

        if (usuario.getPessoa() != null) {
            dto.setNomeCompleto(usuario.getPessoa().getNomeCompleto());

        } else {
            dto.setNomeCompleto("Instrutor (Pendente)");
        }

        return dto;
    }

    //
    public static List<InstrutorResponseDto> toListInstrutorDto(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioMapper::toInstrutorDto)
                .collect(Collectors.toList());
    }
}
