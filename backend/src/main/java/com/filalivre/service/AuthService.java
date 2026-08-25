package com.filalivre.service;

import com.filalivre.dto.CadastroRequest;
import com.filalivre.dto.LoginRequest;
import com.filalivre.dto.UsuarioResponse;
import com.filalivre.model.Perfil;
import com.filalivre.model.Usuario;
import com.filalivre.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    public static final Perfil PERFIL_CADASTRO_PUBLICO = Perfil.OPERADOR;

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public AuthService(UsuarioRepository usuarioRepository,
                       AuthenticationManager authenticationManager,
                       SecurityContextRepository securityContextRepository,
                       PasswordEncoder passwordEncoder,
                       AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroRequest req, HttpServletRequest request, HttpServletResponse response) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        usuario.setSenha(passwordEncoder.encode(req.senha()));
        usuario.setPerfil(PERFIL_CADASTRO_PUBLICO);
        usuario = usuarioRepository.save(usuario);

        autenticarESalvarSessao(new LoginRequest(usuario.getEmail(), req.senha()), request, response);
        return UsuarioResponse.de(usuario);
    }

    public UsuarioResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        return autenticarESalvarSessao(req, request, response);
    }

    private UsuarioResponse autenticarESalvarSessao(LoginRequest req, HttpServletRequest request,
                                                    HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(req.email(), req.senha()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            Usuario usuario = (Usuario) authentication.getPrincipal();
            auditoriaService.registrar(usuario, null, "LOGIN", "Usuário autenticado");
            return UsuarioResponse.de(usuario);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
    }
}
