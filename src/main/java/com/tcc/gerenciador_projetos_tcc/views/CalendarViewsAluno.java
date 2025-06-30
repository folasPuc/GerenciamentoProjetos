package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.CalendarEvent;
import com.tcc.gerenciador_projetos_tcc.service.CalendarEventService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.InMemoryEntryProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route("calendar-aluno")
public class CalendarViewsAluno extends VerticalLayout implements HasUrlParameter<String> {

    private final InMemoryEntryProvider<Entry> entryProvider;
    private final FullCalendar calendar;
    private final CalendarEventService calendarEventService;
    private int userId;
    private String type;

    public CalendarViewsAluno(CalendarEventService calendarEventService) {
        setSizeFull();

        this.calendarEventService = calendarEventService;

        entryProvider = EntryProvider.inMemoryFrom(new ArrayList<>());
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

        // Só hora!
        TimePicker startTimePicker = new TimePicker("Hora de início");
        TimePicker endTimePicker = new TimePicker("Hora de fim");

        // Valor padrão = hora atual do Entry
        startTimePicker.setValue(entry.getStart().toLocalTime());
        endTimePicker.setValue(entry.getEnd().toLocalTime());

        // Pré-carrega título também!
        binder.readBean(entry);

        FormLayout formLayout = new FormLayout(titleField, startTimePicker, endTimePicker);
        dialog.add(formLayout);

        HorizontalLayout buttons = new HorizontalLayout();

        Button saveButton = new Button(newInstance ? "Criar" : "Salvar", e -> {
            try {
                binder.writeBean(entry);

                LocalTime start = startTimePicker.getValue();
                LocalTime end = endTimePicker.getValue();

                if (start.isAfter(end)) {
                    Notification.show("A hora de início não pode ser depois da hora de fim");
                    return;
                }

                if (binder.validate().isOk()) {

                    // Reconstrói start/end
                    LocalDate date = entry.getStart().toLocalDate();
                    entry.setStart(LocalDateTime.of(date, startTimePicker.getValue()));
                    entry.setEnd(LocalDateTime.of(date, endTimePicker.getValue()));

                    // 🔑 Mapeia o Entry para CalendarEvent
                    CalendarEvent calendarEvent = new CalendarEvent();

                    // 🔑 Pega o ID real do banco via groupId se existir
                    if (!newInstance && entry.getGroupId() != null) {
                        calendarEvent.setId(Long.valueOf(entry.getGroupId()));
                    }

                    calendarEvent.setTitle(entry.getTitle());
                    calendarEvent.setStart(entry.getStart());
                    calendarEvent.setEnd(entry.getEnd());
                    calendarEvent.setColor(entry.getColor());
                    calendarEvent.setOwnerId(userId);
                    calendarEvent.setType(this.type);

                    // 🔑 Salva no banco
                    CalendarEvent saved = calendarEventService.save(calendarEvent);

                    // 🔑 Grava o ID real no groupId do Entry
                    entry.setGroupId(saved.getId().toString());

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
                try {
                    // 🔑 Remove usando o ID real no groupId
                    if (entry.getGroupId() != null) {
                        calendarEventService.delete(Long.valueOf(entry.getGroupId()));
                    }
                    entryProvider.removeEntry(entry);
                    entryProvider.refreshAll();
                    Notification.show("Evento removido!");
                    dialog.close();
                } catch (Exception ex) {
                    Notification.show("Erro ao remover evento");
                }
            });
            buttons.add(removeButton);
        }

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        buttons.add(cancelButton);

        dialog.getFooter().add(buttons);
        dialog.open();
    }



    private List<Entry> loadInitialEntries(int id) {

        List<CalendarEvent> calendarEntries = calendarEventService.getEventsForUser(id);


        List<Entry> entries = new ArrayList<>();
        for (CalendarEvent event : calendarEntries) {
            Entry entry = new Entry();
            entry.setGroupId(event.getId().toString());
            entry.setTitle(event.getTitle());
            entry.setStart(event.getStart());
            entry.setEnd(event.getEnd());
            entry.setColor(event.getColor());
            entries.add(entry);
        }

        System.out.println("Entries: " + entries);


        return entries;
    }



    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            String[] parts = parameter.split("-");
            if (parts.length == 2) {
                try {
                    int id = Integer.parseInt(parts[0]);
                    String type = parts[1];

                    userId = id;
                    this.type = type;

                    List<Entry> entries = loadInitialEntries(id); //Mudar aqui dps

                    entryProvider.getEntries().clear();
                    entryProvider.addEntries(entries);
                    entryProvider.refreshAll();

                } catch (NumberFormatException e) {
                    add(new Span("ID inválido: " + parts[0]));
                }
            } else {
                add(new Span("Parâmetros inválidos."));
            }
        } else {
            add(new Span("Parâmetro não informado."));
        }
    }

}