package com.filalivre.controller;

import com.filalivre.dto.DecisaoRequest;
import com.filalivre.dto.SolicitacaoRequest;
import com.filalivre.dto.SolicitacaoResponse;
import com.filalivre.model.Usuario;
import com.filalivre.service.SolicitacaoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public SolicitacaoResponse criar(@Valid @RequestBody SolicitacaoRequest req,
                                     @AuthenticationPrincipal Usuario usuario) {
        return solicitacaoService.criar(usuario, req);
    }

    @GetMapping("/minhas")
    public List<SolicitacaoResponse> minhas(@AuthenticationPrincipal Usuario usuario) {
        return solicitacaoService.minhas(usuario);
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'GERENTE', 'ADMINISTRADOR')")
    public List<SolicitacaoResponse> pendentes() {
        return solicitacaoService.pendentes();
    }

    @PostMapping("/{id}/analisar")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'GERENTE', 'ADMINISTRADOR')")
    public SolicitacaoResponse iniciarAnalise(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return solicitacaoService.iniciarAnalise(id, usuario);
    }

    @PostMapping("/{id}/decidir")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'GERENTE', 'ADMINISTRADOR')")
    public SolicitacaoResponse decidir(@PathVariable Long id,
                                       @Valid @RequestBody DecisaoRequest req,
                                       @AuthenticationPrincipal Usuario usuario) {
        return solicitacaoService.decidir(id, usuario, req);
    }

    @GetMapping("/historico")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'GERENTE', 'ADMINISTRADOR')")
    public List<SolicitacaoResponse> historico() {
        return solicitacaoService.historico();
    }
}
