package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.UsersPuc;
import com.tcc.gerenciador_projetos_tcc.entity.UsersUnicamp;
import com.tcc.gerenciador_projetos_tcc.repository.UsersUnicampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersUnicampService {

    @Autowired
    private UsersUnicampRepository usersUnicampRepository;

    private final PasswordEncoder passwordEncoder;

    public UsersUnicampService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Método para salvar um usuário
    public UsersUnicamp salvarUsuario(Integer ra, String nome, String sobrenome, String email, String senha, String curso) {
        // Verificar se o RA ou o email já existem
        Optional<UsersUnicamp> usuarioExistentePorRa = usersUnicampRepository.findByRa(ra);
        Optional<UsersUnicamp> usuarioExistentePorEmail = usersUnicampRepository.findByEmail(email);

        if (usuarioExistentePorRa.isPresent()) {
            throw new IllegalArgumentException("RA já cadastrado.");
        }
        if (usuarioExistentePorEmail.isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        // Gerar o hash da senha
        String senhaHash = passwordEncoder.encode(senha);

        // Criar um novo usuário
        UsersUnicamp novoUsuario = new UsersUnicamp(ra, nome, sobrenome, email, senhaHash, null, curso);

        // Salvar o usuário no banco de dados
        return usersUnicampRepository.save(novoUsuario);
    }

    // Método para autenticar um usuário
    public boolean autenticarUsuario(Integer ra, String senha) {
        Optional<UsersUnicamp> usuario = usersUnicampRepository.findByRa(ra);

        if (usuario.isPresent()) {
            UsersUnicamp usuarioExistente = usuario.get();
            // Verificar se a senha fornecida corresponde ao hash armazenado
            return passwordEncoder.matches(senha, usuarioExistente.getSenhaHash());
        }
        return false; // RA não encontrado ou senha incorreta
    }

    public Optional <UsersUnicamp> getUserByRa(Integer ra) {
        return usersUnicampRepository.findByRa(ra);
    }
}
