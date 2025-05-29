package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.*;
import com.tcc.gerenciador_projetos_tcc.service.GrupoService;
import com.tcc.gerenciador_projetos_tcc.service.TaskFilesService;
import com.tcc.gerenciador_projetos_tcc.service.TaskService;
import com.tcc.gerenciador_projetos_tcc.views.UIManager.UIManager;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class KanbanView extends VerticalLayout {

    private VerticalLayout todoColumn;
    private VerticalLayout inProgressColumn;
    private VerticalLayout doneColumn;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

//    // Enum para representar status da tarefa
//    private enum TaskStatus {
//        TODO("A Fazer"),
//        IN_PROGRESS("Em Progresso"),
//        DONE("Concluído");
//
//        private final String displayName;
//
//        TaskStatus(String displayName) {
//            this.displayName = displayName;
//        }
//
//        public String getDisplayName() {
//            return displayName;
//        }
//    }
//
//    // Classe para registrar ações na tarefa
//    private static class TaskHistoryEntry {
//        private final LocalDateTime timestamp;
//        private final String action;
//        private final String user; // Usuário que realizou a ação
//        private String details; // Detalhes adicionais, como valores antigos/novos
//
//        public TaskHistoryEntry(String action, String user) {
//            this.timestamp = LocalDateTime.now();
//            this.action = action;
//            this.user = user;
//        }
//
//        public TaskHistoryEntry(String action, String user, String details) {
//            this(action, user);
//            this.details = details;
//        }
//
//        public LocalDateTime getTimestamp() {
//            return timestamp;
//        }
//
//        public String getAction() {
//            return action;
//        }
//
//        public String getUser() {
//            return user;
//        }
//
//        public String getDetails() {
//            return details;
//        }
//    }
//
//    // Task data structure com histórico
//    private static class Task {
//        private final String id;
//        private String title;
//        private String description;
//        private String assignee;
//        private TaskStatus status;
//        private final List<TaskHistoryEntry> history;
//        private LocalDateTime createdAt;
//        private LocalDateTime updatedAt;
//
//        public Task(String title, String description, String assignee, TaskStatus status, String creator) {
//            this.id = UUID.randomUUID().toString();
//            this.title = title;
//            this.description = description;
//            this.assignee = assignee;
//            this.status = status;
//            this.history = new ArrayList<>();
//            this.createdAt = LocalDateTime.now();
//            this.updatedAt = LocalDateTime.now();
//
//            // Registra a criação da tarefa no histórico
//            this.history.add(new TaskHistoryEntry("Criação da tarefa", creator));
//        }
//
//        public void updateStatus(TaskStatus newStatus, String user) {
//            TaskStatus oldStatus = this.status;
//            this.status = newStatus;
//            this.updatedAt = LocalDateTime.now();
//
//            // Registra a mudança de status no histórico
//            this.history.add(new TaskHistoryEntry(
//                    "Alteração de status",
//                    user,
//                    "De: " + oldStatus.getDisplayName() + " → Para: " + newStatus.getDisplayName()
//            ));
//        }
//
//        public void updateDetails(String newTitle, String newDescription, String newAssignee, String user) {
//            StringBuilder changes = new StringBuilder();
//
//            if (!this.title.equals(newTitle)) {
//                changes.append("Título: '").append(this.title).append("' → '").append(newTitle).append("'\n");
//                this.title = newTitle;
//            }
//
//            if (!this.description.equals(newDescription)) {
//                changes.append("Descrição alterada\n");
//                this.description = newDescription;
//            }
//
//            if (!this.assignee.equals(newAssignee)) {
//                changes.append("Responsável: '").append(this.assignee).append("' → '").append(newAssignee).append("'");
//                this.assignee = newAssignee;
//            }
//
//            this.updatedAt = LocalDateTime.now();
//
//            // Registra as alterações no histórico
//            if (changes.length() > 0) {
//                this.history.add(new TaskHistoryEntry("Edição de tarefa", user, changes.toString()));
//            }
//        }
//
//        public void addComment(String comment, String user) {
//            this.history.add(new TaskHistoryEntry("Comentário", user, comment));
//            this.updatedAt = LocalDateTime.now();
//        }
//
//        public List<TaskHistoryEntry> getHistory() {
//            return history;
//        }
//    }

    // Lists to hold tasks in each column
    private List<Task> todoTasks = new ArrayList<>();
    private List<Task> inProgressTasks = new ArrayList<>();
    private List<Task> doneTasks = new ArrayList<>();
    private Long groupId;
    private String groupName;
    private final TaskService taskService;
    private final GrupoService grupoService;
    private final TaskFilesService taskFilesService;

    public KanbanView(Long groupId, String groupName, TaskService taskService, GrupoService grupoService, TaskFilesService taskFilesService) {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        this.groupId = groupId;
        this.groupName = groupName;
        this.taskService = taskService;
        this.grupoService = grupoService;
        this.taskFilesService = taskFilesService;

        H3 title = new H3("Kanban Board");
        title.getStyle().set("margin-top", "0");

        Button addTaskButton = new Button("Nova Tarefa", new Icon(VaadinIcon.PLUS));
        addTaskButton.addClickListener(e -> openTaskDialog(null, todoColumn));

        HorizontalLayout header = new HorizontalLayout(title, addTaskButton);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        add(header);
        add(createKanbanBoard());

        // Add some dummy tasks
        //addDummyTasks();

        //fetch tasks from db
        Integer integerGroupid = groupId.intValue();

        List<Task> fetchTasks = taskService.getTasksByGroup(integerGroupid);

        for (Task task : fetchTasks) {
            if (task.getStatus().equals(Task.TaskStatus.TODO)) {
                todoTasks.add(task);
                // Add task cards to columns
                todoColumn.add(new TaskCard(task));

            }

            if (task.getStatus().equals(Task.TaskStatus.IN_PROGRESS)) {
                inProgressTasks.add(task);
                // Add task cards to columns
                inProgressColumn.add(new TaskCard(task));
            }

            if (task.getStatus().equals(Task.TaskStatus.DONE)) {
                doneTasks.add(task);
                // Add task cards to columns
                doneColumn.add(new TaskCard(task));

            }


        }
    }

    private Component createKanbanBoard() {
        HorizontalLayout board = new HorizontalLayout();
        board.setSizeFull();
        board.setSpacing(true);

        // Create columns
        todoColumn = createColumn("A Fazer");
        inProgressColumn = createColumn("Em Progresso");
        doneColumn = createColumn("Concluído");

        // Configure drop targets
        configureDropTarget(todoColumn, Task.TaskStatus.TODO);
        configureDropTarget(inProgressColumn, Task.TaskStatus.IN_PROGRESS);
        configureDropTarget(doneColumn, Task.TaskStatus.DONE);

        board.add(todoColumn, inProgressColumn, doneColumn);
        board.setFlexGrow(1, todoColumn, inProgressColumn, doneColumn);

        return board;
    }

    private VerticalLayout createColumn(String title) {
        VerticalLayout column = new VerticalLayout();
        column.addClassNames(
                Background.CONTRAST_5,
                BorderRadius.MEDIUM,
                Padding.MEDIUM
        );
        column.setHeight("100%");
        column.setWidth("33%");
        column.getStyle().set("overflow-y", "auto"); // Adiciona scroll vertical
        column.getStyle().set("overflow-x", "hidden"); // ou "visible"

        H3 columnTitle = new H3(title);
        columnTitle.getStyle()
                .set("margin-top", "0")
                .set("color", "var(--lumo-primary-text-color)");

        Button addButton = new Button(new Icon(VaadinIcon.PLUS));
        addButton.getStyle().set("margin-left", "auto");

        // Determinar qual status baseado no título
        Task.TaskStatus status;
        if (title.equals("A Fazer")) {
            status = Task.TaskStatus.TODO;
            addButton.addClickListener(e -> openTaskDialog(null, todoColumn));
        } else if (title.equals("Em Progresso")) {
            status = Task.TaskStatus.IN_PROGRESS;
            addButton.addClickListener(e -> openTaskDialog(null, inProgressColumn));
        } else {
            status = Task.TaskStatus.DONE;
            addButton.addClickListener(e -> openTaskDialog(null, doneColumn));
        }

        HorizontalLayout header = new HorizontalLayout(columnTitle, addButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);

        column.add(header);
        column.getElement().setAttribute("data-status", status.name());

        return column;
    }

    private void configureDropTarget(VerticalLayout column, Task.TaskStatus newStatus) {
        DropTarget<VerticalLayout> dropTarget = DropTarget.create(column);

        dropTarget.addDropListener(event -> {
            if (event.getDragSourceComponent().isPresent() &&
                    event.getDragSourceComponent().get() instanceof TaskCard) {

                TaskCard taskCard = (TaskCard) event.getDragSourceComponent().get();
                Task task = taskCard.getTask();

                // Determine qual é o usuário atual da sessão (como exemplo, usarei um mock)
                String currentUser = getCurrentUserName();

                // Remove from original list
                if (todoTasks.contains(task)) {
                    todoTasks.remove(task);
                } else if (inProgressTasks.contains(task)) {
                    inProgressTasks.remove(task);
                } else if (doneTasks.contains(task)) {
                    doneTasks.remove(task);
                }

                // Add to new list
                if (column == todoColumn) {
                    todoTasks.add(task);
                } else if (column == inProgressColumn) {
                    inProgressTasks.add(task);
                } else if (column == doneColumn) {
                    doneTasks.add(task);
                }

                // Atualiza o status da tarefa e registra no histórico
                task.updateStatus(newStatus, currentUser);

                taskService.updateTaskStatus(task.getId(), newStatus, currentUser);

                // Remove from original parent and add to new column
                TaskCard newCard = new TaskCard(task);

                // Make the layout accept the component
                // If it's not the first child (after the header)
                if (column.getComponentCount() > 1) {
                    column.addComponentAtIndex(column.getComponentCount(), newCard);
                } else {
                    column.add(newCard);
                }

                // Remove the original component from its parent
                if (taskCard.getParent().isPresent()) {
                    ((VerticalLayout) taskCard.getParent().get()).remove(taskCard);
                }

                Notification.show("Tarefa movida com sucesso", 2000, Notification.Position.BOTTOM_CENTER);

                refreshKanbanBoard();
            }
        });
    }

    private String getCurrentUserName() {
        // Para implementação real, obtenha o usuário da sessão
        Users user = com.vaadin.flow.server.VaadinSession.getCurrent().getAttribute(Users.class);
        return user != null ? user.getNome() + " " + user.getSobrenome() : "Sistema";
    }

    private void openTaskDialog(Task task, VerticalLayout targetColumn) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(task == null ? "Nova Tarefa" : "Editar Tarefa");
        dialog.setWidth("800px");

        TextArea commentField = new TextArea("Adicionar comentario");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        // Mapa para armazenar os arquivos temporariamente (nome -> conteúdo)
        Map<String, byte[]> uploadedFiles = new LinkedHashMap<>();
        Map<String, ByteArrayOutputStream> fileBuffers = new HashMap<>();

        // Componente de upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropLabel(new Span("Arraste arquivos ou clique"));
        upload.setAcceptedFileTypes(".pdf", ".docx", ".png", ".jpg", ".csv"); // ajuste conforme necessário
        upload.setWidthFull();
        upload.setMaxFiles(5);


        VerticalLayout uploadSection = new VerticalLayout();
        uploadSection.setPadding(false);
        uploadSection.setSpacing(true);

        H2 uploadTitle = new H2("Adicione arquivos a esta tarefa");
        uploadTitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-bottom", "0");


//        upload.setReceiver((fileName, mimeType) -> {
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            fileBuffers.put(fileName, buffer);
//            return buffer;
//        });

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream file = buffer.getInputStream();

            try {
                uploadedFiles.put(fileName, file.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        upload.addFileRemovedListener(event -> {
            uploadedFiles.remove(event.getFileName());
        });

        dialog.addDialogCloseActionListener(event -> {
            uploadedFiles.clear();
        });

        TextField titleField = new TextField("Título");
        titleField.setWidthFull();
        if (task != null) {
            titleField.setValue(task.getTitle());
        }

        TextArea descriptionField = new TextArea("Descrição");
        descriptionField.setWidthFull();
        descriptionField.setHeight("100px");
        if (task != null) {
            descriptionField.setValue(task.getDescription());
        }

//        TextField assigneeField = new TextField("Responsável");
//        assigneeField.setWidthFull();

        Select<String> assigneeSelect = new Select<>();
        assigneeSelect.setLabel("Responsável");
        assigneeSelect.setWidthFull();

        Optional<Grupo> kanbanGroup = grupoService.findById(groupId);

        Set<Users> assignees = kanbanGroup.get().getUsuarios();

        List<String> assigneeNames = assignees.stream()
                .map(Users::getNome)  // pega apenas o nome
                .collect(Collectors.toList());

        assigneeSelect.setItems(assigneeNames);



        if (task != null) {
            assigneeSelect.setValue(task.getAssignee());
        }

        uploadSection.add(uploadTitle, upload);

        content.add(titleField, descriptionField, assigneeSelect, uploadSection);

        // Se estiver editando uma tarefa existente, exibe o histórico
        if (task != null) {
            //content.add(createHistoryComponent(task, "250px"));

            // Adicionar comentário
            commentField.setWidthFull();
            commentField.setHeight("80px");

//            Button addCommentButton = new Button("Adicionar comentário", e -> {
//                String comment = commentField.getValue();
//                if (!comment.isEmpty()) {
//                    task.addComment(comment, getCurrentUserName());
//                    taskService.addCommentToTask(task.getId(), comment, getCurrentUserName());
//                    commentField.clear();
//
//                    // Atualiza o componente de histórico
//                    //content.replace(content.getComponentAt(3), createHistoryComponent(task, "250px"));
//
//                    refreshKanbanBoard();
//                }
//            });

            content.add(commentField);
        }

        Button saveButton = new Button("Salvar", e -> {
            String title = titleField.getValue();
            String description = descriptionField.getValue();
            String assignee = assigneeSelect.getValue();

            String comment = commentField.getValue();
                if (!comment.isEmpty() && task != null) {
                    task.addComment(comment, getCurrentUserName());
                    taskService.addCommentToTask(task.getId(), comment, getCurrentUserName());

                }

            if (title.isEmpty()) {
                Notification.show("Por favor, insira um título para a tarefa.");
                return;
            }

            String currentUser = getCurrentUserName();

            if (task == null) {
                // Determine o status com base na coluna alvo
                Task.TaskStatus status;
                if (targetColumn == todoColumn) {
                    status = Task.TaskStatus.TODO;
                } else if (targetColumn == inProgressColumn) {
                    status = Task.TaskStatus.IN_PROGRESS;
                } else {
                    status = Task.TaskStatus.DONE;
                }

                // Create new task
                Task newTask = taskService.createTask(title, description, assignee, status, groupId, currentUser);

                //Here we add the files to TaskFiles if they exist
                //TODO here


                if (!uploadedFiles.isEmpty()) {

                    for (Map.Entry<String, byte[]> entry : uploadedFiles.entrySet()) {
                        String filename = entry.getKey();
                        byte[] filedata = entry.getValue();

                        // Aqui você pode salvar no banco de dados
                        // Exemplo fictício: fileService.saveToDatabase(task.getId(), nomeArquivo, conteudo);
                        taskFilesService.saveToDatabase((long) newTask.getId(), filename, filedata);

                        System.out.println("Salvando arquivo: " + filename + " (" + filedata.length + " bytes)");
                    }

                    // Limpa o mapa após envio
                    uploadedFiles.clear();
                    Notification.show("Arquivos enviados com sucesso!", 3000, Notification.Position.TOP_CENTER);
                }


                TaskCard card = new TaskCard(newTask);

                // Add to appropriate list
                if (targetColumn == todoColumn) {
                    todoTasks.add(newTask);
                } else if (targetColumn == inProgressColumn) {
                    inProgressTasks.add(newTask);
                } else if (targetColumn == doneColumn) {
                    doneTasks.add(newTask);
                }

                targetColumn.add(card);

                refreshKanbanBoard();
            } else {
                // Update existing task
                task.updateDetails(title, description, assignee, currentUser);
                taskService.updateTaskDetails(task.getId(), title, description, assignee, currentUser);

                if (!uploadedFiles.isEmpty()) {

                    for (Map.Entry<String, byte[]> entry : uploadedFiles.entrySet()) {
                        String filename = entry.getKey();
                        byte[] filedata = entry.getValue();

                        // Aqui você pode salvar no banco de dados
                        // Exemplo fictício: fileService.saveToDatabase(task.getId(), nomeArquivo, conteudo);
                        taskFilesService.saveToDatabase((long) task.getId(), filename, filedata);

                        System.out.println("Salvando arquivo: " + filename + " (" + filedata.length + " bytes)");

                        task.addFile(filename, getCurrentUserName());
                    }

                    taskService.saveTask(task);


                    // Limpa o mapa após envio
                    uploadedFiles.clear();
                    Notification.show("Arquivos enviados com sucesso!", 3000, Notification.Position.TOP_CENTER);
                }


                // Refresh UI
                refreshKanbanBoard();
            }

            dialog.close();
        });

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);

        content.add(buttons);
        dialog.add(content);

        dialog.open();
    }

    private Component createHistoryComponent(Task task, String height) {
        H4 historyTitle = new H4("Histórico");
        historyTitle.getStyle().set("margin-bottom", "0.5em");

        VerticalLayout historyLayout = new VerticalLayout();
        historyLayout.setPadding(false);
        historyLayout.setSpacing(false);
        historyLayout.add(historyTitle);

        // Grid para exibir o histórico
        Grid<TaskHistoryEntry> historyGrid = new Grid<>();
        historyGrid.setItems(task.getHistory());

        historyGrid.addColumn(entry -> dateFormatter.format(entry.getTimestamp()))
                .setHeader("Data/Hora").setAutoWidth(true);
        historyGrid.addColumn(TaskHistoryEntry::getUser)
                .setHeader("Usuário").setAutoWidth(true);

        // Coluna de ação com ComponentRenderer e span
        historyGrid.addColumn(new ComponentRenderer<>(entry -> {
            Span actionSpan = new Span(entry.getAction());
            actionSpan.getStyle()
                    .set("white-space", "normal")
                    .set("word-break", "break-word")
                    .set("width", "100%")
                    .set("display", "inline-block");
            return actionSpan;
        })).setHeader("Ação").setAutoWidth(true);

        // Coluna de detalhes com ComponentRenderer e span
        historyGrid.addColumn(new ComponentRenderer<>(entry -> {
            String details = entry.getDetails() != null ? entry.getDetails() : "";
            Span detailsSpan = new Span(details);
            detailsSpan.getStyle()
                    .set("white-space", "normal")
                    .set("word-break", "break-word")
                    .set("width", "100%")
                    .set("display", "inline-block");
            return detailsSpan;
        })).setHeader("Detalhes").setFlexGrow(1);

        historyGrid.setHeight(height);
        historyLayout.add(historyGrid);

        return historyLayout;
    }

    private void showTaskHistory(Task task) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Histórico da Tarefa: " + task.getTitle());
        dialog.setWidth("1000px");

        VerticalLayout content = new VerticalLayout();
        content.add(createHistoryComponent(task, "400px"));

        Button closeButton = new Button("Fechar", e -> dialog.close());
        content.add(closeButton);
        content.setHorizontalComponentAlignment(Alignment.END, closeButton);

        dialog.add(content);
        dialog.open();
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    private void refreshKanbanBoard() {

        // Obtém todas as sessões da UI conectadas
        Set<UI> uis = UIManager.getInstance().getAllUIs();

        for (UI ui : uis) {

            ui.access(() -> {


                HomeView homeView = ui.getSession().getAttribute(HomeView.class);

                Grupo selectedKanban = (Grupo) ui.getSession().getAttribute("kanbanSelected");

                if (selectedKanban != null) {

                    if (Objects.equals(selectedKanban.getId(), this.getGroupId())) {

                        clearColumnContent(todoColumn);
                        clearColumnContent(inProgressColumn);
                        clearColumnContent(doneColumn);

                        // Re-add tasks
                        todoTasks.forEach(task -> todoColumn.add(new TaskCard(task)));
                        inProgressTasks.forEach(task -> inProgressColumn.add(new TaskCard(task)));
                        doneTasks.forEach(task -> doneColumn.add(new TaskCard(task)));

                        homeView.grupoGrid.deselect(selectedKanban);
                        homeView.grupoGrid.select(selectedKanban);

                        //Notification.show("Estamos atualizando aqui");

                    }

                }
            });

        }

    }

    private void clearColumnContent(VerticalLayout column) {
        // Keep only the first component (header)
        while (column.getComponentCount() > 1) {
            column.remove(column.getComponentAt(column.getComponentCount() - 1));
        }
    }

    // TaskCard component to represent a task in the Kanban board
    private class TaskCard extends Div implements DragSource<TaskCard> {
        private final Task task;

        public TaskCard(Task task) {
            this.task = task;

            addClassNames(
                    Background.CONTRAST_5,
                    Border.ALL,
                    BorderRadius.MEDIUM,
                    Padding.SMALL,
                    Margin.Vertical.SMALL
            );

            getStyle()
                    .set("background-color", "white")
                    .set("cursor", "move")
                    .set("transition", "box-shadow 0.3s ease-in-out")
                    .set("user-select", "none");

            setWidthFull();

            // Task title
            H3 title = new H3(task.getTitle());
            title.getStyle()
                    .set("margin-top", "0")
                    .set("margin-bottom", "0.5em")
                    .set("font-size", "1.2em");

            // Task description
            Div description = new Div();
            description.setText(task.getDescription());
            description.getStyle()
                    .set("margin-bottom", "0.5em")
                    .set("color", "var(--lumo-secondary-text-color)");

            // Task assignee
            Span assignee = new Span(task.getAssignee());
            assignee.getStyle()
                    .set("display", "block")
                    .set("font-size", "0.9em")
                    .set("color", "var(--lumo-primary-color)")
                    .set("margin-top", "auto");

            // Metadata layout (última alteração, criado em, etc)
            Div metadata = new Div();
            metadata.getStyle()
                    .set("font-size", "0.8em")
                    .set("margin-top", "0.5em")
                    .set("color", "var(--lumo-tertiary-text-color)");

            Span updatedAt = new Span("Última atualização: " + dateFormatter.format(task.getUpdatedAt()));
            metadata.add(updatedAt);

            // History badge - mostra quantos registros de histórico existem

            // Ícone de histórico
            Icon historyIcon = new Icon(VaadinIcon.CLOCK);
            historyIcon.getStyle().set("position", "relative");
            historyIcon.getStyle().set("font-size", "11px");

            // Badge com a quantidade de entradas no histórico
            Span historyBadge = new Span(String.valueOf(task.getHistory().size()));
            historyBadge.getStyle()
                    .set("background", "red")
                    .set("color", "white")
                    .set("font-size", "10px")
                    .set("border-radius", "50%")
                    .set("padding", "0 6px")
                    .set("position", "absolute")
                    .set("top", "-8px")
                    .set("right", "-8px");


            // Container para o botão com ícone e badge
            Div iconWithBadgeH = new Div(historyIcon, historyBadge);
            iconWithBadgeH.getStyle().set("position", "relative");

            // Botão de histórico com o layout interno
            Button historyButton = new Button(iconWithBadgeH);

            historyButton.addClickListener(e -> showTaskHistory(task));

            // Edit button
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> {
                if (todoTasks.contains(task)) {
                    openTaskDialog(task, todoColumn);
                } else if (inProgressTasks.contains(task)) {
                    openTaskDialog(task, inProgressColumn);
                } else {
                    openTaskDialog(task, doneColumn);
                }
            });

            // Files button

            // Criando o ícone de arquivos
            Icon fileIcon = new Icon(VaadinIcon.FILE);
            fileIcon.getStyle().set("position", "relative");
            fileIcon.getStyle().set("font-size", "11px");

            // Badge com o contador
            Span badge = new Span(String.valueOf(taskFilesService.getFilesByTaskId( (long) task.getId()).size()));
            badge.getStyle()
                    .set("background", "red")
                    .set("color", "white")
                    .set("font-size", "10px")
                    .set("border-radius", "50%")
                    .set("padding", "0 6px")
                    .set("position", "absolute")
                    .set("top", "-8px")
                    .set("right", "-8px");

            // Container para o botão com ícone e badge
            Div iconWithBadge = new Div(fileIcon, badge);
            iconWithBadge.getStyle().set("position", "relative");



            Button filesButton = new Button(iconWithBadge);
            filesButton.addClickListener(e -> {

                Dialog filesDialog = new Dialog();
                filesDialog.setHeaderTitle("Arquivos da Tarefa");
                filesDialog.setWidth("600px");

                VerticalLayout fileListLayout = new VerticalLayout();
                fileListLayout.setSpacing(false);
                fileListLayout.setPadding(false);

                Span subTitle = new Span("Arquivos disponíveis para download:");
                subTitle.getStyle().set("color", "gray").set("font-size", "14px").set("margin-bottom", "10px");

                fileListLayout.add(subTitle);

                List<TaskFiles> files = taskFilesService.getFilesByTaskId((long) task.getId());

                if (files.isEmpty()) {
                    fileListLayout.add(new Span("Nenhum arquivo disponível."));
                } else {
                    for (TaskFiles file : files) {
                        StreamResource resource = new StreamResource(
                                file.getFilename(),
                                () -> new ByteArrayInputStream(file.getFile())
                        );

                        Icon fileIconA = VaadinIcon.FILE.create();
                        fileIconA.setSize("16px");

                        Anchor downloadLink = new Anchor(resource, file.getFilename());
                        downloadLink.getElement().setAttribute("download", true);
                        downloadLink.getStyle()
                                .set("text-decoration", "underline")
                                .set("color", "#1a73e8")
                                .set("margin-left", "5px");

                        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
                        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
                        deleteButton.getElement().setProperty("title", "Remover arquivo");
                        deleteButton.getStyle().set("margin-left", "auto");


                        HorizontalLayout fileItem = new HorizontalLayout(fileIconA, downloadLink, deleteButton);
                        fileItem.setAlignItems(FlexComponent.Alignment.CENTER);
                        fileItem.setSpacing(true);
                        fileItem.setPadding(false);


                        deleteButton.addClickListener(click -> {
                            taskFilesService.deleteById(file.getId());
                            fileListLayout.remove(fileItem);
                            task.removeFile(file.getFilename(), getCurrentUserName());
                            taskService.saveTask(task);
                            refreshKanbanBoard();
                            Notification.show("Arquivo removido", 2000, Notification.Position.TOP_CENTER);
                        });

                        fileListLayout.add(fileItem);
                    }
                }

                filesDialog.add(fileListLayout);

                Button close = new Button("Fechar", event -> filesDialog.close());
                HorizontalLayout footer = new HorizontalLayout(close);
                footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
                footer.setWidthFull();

                filesDialog.getFooter().add(footer);
                filesDialog.open();
            });


            // Delete button
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(e -> {
                if (todoTasks.contains(task)) {
                    todoTasks.remove(task);
                    todoColumn.remove(this);
                } else if (inProgressTasks.contains(task)) {
                    inProgressTasks.remove(task);
                    inProgressColumn.remove(this);
                } else if (doneTasks.contains(task)) {
                    doneTasks.remove(task);
                    doneColumn.remove(this);
                }
                Notification.show("Tarefa removida", 2000, Notification.Position.BOTTOM_CENTER);

                taskService.deleteTask(task.getId());
                taskFilesService.deleteFilesByTaskId((long) task.getId());
                refreshKanbanBoard();


            });

            // Action buttons layout
            HorizontalLayout actions = new HorizontalLayout();
            actions.add(editButton, deleteButton, filesButton, historyButton);
            actions.setWidthFull();
            actions.setJustifyContentMode(JustifyContentMode.END);
            actions.setSpacing(true);

            add(title, description, assignee, metadata, actions);

            // Configure drag source
            DragSource<TaskCard> dragSource = DragSource.create(this);
            dragSource.setDraggable(true);

            addClassName("task-card");

            // Visual feedback during drag
            getElement().addEventListener("mousedown", e ->
                    getStyle().set("box-shadow", "var(--lumo-box-shadow-m)"));

            getElement().addEventListener("mouseup", e ->
                    getStyle().set("box-shadow", "var(--lumo-box-shadow-s)"));
        }

        public Task getTask() {
            return task;
        }
    }
}