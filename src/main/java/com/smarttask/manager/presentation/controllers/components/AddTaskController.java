package com.smarttask.manager.presentation.controllers.components;

import com.smarttask.manager.application.dto.AiInsightResult;
import com.smarttask.manager.application.usecase.voice.CaptureVoiceCommandUseCase;
import com.smarttask.manager.domain.model.TaskStatus;
import com.smarttask.manager.infrastructure.external.ai.NLPParserImpl;
import com.smarttask.manager.infrastructure.external.calendar.CalendarSyncService;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.presentation.controllers.components.modalDialog.DateTimePopupController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.PriorityPopupController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.ReccurringTaskPopupController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.StatusPopupController;
import com.smarttask.manager.application.dto.TaskDTO;
import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.domain.model.PriorityLevel;
import com.smarttask.manager.domain.model.RecurrenceType;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import javafx.scene.control.Alert;
import com.smarttask.manager.domain.model.Task;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddTaskController implements Initializable {

    @FXML private TextField titleField;
    @FXML private TextField descField;
    @FXML private Button btnDate;
    @FXML private Button btnPriority;
    @FXML private Button btnStatus;
    @FXML private Button btnRecurring;
    @FXML private Button btnMic;

    private Runnable refreshCallback;

    public void setOnTaskAdded(Runnable callback) {
        this.refreshCallback = callback;
    }

    private LocalDateTime taskDeadline;
    private String selectedPriority = "Low";
    private String selectedStatus = "Todo";
    private String selectedRecurring = null;
    private String selectedCategory = "General";

    private NLPParserImpl aiParser;
    private final CaptureVoiceCommandUseCase voiceUseCase = new CaptureVoiceCommandUseCase();
    private TaskUseCase taskUseCase;

    private final CalendarSyncService calendarService = new CalendarSyncService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.aiParser = new NLPParserImpl();
        } catch (Exception e) {
            System.err.println("⚠️ AI Feature Unavailable: " + e.getMessage());
            this.aiParser = null;
        }

        try {
            var connection = DatabaseConnection.getConnection();
            var repository = new PostgresTaskRepository(connection);
            this.taskUseCase = new TaskUseCase(repository);
        } catch (Exception e) {
            System.err.println("⚠️ Database Connection Failed: " + e.getMessage());
        }
    }


    @FXML
    private void onOpenDatePick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/DateTimePopup.fxml"));
            VBox popupContent = loader.load();
            DateTimePopupController controller = loader.getController();

            Popup popup = createPopup(popupContent);
            controller.setParentPopup(popup);

            controller.setOnSave(dateTime -> {
                this.taskDeadline = dateTime;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm");
                btnDate.setText(dateTime.format(formatter));
                applyButtonStyle(btnDate, "#0D89FF");
            });

            showPopupUnderNode(popup, (Node) event.getSource());
        } catch (IOException e) {
            System.err.println("Error Opening Date Picker: ");

        }
    }

    @FXML
    private void onOpenPriority(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/PriorityPopup.fxml"));
            VBox popupContent = loader.load();
            PriorityPopupController controller = loader.getController();

            Popup popup = createPopup(popupContent);
            controller.setParentPopup(popup);

            controller.setOnSelect(this::updatePriorityUI);

            showPopupUnderNode(popup, (Node) event.getSource());
        } catch (IOException e) {
            System.err.println("Error Opening Priority: ");

        }
    }

    @FXML
    private void onOpenStatus(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/StatusPopup.fxml"));
            VBox popupContent = loader.load();
            StatusPopupController controller = loader.getController();

            Popup popup = createPopup(popupContent);
            controller.setParentPopup(popup);

            controller.setOnSelect(this::updateStatusUI);

            showPopupUnderNode(popup, (Node) event.getSource());
        } catch (IOException e) {
            System.err.println("Error Opening Status: ");

        }
    }

    @FXML
    private void onOpenRecurring(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/RecurringTaskPopup.fxml"));
            VBox popupContent = loader.load();
            ReccurringTaskPopupController controller = loader.getController();

            Popup popup = createPopup(popupContent);
            controller.setParentPopup(popup);

            controller.setOnSelect(this::updateRecurringUI);

            showPopupUnderNode(popup, (Node) event.getSource());
        } catch (IOException e) {
            System.err.println("Error Opening Recurring: ");

        }
    }


    @FXML
    public void onSmartAdd() {
        if (this.aiParser == null) {
            System.out.println("❌ AI is not configured (Missing API Key).");
            return;
        }

        String userText = titleField.getText();
        if (userText == null || userText.trim().isEmpty()) {
            return;
        }

        System.out.println("✨ AI Analyzing: " + userText);
        AiInsightResult result = aiParser.parseNaturalLanguage(userText);

        if (result != null) {
            if (result.title != null && !result.title.isEmpty()) {
                titleField.setText(result.title);
            }
            if (result.date != null) {
                this.taskDeadline = result.date;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM, h:mm a");
                btnDate.setText(this.taskDeadline.format(formatter));
                applyButtonStyle(btnDate, "#0D89FF");
            }
            if (result.priority != null) {
                updatePriorityUI(capitalize(result.priority));
            }

            if (result.category != null && !result.category.isEmpty()) {
                this.selectedCategory = capitalize(result.category);
                System.out.println("🏷️ AI detected category: " + this.selectedCategory);
            } else {
                this.selectedCategory = "General";
            }

        } else {
            System.out.println("AI could not extract details.");
        }
    }

    @FXML
    public void onMicClicked() {
        setMicIconColor("#db4c3f");
        btnMic.setDisable(true);

        String originalPlaceholder = titleField.getPromptText();
        titleField.setPromptText("🎤 Listening...");
        titleField.setText("");

        new Thread(() -> {
            String spokenText = voiceUseCase.execute();

            Platform.runLater(() -> {
                setMicIconColor("#0D89FF");
                btnMic.setDisable(false);
                titleField.setPromptText(originalPlaceholder);

                if (spokenText != null && !spokenText.trim().isEmpty()) {
                    titleField.setText(spokenText);
                } else {
                    titleField.setPromptText("❌ I didn't hear anything.");
                }
            });
        }).start();
    }

    @FXML
    public void onSaveTask(ActionEvent event) {
        if (taskUseCase == null) {
            showAlert("Erreur Base de données", "Non connecté à la base de données.");
            return;
        }

        String currentUserId = null;
        if (UserSession.getInstance().getUser() != null) {
            currentUserId = UserSession.getInstance().getUser().id();
        } else {
            throw new RuntimeException("Utilisateur non connecté !");
        }

        String title = titleField.getText();
        String rawDesc = descField.getText();
        String finalDescription = (rawDesc == null || rawDesc.trim().isEmpty()) ? null : rawDesc;

        if (title == null || title.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un titre pour la tâche.");
            return;
        }

        try {
            PriorityLevel priority = mapPriority(selectedPriority);
            TaskStatus status = mapStatus(selectedStatus);
            boolean isRecurring = selectedRecurring != null;
            RecurrenceType recurrence = mapRecurrence(selectedRecurring);

            TaskDTO newTaskDto = new TaskDTO(
                    title,
                    finalDescription,
                    selectedCategory,
                    priority,
                    status,
                    taskDeadline,
                    null,
                    isRecurring,
                    recurrence,
                    currentUserId,
                    null
            );

            String newTaskId = taskUseCase.create(newTaskDto);
            System.out.println("✅ Task successfully saved! DB ID: " + newTaskId);

            if (taskDeadline != null) {
                System.out.println("🔄 Syncing to Google Calendar...");

                Task taskForSync = new Task(
                        newTaskId,
                        title,
                        currentUserId,
                        LocalDateTime.now()
                );

                taskForSync.updateDetails(
                        title,
                        finalDescription,
                        selectedCategory,
                        priority,
                        status,
                        taskDeadline,
                        null,
                        isRecurring,
                        recurrence,
                        null
                );

                new Thread(() -> {
                    try {
                        calendarService.syncTask(taskForSync);
                        System.out.println("✅ Google Calendar Sync Complete!");
                    } catch (Exception e) {
                        System.err.println("❌ Google Sync Failed: " + e.getMessage());
                    }
                }).start();
            }

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            resetForm();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de sauvegarder : " + e.getMessage());
        }
    }

    private void updatePriorityUI(String priorityName) {
        this.selectedPriority = priorityName;
        btnPriority.setText(priorityName);
        String colorHex = switch (priorityName) {
            case "Urgent" -> "#db4c3f";
            case "High"   -> "#eb8909";
            case "Medium" -> "#0D89FF";
            case "Low"    -> "#25b84c";
            default       -> "#939595";
        };
        applyButtonStyle(btnPriority, colorHex);
    }

    private void updateStatusUI(String statusName) {
        this.selectedStatus = statusName;
        btnStatus.setText(statusName);
        String colorHex = switch (statusName) {
            case "Archived" -> "#25b84c";
            case "Done"     -> "#0D89FF";
            case "Doing"    -> "#eb8909";
            case "Todo"     -> "#db4c3f";
            default         -> "#939595";
        };
        applyButtonStyle(btnStatus, colorHex);
    }

    private void updateRecurringUI(String recurringName) {
        this.selectedRecurring = recurringName;
        btnRecurring.setText(recurringName);
        String colorHex = switch (recurringName) {
            case "Daily"   -> "#25b84c";
            case "Weekly"  -> "#0D89FF";
            case "Monthly" -> "#eb8909";
            case "Yearly"  -> "#db4c3f";
            default        -> "#939595";
        };
        applyButtonStyle(btnRecurring, colorHex);
    }

    private void applyButtonStyle(Button button, String hexColor) {
        button.setStyle(
                "-fx-background-color: " + hexColor + ";" +
                        "-fx-border-color: " + hexColor + ";" +
                        "-fx-text-fill: white;"
        );
        if (button.getGraphic() instanceof SVGPath icon) {
            icon.setStroke(Color.WHITE);
            icon.setFill(Color.TRANSPARENT);
        }
    }

    private void setMicIconColor(String hexColor) {
        if (btnMic.getGraphic() instanceof SVGPath icon) {
            icon.setStroke(Color.web(hexColor));
        }
    }

    private Popup createPopup(VBox content) {
        Popup popup = new Popup();
        popup.getContent().add(content);
        popup.setAutoHide(true);
        return popup;
    }

    private void showPopupUnderNode(Popup popup, Node node) {
        double x = node.localToScreen(node.getBoundsInLocal()).getMinX();
        double y = node.localToScreen(node.getBoundsInLocal()).getMaxY();
        popup.show(node, x, y + 5);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private PriorityLevel mapPriority(String priorityName) {
        if (priorityName == null) return PriorityLevel.NOT_URGENT_NOT_IMPORTANT;

        return switch (priorityName.toUpperCase()) {
            case "URGENT" -> PriorityLevel.URGENT_IMPORTANT;
            case "HIGH" -> PriorityLevel.NOT_URGENT_IMPORTANT;
            case "MEDIUM" -> PriorityLevel.URGENT_NOT_IMPORTANT;
            case "LOW" -> PriorityLevel.NOT_URGENT_NOT_IMPORTANT;
            default -> PriorityLevel.NOT_URGENT_NOT_IMPORTANT;
        };
    }
    private TaskStatus mapStatus(String statusName) {
        if (statusName == null) return TaskStatus.TODO;

        return switch (statusName.toUpperCase()) {
            case "ARCHIVED" -> TaskStatus.ARCHIVED;
            case "DONE" -> TaskStatus.DONE;
            case "DOING" -> TaskStatus.DOING;
            case "TODO" -> TaskStatus.TODO;
            default -> TaskStatus.TODO;
        };
    }

    private RecurrenceType mapRecurrence(String recurringName) {
        if (recurringName == null) return null;
        return switch (recurringName.toUpperCase()) {
            case "DAILY", "QUOTIDIEN" -> RecurrenceType.DAILY;
            case "WEEKLY", "HEBDOMADAIRE" -> RecurrenceType.WEEKLY;
            case "MONTHLY", "MENSUEL" -> RecurrenceType.MONTHLY;
            case "YEARLY", "ANNUEL" -> RecurrenceType.YEARLY;
            default -> null;
        };
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void resetForm() {
        titleField.setText("");
        titleField.setPromptText("Nom de la tâche");

        descField.setText("");
        descField.setPromptText("Description");

        taskDeadline = null;
        selectedPriority = "Low";
        selectedStatus = "Todo";
        selectedRecurring = null;
        selectedCategory = "General";

        resetButtonStyle(btnDate, "Date");
        resetButtonStyle(btnPriority, "Priorité");
        resetButtonStyle(btnStatus, "Status");
        resetButtonStyle(btnRecurring, "Recurring");
    }

    private void resetButtonStyle(Button button, String defaultText) {
        button.setText(defaultText);
        button.setStyle("");
        if (button.getGraphic() instanceof SVGPath icon) {
            icon.setStroke(Color.web("#939595"));
            icon.setFill(Color.TRANSPARENT);
        }
    }
}