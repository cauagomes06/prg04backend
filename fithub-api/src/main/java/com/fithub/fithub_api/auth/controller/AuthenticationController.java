package com.fithub.fithub_api.auth.controller;// ...
import com.fithub.fithub_api.auth.dto.LoginRequestDto;
import com.fithub.fithub_api.auth.dto.TokenResponseDto;
import com.fithub.fithub_api.infraestructure.jwt.JwtTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// ...

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        // GERA O TOKEN REAL
        String token = tokenService.generateToken(authentication);

        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}