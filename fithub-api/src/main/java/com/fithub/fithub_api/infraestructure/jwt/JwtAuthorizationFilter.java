package com.fithub.fithub_api.infraestructure.jwt;

import com.fithub.fithub_api.infraestructure.jwt.JwtTokenService;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UsuarioService usuarioService; // O nosso UserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 1. Verifica se o cabeçalho existe e se começa com "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7); // Extrai apenas o token
            try {
                username = jwtTokenService.getUsernameFromToken(jwt);
            } catch (Exception e) {
                // Token inválido (expirado, assinatura errada, etc.)
                logger.warn("Não foi possível extrair o username do token JWT.", e);
            }
        }

        // 2. Se temos um username e o utilizador ainda não está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Carrega os detalhes do utilizador a partir do banco
            UserDetails userDetails = this.usuarioService.loadUserByUsername(username);

            // 3. Valida o token (compara com os dados do banco e verifica expiração)
            if (jwtTokenService.validateToken(jwt, userDetails)) {

                // 4. Cria a autenticação e coloca-a no Contexto de Segurança do Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}