package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.Alunos;
import com.tcc.gerenciador_projetos_tcc.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    public AlunoService() {

    }

    public List<Alunos> listarTodos() {
        return alunoRepository.findAll();
    }

    public Alunos salvarAluno(Integer ra, String faculdade) {
        Alunos aluno = new Alunos(ra, faculdade);
        return alunoRepository.save(aluno);
    }

    public Alunos save (Alunos aluno) {
        return alunoRepository.save(aluno);
    }

    public void deletarAluno(int id) {
        alunoRepository.deleteById(id);
    }

    public boolean existsRAAndFaculdade(int ra, String faculdade) {
        return alunoRepository.existsByRaAndFaculdade(ra, faculdade);
    }
}
