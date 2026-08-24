package com.filalivre.service;

import com.filalivre.model.RegistroAcao;
import com.filalivre.model.Usuario;
import com.filalivre.repository.RegistroAcaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {

    private final RegistroAcaoRepository registroAcaoRepository;

    public AuditoriaService(RegistroAcaoRepository registroAcaoRepository) {
        this.registroAcaoRepository = registroAcaoRepository;
    }

    @Transactional
    public void registrar(Usuario usuario, Integer caixaNumero, String acao, String detalhes) {
        RegistroAcao registro = new RegistroAcao();
        registro.setUsuario(usuario);
        registro.setCaixaNumero(caixaNumero);
        registro.setAcao(acao);
        registro.setDetalhes(detalhes);
        registroAcaoRepository.save(registro);
    }
}
