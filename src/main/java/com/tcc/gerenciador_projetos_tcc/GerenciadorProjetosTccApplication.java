package com.tcc.gerenciador_projetos_tcc;

import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@Push
@Theme("my-theme")
public class GerenciadorProjetosTccApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(GerenciadorProjetosTccApplication.class, args);
    }
}
