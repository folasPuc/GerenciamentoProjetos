package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.Grupo;
import com.tcc.gerenciador_projetos_tcc.repository.GrupoRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;

    public GrupoService(GrupoRepository grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    public List<Grupo> listarTodos() {
        return grupoRepository.findAll();
    }

    public List<Grupo> buscarPorUsuario(Integer userId) {
        return grupoRepository.findByUsuarios_Id(userId);
    }

    public Grupo salvar(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    public void deletar(Long id) {
        grupoRepository.deleteById(id);
    }

    public Optional<Grupo> findById(Long id) {
       return grupoRepository.findById(id);
    }

    // Método principal - buscar por ID
    public Grupo buscarPorId(Long id) {
        Optional<Grupo> grupo = grupoRepository.findById(id);
        return grupo.orElse(null);
    }

    public boolean existsById(Long groupId) {
        return grupoRepository.existsById(groupId);
    }

    public boolean alunoEstaNoGrupo(Long groupId, Integer userId) {
        return grupoRepository.existsByIdAndUsuarios_Id(groupId, userId);
    }

    public List<Grupo> buscarPorNome(String nome) {
        return grupoRepository.findByNomeContainingIgnoreCase(nome);
    }
}
