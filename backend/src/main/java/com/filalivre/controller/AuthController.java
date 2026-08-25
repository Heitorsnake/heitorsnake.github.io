package com.filalivre.controller;

import com.filalivre.dto.CadastroRequest;
import com.filalivre.dto.LoginRequest;
import com.filalivre.dto.UsuarioResponse;
import com.filalivre.model.Usuario;
import com.filalivre.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest req,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        return authService.login(req, request, response);
    }

    @PostMapping("/cadastro")
    public UsuarioResponse cadastro(@Valid @RequestBody CadastroRequest req,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        return authService.cadastrar(req, request, response);
    }

    @GetMapping("/eu")
    public UsuarioResponse eu(@AuthenticationPrincipal Usuario usuario) {
        return UsuarioResponse.de(usuario);
    }
}
