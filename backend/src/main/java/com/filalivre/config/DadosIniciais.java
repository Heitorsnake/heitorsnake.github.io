package com.filalivre.config;

import com.filalivre.model.Caixa;
import com.filalivre.model.Perfil;
import com.filalivre.model.Usuario;
import com.filalivre.repository.CaixaRepository;
import com.filalivre.repository.UsuarioRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DadosIniciais {

    @Bean
    CommandLineRunner criarDadosIniciais(UsuarioRepository usuarioRepository,
                                         CaixaRepository caixaRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                usuarioRepository.saveAll(List.of(
                        usuario("Administrador", "admin@filalivre.com", "admin123", Perfil.ADMINISTRADOR, passwordEncoder),
                        usuario("Gerente Geral", "gerente@filalivre.com", "gerente123", Perfil.GERENTE, passwordEncoder),
                        usuario("Operador 1", "operador@filalivre.com", "operador123", Perfil.OPERADOR, passwordEncoder)));
            }
            if (caixaRepository.count() == 0) {
                for (int numero = 1; numero <= 6; numero++) {
                    Caixa caixa = new Caixa();
                    caixa.setNumero(numero);
                    caixaRepository.save(caixa);
                }
            }
        };
    }

    private Usuario usuario(String nome, String email, String senha, Perfil perfil, PasswordEncoder encoder) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(encoder.encode(senha));
        u.setPerfil(perfil);
        return u;
    }
}
