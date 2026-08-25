package com.filalivre.service;

import com.filalivre.dto.CaixaRequest;
import com.filalivre.dto.CaixaResponse;
import com.filalivre.dto.TotaisRequest;
import com.filalivre.model.Caixa;
import com.filalivre.model.StatusCaixa;
import com.filalivre.model.StatusSolicitacao;
import com.filalivre.model.Usuario;
import com.filalivre.repository.CaixaRepository;
import com.filalivre.repository.SolicitacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaixaService {

    private final CaixaRepository caixaRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final AuditoriaService auditoriaService;

    public CaixaService(CaixaRepository caixaRepository,
                        SolicitacaoRepository solicitacaoRepository,
                        AuditoriaService auditoriaService) {
        this.caixaRepository = caixaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<CaixaResponse> listar() {
        return caixaRepository.findAllByOrderByNumeroAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CaixaResponse criar(CaixaRequest req) {
        if (caixaRepository.existsByNumero(req.numero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um caixa com o número " + req.numero());
        }
        Caixa caixa = new Caixa();
        caixa.setNumero(req.numero());
        caixa = caixaRepository.save(caixa);
        return toResponse(caixa);
    }

    @Transactional
    public CaixaResponse iniciarAtendimento(Long id, Usuario operador) {
        Caixa caixa = buscar(id);
        caixa.setOperador(operador);
        caixa.setStatus(StatusCaixa.NORMAL);
        caixa.setValorCompra(BigDecimal.ZERO);
        caixa.setQtdItens(0);
        caixa.setInicioAtendimento(LocalDateTime.now());
        auditoriaService.registrar(operador, caixa.getNumero(), "INICIO_ATENDIMENTO",
                "Operador assumiu o caixa");
        return toResponse(caixa);
    }

    @Transactional
    public CaixaResponse alternarEspera(Long id, Usuario usuario) {
        Caixa caixa = buscar(id);
        if (caixa.getStatus() == StatusCaixa.SOLICITACAO || caixa.getStatus() == StatusCaixa.APROVACAO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível alterar o status: existe uma solicitação em andamento neste caixa");
        }
        if (caixa.getStatus() == StatusCaixa.NORMAL) {
            caixa.setStatus(StatusCaixa.AGUARDANDO);
            auditoriaService.registrar(usuario, caixa.getNumero(), "CAIXA_EM_ESPERA", "Caixa colocado em espera");
        } else {
            caixa.setStatus(StatusCaixa.NORMAL);
            auditoriaService.registrar(usuario, caixa.getNumero(), "ESPERA_REMOVIDA", "Caixa voltou ao normal");
        }
        return toResponse(caixa);
    }

    @Transactional
    public CaixaResponse finalizarAtendimento(Long id, Usuario usuario) {
        Caixa caixa = buscar(id);
        String detalhes = String.format("Compra finalizada: %d itens, R$ %.2f",
                caixa.getQtdItens(), caixa.getValorCompra());
        caixa.setOperador(null);
        caixa.setStatus(StatusCaixa.NORMAL);
        caixa.setValorCompra(BigDecimal.ZERO);
        caixa.setQtdItens(0);
        caixa.setInicioAtendimento(null);
        auditoriaService.registrar(usuario, caixa.getNumero(), "FIM_ATENDIMENTO", detalhes);
        return toResponse(caixa);
    }

    @Transactional
    public CaixaResponse atualizarTotais(Long id, Usuario operador, TotaisRequest req) {
        Caixa caixa = buscar(id);
        caixa.setValorCompra(req.valorCompra());
        caixa.setQtdItens(req.qtdItens());
        return toResponse(caixa);
    }

    private Caixa buscar(Long id) {
        return caixaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caixa não encontrado"));
    }

    private CaixaResponse toResponse(Caixa c) {
        boolean pendente = solicitacaoRepository.existsByCaixaIdAndStatus(c.getId(), StatusSolicitacao.PENDENTE);
        return new CaixaResponse(
                c.getId(),
                c.getNumero(),
                c.getStatus(),
                c.getValorCompra(),
                c.getQtdItens(),
                c.getOperador() != null ? c.getOperador().getNome() : null,
                c.getInicioAtendimento(),
                pendente);
    }
}
