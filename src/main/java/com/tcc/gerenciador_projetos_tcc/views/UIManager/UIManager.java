package com.tcc.gerenciador_projetos_tcc.views.UIManager;

import com.vaadin.flow.component.UI;
import java.util.HashSet;
import java.util.Set;

public class UIManager {

    private static UIManager instance;

    // Conjunto de UIs conectadas
    private Set<UI> uis = new HashSet<>();

    private UIManager() {}

    public static synchronized UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }

    // Adiciona uma UI à lista de UIs conectadas
    public void addUI(UI ui) {
        uis.add(ui);
    }

    // Remove uma UI da lista de UIs conectadas
    public void removeUI(UI ui) {
        uis.remove(ui);
    }

    // Obtém todas as UIs conectadas
    public Set<UI> getAllUIs() {
        return uis;
    }
}
