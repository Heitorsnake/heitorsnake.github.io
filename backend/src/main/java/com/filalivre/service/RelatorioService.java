package com.filalivre.service;

import com.filalivre.dto.ResumoResponse;
import com.filalivre.model.StatusCaixa;
import com.filalivre.model.StatusSolicitacao;
import com.filalivre.model.Solicitacao;
import com.filalivre.repository.CaixaRepository;
import com.filalivre.repository.SolicitacaoRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final CaixaRepository caixaRepository;

    public RelatorioService(SolicitacaoRepository solicitacaoRepository, CaixaRepository caixaRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.caixaRepository = caixaRepository;
    }

    public ResumoResponse resumo() {
        List<Solicitacao> decididas = solicitacaoRepository
                .findByStatusIn(List.of(StatusSolicitacao.APROVADA, StatusSolicitacao.RECUSADA));

        double tempoMedioMinutos = decididas.stream()
                .filter(s -> s.getDecididoEm() != null)
                .mapToLong(s -> Duration.between(s.getCriadoEm(), s.getDecididoEm()).toMinutes())
                .average()
                .orElse(0.0);

        Map<String, Long> caixasPorStatus = new LinkedHashMap<>();
        for (StatusCaixa st : StatusCaixa.values()) {
            caixasPorStatus.put(st.name(), 0L);
        }
        caixasPorStatus.putAll(caixaRepository.findAll().stream()
                .collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting())));

        return new ResumoResponse(
                solicitacaoRepository.count(),
                decididas.stream().filter(s -> s.getStatus() == StatusSolicitacao.APROVADA).count(),
                decididas.stream().filter(s -> s.getStatus() == StatusSolicitacao.RECUSADA).count(),
                solicitacaoRepository.findByStatusOrderByCriadoEmDesc(StatusSolicitacao.PENDENTE).size(),
                Math.round(tempoMedioMinutos * 10.0) / 10.0,
                LocalDateTime.now(),
                caixasPorStatus);
    }
}
