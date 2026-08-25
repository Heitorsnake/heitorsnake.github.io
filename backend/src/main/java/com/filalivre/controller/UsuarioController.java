package com.filalivre.controller;

import com.filalivre.dto.UsuarioAdminRequest;
import com.filalivre.dto.UsuarioResponse;
import com.filalivre.model.Usuario;
import com.filalivre.repository.UsuarioRepository;
import com.filalivre.service.AuditoriaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder,
                             AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioAdminRequest req,
                                 @AuthenticationPrincipal Usuario logado) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        usuario.setSenha(passwordEncoder.encode(req.senha()));
        usuario.setPerfil(req.perfil());
        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar(logado, null, "USUARIO_CRIADO",
                "Usuário " + usuario.getEmail() + " criado com perfil " + usuario.getPerfil());
        return UsuarioResponse.de(usuario);
    }

    @PatchMapping("/{id}/ativo")
    public UsuarioResponse alternarAtivo(@PathVariable Long id, @AuthenticationPrincipal Usuario logado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (usuario.getId().equals(logado.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode desativar a si mesmo");
        }
        usuario.setAtivo(!usuario.isAtivo());
        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar(logado, null, usuario.isAtivo() ? "USUARIO_ATIVADO" : "USUARIO_DESATIVADO",
                "Usuário " + usuario.getEmail());
        return UsuarioResponse.de(usuario);
    }
}
