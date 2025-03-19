package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Home")
@Route("homeview")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Recupera o usuário da sessão

        Users user = VaadinSession.getCurrent().getAttribute(Users.class);

        if (user != null) {
            add(new Text("Olá, " + user.getNome() + " ! "));
            add(new Text("Faculdade: " + user.getFaculdade()));
            add(new Text("Curso: " + user.getCurso()));
        } else {
            add(new Text("Usuário não autenticado."));
        }
    }
}
