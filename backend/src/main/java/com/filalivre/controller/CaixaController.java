package com.filalivre.controller;

import com.filalivre.dto.CaixaRequest;
import com.filalivre.dto.CaixaResponse;
import com.filalivre.dto.TotaisRequest;
import com.filalivre.model.Usuario;
import com.filalivre.service.CaixaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caixas")
public class CaixaController {

    private final CaixaService caixaService;

    public CaixaController(CaixaService caixaService) {
        this.caixaService = caixaService;
    }

    @GetMapping
    public List<CaixaResponse> listar() {
        return caixaService.listar();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CaixaResponse criar(@Valid @RequestBody CaixaRequest req) {
        return caixaService.criar(req);
    }

    @PostMapping("/{id}/iniciar")
    public CaixaResponse iniciarAtendimento(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return caixaService.iniciarAtendimento(id, usuario);
    }

    @PostMapping("/{id}/espera")
    public CaixaResponse alternarEspera(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return caixaService.alternarEspera(id, usuario);
    }

    @PostMapping("/{id}/finalizar")
    public CaixaResponse finalizarAtendimento(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return caixaService.finalizarAtendimento(id, usuario);
    }

    @PutMapping("/{id}/totais")
    public CaixaResponse atualizarTotais(@PathVariable Long id,
                                         @Valid @RequestBody TotaisRequest req,
                                         @AuthenticationPrincipal Usuario usuario) {
        return caixaService.atualizarTotais(id, usuario, req);
    }
}
