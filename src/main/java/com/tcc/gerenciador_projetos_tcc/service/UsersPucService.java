package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.UsersPuc;
import com.tcc.gerenciador_projetos_tcc.repository.UsersPucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersPucService {

    @Autowired
    private UsersPucRepository usersPucRepository;

    private final PasswordEncoder passwordEncoder;

    public UsersPucService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Método para salvar um usuário
    public UsersPuc salvarUsuario(Integer ra, String nome, String sobrenome, String email, String senha, String curso) {
        // Verificar se o RA ou o email já existem
        Optional<UsersPuc> usuarioExistentePorRa = usersPucRepository.findByRa(ra);
        Optional<UsersPuc> usuarioExistentePorEmail = usersPucRepository.findByEmail(email);

        System.out.println(usuarioExistentePorRa.isPresent());

        System.out.println(usuarioExistentePorEmail.isPresent());

        if (usuarioExistentePorRa.isPresent()) {
            throw new IllegalArgumentException("RA já cadastrado.");
        }
        if (usuarioExistentePorEmail.isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        // Gerar o hash da senha
        String senhaHash = passwordEncoder.encode(senha);

        // Criar um novo usuário
        UsersPuc novoUsuario = new UsersPuc(ra, nome, sobrenome, email, senhaHash, null, curso);

        // Salvar o usuário no banco de dados
        return usersPucRepository.save(novoUsuario);
    }

    // Método para autenticar um usuário
    public boolean autenticarUsuario(Integer ra, String senha) {
        Optional<UsersPuc> usuario = usersPucRepository.findByRa(ra);

        if (usuario.isPresent()) {
            UsersPuc usuarioExistente = usuario.get();
            // Verificar se a senha fornecida corresponde ao hash armazenado
            return passwordEncoder.matches(senha, usuarioExistente.getSenhaHash());
        }
        return false; // RA não encontrado ou senha incorreta
    }

    public Optional <UsersPuc> getUserByRa(Integer ra) {
        return usersPucRepository.findByRa(ra);
    }
}

