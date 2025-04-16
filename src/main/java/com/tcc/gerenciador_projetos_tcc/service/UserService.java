package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Método para salvar um usuário
    public Users salvarUsuario(Integer ra, String nome, String sobrenome, String email, String senha, String curso, String faculdade) {

        Optional<Users> existingUser = usersRepository.findByRaAndFaculdade(ra, faculdade);

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Usuario já cadastrado.");
        }

        // Gerar o hash da senha
        String senhaHash = passwordEncoder.encode(senha);

        // Criar um novo usuário
        Users novoUsuario = new Users(ra, nome, sobrenome, email, senhaHash, null, curso, faculdade);

        // Salvar o usuário no banco de dados
        return usersRepository.save(novoUsuario);
    }

    // Método para autenticar um usuário
    public boolean autenticarUsuario(Integer ra, String senha, String faculdade) {
        Optional<Users> usuario = usersRepository.findByRaAndFaculdade(ra, faculdade);

        if (usuario.isPresent()) {
            Users usuarioExistente = usuario.get();
            // Verificar se a senha fornecida corresponde ao hash armazenado
            return passwordEncoder.matches(senha, usuarioExistente.getSenhaHash());
        }
        return false; // RA não encontrado ou senha incorreta
    }

    public Optional <Users> getUserByRaAndFaculdade(Integer ra, String faculdade) {
        return usersRepository.findByRaAndFaculdade(ra, faculdade);
    }

    public List<Users> buscarPorNomeFaculdade(String nome, String faculdade) {

        // Chama o repositório para buscar os usuários com nome e faculdade
        return usersRepository.findByNomeContainingIgnoreCaseAndFaculdade(nome, faculdade);
    }
}

