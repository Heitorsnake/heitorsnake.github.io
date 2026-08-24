package com.filalivre.service;

import com.filalivre.dto.DecisaoRequest;
import com.filalivre.dto.SolicitacaoRequest;
import com.filalivre.dto.SolicitacaoResponse;
import com.filalivre.model.Caixa;
import com.filalivre.model.StatusCaixa;
import com.filalivre.model.StatusSolicitacao;
import com.filalivre.model.Solicitacao;
import com.filalivre.model.Usuario;
import com.filalivre.repository.CaixaRepository;
import com.filalivre.repository.SolicitacaoRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final CaixaRepository caixaRepository;
    private final AuditoriaService auditoriaService;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              CaixaRepository caixaRepository,
                              AuditoriaService auditoriaService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.caixaRepository = caixaRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public SolicitacaoResponse criar(Usuario operador, SolicitacaoRequest req) {
        Caixa caixa = caixaRepository.findById(req.caixaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caixa não encontrado"));

        if (caixa.getOperador() == null || !caixa.getOperador().getId().equals(operador.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não está atendendo este caixa");
        }
        if (solicitacaoRepository.existsByCaixaIdAndStatus(caixa.getId(), StatusSolicitacao.PENDENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este caixa já possui uma solicitação pendente");
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setCaixa(caixa);
        solicitacao.setOperador(operador);
        solicitacao.setTipo(req.tipo());
        solicitacao.setProduto(req.produto());
        solicitacao.setQuantidade(req.quantidade());
        solicitacao.setValor(req.valor());
        solicitacao.setMotivo(req.motivo());
        solicitacao = solicitacaoRepository.save(solicitacao);

        caixa.setStatus(StatusCaixa.SOLICITACAO);
        auditoriaService.registrar(operador, caixa.getNumero(), "SOLICITACAO_CRIADA",
                String.format("%s do produto '%s' (R$ %.2f). Motivo: %s",
                        req.tipo(), req.produto(), req.valor(),
                        req.motivo() == null || req.motivo().isBlank() ? "-" : req.motivo()));
        return toResponse(solicitacao);
    }

    public List<SolicitacaoResponse> minhas(Usuario operador) {
        return solicitacaoRepository.findByOperadorIdOrderByCriadoEmDesc(operador.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<SolicitacaoResponse> pendentes() {
        return solicitacaoRepository.findByStatusOrderByCriadoEmDesc(StatusSolicitacao.PENDENTE)
                .stream().map(this::toResponse).toList();
    }

    public List<SolicitacaoResponse> historico() {
        return solicitacaoRepository.findTop100ByOrderByCriadoEmDesc()
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public SolicitacaoResponse iniciarAnalise(Long id, Usuario gestor) {
        Solicitacao solicitacao = buscar(id);
        exigirPendente(solicitacao);
        solicitacao.getCaixa().setStatus(StatusCaixa.APROVACAO);
        auditoriaService.registrar(gestor, solicitacao.getCaixa().getNumero(), "ANALISE_INICIADA",
                "Gestor iniciou a análise da solicitação #" + solicitacao.getId());
        return toResponse(solicitacao);
    }

    @Transactional
    public SolicitacaoResponse decidir(Long id, Usuario gestor, DecisaoRequest req) {
        Solicitacao solicitacao = buscar(id);
        exigirPendente(solicitacao);

        solicitacao.setStatus(req.aprovar() ? StatusSolicitacao.APROVADA : StatusSolicitacao.RECUSADA);
        solicitacao.setDecididoEm(LocalDateTime.now());
        solicitacao.setDecididoPor(gestor);
        solicitacao.getCaixa().setStatus(StatusCaixa.NORMAL);

        auditoriaService.registrar(gestor, solicitacao.getCaixa().getNumero(),
                req.aprovar() ? "SOLICITACAO_APROVADA" : "SOLICITACAO_RECUSADA",
                String.format("Decisão sobre solicitação #%d (%s de '%s')",
                        solicitacao.getId(), solicitacao.getTipo(), solicitacao.getProduto()));
        return toResponse(solicitacao);
    }

    private void exigirPendente(Solicitacao s) {
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta solicitação já foi decidida");
        }
    }

    private Solicitacao buscar(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada"));
    }

    private SolicitacaoResponse toResponse(Solicitacao s) {
        Usuario decididoPor = s.getDecididoPor();
        return new SolicitacaoResponse(
                s.getId(),
                s.getCaixa().getId(),
                s.getCaixa().getNumero(),
                s.getOperador().getNome(),
                s.getTipo(),
                s.getProduto(),
                s.getQuantidade(),
                s.getValor(),
                s.getMotivo(),
                s.getStatus(),
                s.getCriadoEm(),
                s.getDecididoEm(),
                decididoPor != null ? decididoPor.getNome() : null);
    }
}
