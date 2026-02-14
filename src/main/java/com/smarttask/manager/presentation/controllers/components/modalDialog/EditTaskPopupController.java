package com.smarttask.manager.presentation.controllers.components.modalDialog;

import com.smarttask.manager.application.dto.AiInsightResult;
import com.smarttask.manager.application.dto.TaskDTO;
import com.smarttask.manager.application.usecase.task.TaskUseCase;
import com.smarttask.manager.application.usecase.voice.CaptureVoiceCommandUseCase;
import com.smarttask.manager.domain.model.PriorityLevel;
import com.smarttask.manager.domain.model.RecurrenceType;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.domain.model.TaskStatus;
import com.smarttask.manager.infrastructure.external.ai.NLPParserImpl;
import com.smarttask.manager.infrastructure.external.calendar.CalendarSyncService;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresTaskRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditTaskPopupController implements Initializable {

    // =========================================================================
    // 1. FXML UI ELEMENTS
    // =========================================================================
    @FXML private TextField titleField;
    @FXML private TextField descField;
    @FXML private Button btnDate;
    @FXML private Button btnPriority;
    @FXML private Button btnStatus;
    @FXML private Button btnReccurring;
    @FXML private Button btnMic;
    @FXML private Button btnCancel;

    // =========================================================================
    // 2. STATE VARIABLES
    // =========================================================================
    private String taskIdToUpdate;
    private String currentUserId; // To keep ownership correct
    private LocalDateTime taskDeadline;
    private String selectedPriority = "Low";
    private String selectedStatus = "Todo";
    private String selectedReccurring = null;
    private String selectedCategory = "General";

    // =========================================================================
    // 3. BACKEND SERVICES
    // =========================================================================
    private NLPParserImpl aiParser;
    private final CaptureVoiceCommandUseCase voiceUseCase = new CaptureVoiceCommandUseCase();
    private TaskUseCase taskUseCase;
    private final CalendarSyncService calendarService = new CalendarSyncService();

    // =========================================================================
    // 4. INITIALIZATION
    // =========================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // AI Initialization
        try {
            this.aiParser = new NLPParserImpl();
        } catch (Exception e) {
            System.err.println("⚠️ AI Feature Unavailable: " + e.getMessage());
            this.aiParser = null;
        }

        // Database & Use Case Initialization
        try {
            var connection = DatabaseConnection.getConnection();
            var repository = new PostgresTaskRepository(connection);
            this.taskUseCase = new TaskUseCase(repository);
        } catch (Exception e) {
            System.err.println("⚠️ Database Connection Failed: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. PUBLIC METHOD TO SET TASK DATA
    // =========================================================================
    public void setTask(Task task) {
        this.taskIdToUpdate = task.getIdTask();
        this.currentUserId = task.getOwnerId();
        this.selectedCategory = task.getCategory();

        // 1. Pre-fill Fields
        titleField.setText(task.getTitle());
        if (task.getDescription() != null) {
            descField.setText(task.getDescription());
        }

        // 2. Pre-fill Date
        if (task.getDueDate() != null) {
            this.taskDeadline = task.getDueDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm");
            btnDate.setText(this.taskDeadline.format(formatter));
            applyButtonStyle(btnDate, "#0D89FF");
        }

        // 3. Pre-fill Priority
        if (task.getPriority() != null) {
            updatePriorityUI(capitalize(task.getPriority().name()));
        }

        // 4. Pre-fill Status
        if (task.getStatus() != null) {
            updateStatusUI(capitalize(task.getStatus().name()));
        }

        // 5. Pre-fill Recurring
        if (task.isRecurring() && task.getRecurrenceType() != null) {
            updateRecurringUI(capitalize(task.getRecurrenceType().name()));
        }
    }

    // =========================================================================
    // 6. POPUP HANDLERS (Identical to AddTaskController)
    // =========================================================================
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
        } catch (IOException e) { e.printStackTrace(); }
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
        } catch (IOException e) { e.printStackTrace(); }
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
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onOpenReccurring(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/RecurringTaskPopup.fxml"));
            VBox popupContent = loader.load();
            ReccurringTaskPopupController controller = loader.getController();
            Popup popup = createPopup(popupContent);
            controller.setParentPopup(popup);
            controller.setOnSelect(this::updateRecurringUI);
            showPopupUnderNode(popup, (Node) event.getSource());
        } catch (IOException e) { e.printStackTrace(); }
    }

    // =========================================================================
    // 7. AI & VOICE HANDLERS
    // =========================================================================
    @FXML
    public void onSmartAdd() {
        if (this.aiParser == null) return;
        String userText = titleField.getText();
        if (userText == null || userText.trim().isEmpty()) return;

        System.out.println("✨ AI Analyzing for Edit: " + userText);
        AiInsightResult result = aiParser.parseNaturalLanguage(userText);

        if (result != null) {
            if (result.title != null && !result.title.isEmpty()) titleField.setText(result.title);
            if (result.date != null) {
                this.taskDeadline = result.date;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM, h:mm a");
                btnDate.setText(this.taskDeadline.format(formatter));
                applyButtonStyle(btnDate, "#0D89FF");
            }
            if (result.priority != null) updatePriorityUI(capitalize(result.priority));
            if (result.category != null && !result.category.isEmpty()) this.selectedCategory = capitalize(result.category);
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
                if (spokenText != null && !spokenText.trim().isEmpty()) titleField.setText(spokenText);
                else titleField.setPromptText("❌ I didn't hear anything.");
            });
        }).start();
    }

    // =========================================================================
    // 8. UPDATE TASK HANDLER (Main Logic)
    // =========================================================================
    @FXML
    public void onSaveTask(ActionEvent event) {
        if (taskUseCase == null || taskIdToUpdate == null) {
            showAlert("Erreur", "Impossible de mettre à jour la tâche.");
            return;
        }

        String title = titleField.getText();
        String rawDesc = descField.getText();
        String finalDescription = (rawDesc == null || rawDesc.trim().isEmpty()) ? null : rawDesc;

        if (title == null || title.trim().isEmpty()) {
            showAlert("Erreur", "Le titre est obligatoire.");
            return;
        }

        try {
            // Map Enums
            PriorityLevel priority = mapPriority(selectedPriority);
            TaskStatus status = mapStatus(selectedStatus);
            boolean isRecurring = selectedReccurring != null;
            RecurrenceType recurrence = mapRecurrence(selectedReccurring);

            // Create UPDATE DTO
            TaskDTO updateDto = new TaskDTO(
                    title,
                    finalDescription,
                    selectedCategory,
                    priority,
                    status,
                    taskDeadline,
                    isRecurring,
                    recurrence,
                    currentUserId, // Keep original owner
                    null
            );

            // 1. Perform Update
            taskUseCase.update(taskIdToUpdate, updateDto);
            System.out.println("✅ Task Updated Successfully: " + taskIdToUpdate);

            // 2. Sync to Google Calendar
            if (taskDeadline != null) {
                Task taskForSync = new Task(taskIdToUpdate, title, currentUserId, LocalDateTime.now());
                taskForSync.updateDetails(
                        title, finalDescription, selectedCategory, priority, status,
                        taskDeadline, isRecurring, recurrence, null
                );
                taskForSync.setStatus(status);

                new Thread(() -> calendarService.syncTask(taskForSync)).start();
            }

            // 3. Close Popup
            closeDialog();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur Update", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        if (btnCancel != null && btnCancel.getScene() != null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }

    // =========================================================================
    // 9. HELPERS
    // =========================================================================
    private void updatePriorityUI(String name) {
        this.selectedPriority = name;
        btnPriority.setText(name);
        applyButtonStyle(btnPriority, getPriorityColor(name));
    }

    private void updateStatusUI(String name) {
        this.selectedStatus = name;
        btnStatus.setText(name);
        applyButtonStyle(btnStatus, "#0D89FF");
    }

    private void updateRecurringUI(String name) {
        this.selectedReccurring = name;
        btnReccurring.setText(name);
        applyButtonStyle(btnReccurring, "#0D89FF");
    }

    private void applyButtonStyle(Button button, String hexColor) {
        button.setStyle("-fx-background-color: " + hexColor + "; -fx-border-color: " + hexColor + "; -fx-text-fill: white;");
        if (button.getGraphic() instanceof SVGPath icon) { icon.setStroke(Color.WHITE); icon.setFill(Color.TRANSPARENT); }
    }

    private void setMicIconColor(String hexColor) {
        if (btnMic.getGraphic() instanceof SVGPath icon) icon.setStroke(Color.web(hexColor));
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

    private String getPriorityColor(String priorityName) {
        if (priorityName == null) return "#939595";
        return switch (priorityName.toUpperCase()) {
            case "URGENT" -> "#db4c3f";
            case "HIGH" -> "#eb8909";
            case "MEDIUM" -> "#0D89FF";
            case "LOW" -> "#25b84c";
            default -> "#939595";
        };
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        // Fix for "URGENT_IMPORTANT" -> "Urgent important" if needed, or simple cap
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase().replace("_", " ");
    }

    private PriorityLevel mapPriority(String priorityName) {
        if (priorityName == null) return PriorityLevel.NOT_URGENT_NOT_IMPORTANT;
        // Basic mapping logic matching AddTask
        return switch (priorityName.toUpperCase()) {
            case "URGENT" -> PriorityLevel.URGENT_IMPORTANT;
            case "HIGH", "ÉLEVÉE" -> PriorityLevel.NOT_URGENT_IMPORTANT;
            case "MEDIUM", "MOYENNE" -> PriorityLevel.URGENT_NOT_IMPORTANT;
            default -> PriorityLevel.NOT_URGENT_NOT_IMPORTANT;
        };
    }

    private TaskStatus mapStatus(String statusName) {
        if (statusName == null) return TaskStatus.TODO;
        try {
            return TaskStatus.valueOf(statusName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TaskStatus.TODO;
        }
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
        alert.setContentText(content);
        alert.showAndWait();
    }
}