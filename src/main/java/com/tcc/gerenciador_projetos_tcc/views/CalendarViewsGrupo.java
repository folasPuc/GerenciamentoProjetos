package com.tcc.gerenciador_projetos_tcc.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.InMemoryEntryProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route("calendar-group")
public class CalendarViewsGrupo extends VerticalLayout {

    private final InMemoryEntryProvider<Entry> entryProvider;
    private final FullCalendar calendar;

    public CalendarViewsGrupo() {
        setSizeFull();

        // Mock inicial: carregar eventos do "backend"
        List<Entry> entryList = loadInitialEntries();
        entryProvider = EntryProvider.inMemoryFrom(entryList);

        calendar = FullCalendarBuilder.create().build();
        calendar.setEntryProvider(entryProvider);
        calendar.setSizeFull();

        // Configurar a visualização inicial para mostrar horários
        calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK); // ou TIME_GRID_DAY

        // Habilitar seleção para permitir cliques em áreas vazias
        calendar.setTimeslotsSelectable(true);

        calendar.setLocale(Locale.forLanguageTag("pt-BR")); // ou "en", "es", "de", etc.

        // Adicionar botão para criar novo evento (alternativa)
        Button newEventButton = new Button("Novo Evento", e -> {
            Entry entry = new Entry();
            entry.setStart(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0));
            entry.setEnd(entry.getStart().plusHours(1));
            entry.setAllDay(false);
            entry.setColor("dodgerblue");
            openEntryEditor(entry, true);
        });

        // Botões para mudança de visualização
        Button dayViewButton = new Button("Dia", e -> {
            calendar.changeView(CalendarViewImpl.TIME_GRID_DAY);
        });

        Button weekViewButton = new Button("Semana", e -> {
            calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
        });

        Button monthViewButton = new Button("Mês", e -> {
            calendar.changeView(CalendarViewImpl.DAY_GRID_MONTH);
        });

        HorizontalLayout viewButtons = new HorizontalLayout(dayViewButton, weekViewButton, monthViewButton);
        viewButtons.setSpacing(true);

        HorizontalLayout toolbar = new HorizontalLayout(newEventButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.add(viewButtons);
        add(toolbar, calendar);
        expand(calendar);

        // Clique em horário vazio para criar novo evento
        calendar.addTimeslotsSelectedListener(event -> {
            Entry entry = new Entry();
            entry.setStart(event.getStart());
            entry.setEnd(event.getEnd());
            entry.setAllDay(event.isAllDay());
            entry.setColor("dodgerblue");
            openEntryEditor(entry, true);
        });

        // Clique duplo em área vazia (alternativa para criar eventos)
        calendar.addDayNumberClickedListener(event -> {
            Entry entry = new Entry();
            entry.setStart(event.getDate().atStartOfDay());
            entry.setEnd(event.getDate().atStartOfDay().plusHours(1));
            entry.setAllDay(false);
            entry.setColor("dodgerblue");
            openEntryEditor(entry, true);
        });

        // Clique em evento existente para editar
        calendar.addEntryClickedListener(event -> {
            Entry entry = event.getEntry();
            openEntryEditor(entry, false);
        });
    }

    private void openEntryEditor(Entry entry, boolean newInstance) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(newInstance ? "Novo Evento" : "Editar Evento");

        Binder<Entry> binder = new Binder<>(Entry.class);
        TextField titleField = new TextField("Título");
        binder.bind(titleField, Entry::getTitle, Entry::setTitle);

        FormLayout formLayout = new FormLayout(titleField);
        dialog.add(formLayout);

        HorizontalLayout buttons = new HorizontalLayout();

        Button saveButton = new Button(newInstance ? "Criar" : "Salvar", e -> {
            try {
                binder.writeBean(entry);
                if (binder.validate().isOk()) {
                    if (newInstance) {
                        entryProvider.addEntries(entry);
                        Notification.show("Evento criado!");
                    } else {
                        entryProvider.refreshItem(entry);
                        Notification.show("Evento atualizado!");
                    }
                    entryProvider.refreshAll();
                    dialog.close();
                }
            } catch (ValidationException ex) {
                Notification.show("Erro ao salvar evento");
            }
        });

        buttons.add(saveButton);

        if (!newInstance) {
            Button removeButton = new Button("Remover", e -> {
                entryProvider.removeEntry(entry);
                entryProvider.refreshAll();
                Notification.show("Evento removido!");
                dialog.close();
            });
            buttons.add(removeButton);
        }

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        buttons.add(cancelButton);

        dialog.getFooter().add(buttons);
        dialog.open();
    }

    private List<Entry> loadInitialEntries() {
        // Simula eventos carregados de um backend
        List<Entry> entries = new ArrayList<>();

        Entry e1 = new Entry();
        e1.setTitle("Evento existente");
        e1.setStart(LocalDateTime.now().withHour(10));
        e1.setEnd(e1.getStart().plusHours(1));
        e1.setColor("#33aa33");
        entries.add(e1);

        return entries;
    }
}