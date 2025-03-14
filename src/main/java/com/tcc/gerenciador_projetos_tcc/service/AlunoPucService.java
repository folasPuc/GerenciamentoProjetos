package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.AlunoPuc;
import com.tcc.gerenciador_projetos_tcc.repository.AlunoPucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoPucService {

    @Autowired
    private AlunoPucRepository alunoPucRepository;

    public AlunoPucService() {

    }

    public List<AlunoPuc> listarTodos() {
        return alunoPucRepository.findAll();
    }

    public Optional<AlunoPuc> buscarPorRa(Integer ra) {
        return alunoPucRepository.findByRa(ra);
    }

    public AlunoPuc salvarAluno(Integer ra) {
        AlunoPuc aluno = new AlunoPuc(ra);
        return alunoPucRepository.save(aluno);
    }

    public void deletarAluno(int id) {
        alunoPucRepository.deleteById(id);
    }

    public boolean existsRA(int ra) {
        return alunoPucRepository.existsByRa(ra);
    }
}
