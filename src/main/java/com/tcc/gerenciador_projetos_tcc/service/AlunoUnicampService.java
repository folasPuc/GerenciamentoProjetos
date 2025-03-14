package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.AlunoPuc;
import com.tcc.gerenciador_projetos_tcc.entity.AlunoUnicamp;
import com.tcc.gerenciador_projetos_tcc.repository.AlunoPucRepository;
import com.tcc.gerenciador_projetos_tcc.repository.AlunoUnicampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoUnicampService {

    @Autowired
    private AlunoUnicampRepository alunoUnicampRepository;

    public AlunoUnicampService() {

    }

    public List<AlunoUnicamp> listarTodos() {
        return alunoUnicampRepository.findAll();
    }

    public Optional<AlunoUnicamp> buscarPorRa(Integer ra) {
        return alunoUnicampRepository.findByRa(ra);
    }

    public AlunoUnicamp salvarAluno(Integer ra) {
        AlunoUnicamp aluno = new AlunoUnicamp(ra);
        return alunoUnicampRepository.save(aluno);
    }

    public void deletarAluno(int id) {
        alunoUnicampRepository.deleteById(id);
    }

    public boolean existsRA(int ra) {
        return alunoUnicampRepository.existsByRa(ra);
    }
}
