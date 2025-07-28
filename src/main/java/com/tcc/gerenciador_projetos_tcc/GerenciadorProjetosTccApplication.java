package com.tcc.gerenciador_projetos_tcc;

import com.tcc.gerenciador_projetos_tcc.entity.Alunos;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.repository.AlunoRepository;
import com.tcc.gerenciador_projetos_tcc.repository.UsersRepository;
import com.tcc.gerenciador_projetos_tcc.service.AlunoService;
import com.tcc.gerenciador_projetos_tcc.service.UserService;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
@Push
@Theme("my-theme")
public class GerenciadorProjetosTccApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(GerenciadorProjetosTccApplication.class, args);
    }

    @Bean
    public CommandLineRunner createDefaultAdmin(UserService userService, AlunoService alunoService) {
        return args -> {
            String defaultEmail = "admin@admin.com";
            int defaultRA = 909090;
            String defaultPassword = "admin123";

            Optional<Users> user = userService.findByEmail(defaultEmail);
            if (user.isEmpty()) {
                Users novoAdmin = new Users();
                novoAdmin.setRa(defaultRA);
                novoAdmin.setEmail(defaultEmail);
                novoAdmin.setSenhaHash(defaultPassword);
                novoAdmin.setNome("admin");
                novoAdmin.setSobrenome("admin");
                novoAdmin.setFaculdade("PUCCAMPINAS");
                novoAdmin.setRole("admin");

                Alunos aluno = new Alunos();
                aluno.setRa(defaultRA);
                aluno.setFaculdade("PUCCAMPINAS");
                alunoService.save(aluno);


                userService.salvarAdmin(novoAdmin.getRa(), novoAdmin.getNome(), novoAdmin.getSobrenome(), novoAdmin.getEmail(), novoAdmin.getSenhaHash(),
                        novoAdmin.getCurso(), novoAdmin.getFaculdade());
            } else {
                System.out.println("Usuário admin já existe.");
            }
        };
    }
}
