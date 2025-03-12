package com.tcc.gerenciador_projetos_tcc.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("") // Define a rota principal ("/")
public class MainView extends VerticalLayout {

    public MainView() {
        // Título da página
        H1 title = new H1("Olá, Vaadin!");

        // Botão de teste
        Button button = new Button("Clique aqui", event ->
                Notification.show("Botão clicado!")
        );

        // Adiciona os componentes ao layout
        add(title, button);
    }
}
