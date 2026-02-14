package com.smarttask.manager.presentation.controllers.components;

import com.smarttask.manager.application.dto.AiInsightResult;
import com.smarttask.manager.application.usecase.voice.CaptureVoiceCommandUseCase;
import com.smarttask.manager.infrastructure.external.ai.NLPParserImpl;
import com.smarttask.manager.infrastructure.external.calendar.CalendarSyncService;
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
import com.smarttask.manager.infrastructure.session.UserSession;

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

    // =========================================================================
    // 1. FXML UI ELEMENTS
    // =========================================================================
    @FXML private TextField titleField;
    @FXML private TextField descField; // Linked to your FXML
    @FXML private Button btnDate;
    @FXML private Button btnPriority;
    @FXML private Button btnStatus;
    @FXML private Button btnReccurring;
    @FXML private Button btnMic;

    // =========================================================================
    // 2. STATE VARIABLES (Data to be saved)
    // =========================================================================
    private LocalDateTime taskDeadline;
    private String selectedPriority = "Low";
    private String selectedStatus = "Todo";
    private String selectedReccurring = null;
    private String selectedCategory = "General"; // Default category

    // =========================================================================
    // 3. BACKEND SERVICES & USE CASES
    // =========================================================================
    private NLPParserImpl aiParser;
    private final CaptureVoiceCommandUseCase voiceUseCase = new CaptureVoiceCommandUseCase();
    private TaskUseCase taskUseCase;

    // 👇 NEW: Calendar Service
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
    // 5. POPUP HANDLERS
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
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // 6. ACTION HANDLERS (AI & Voice)
    // =========================================================================

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

            // --- NEW: Handle Categories from AI ---
            if (result.category != null && !result.category.isEmpty()) {
                this.selectedCategory = capitalize(result.category);
                System.out.println("🏷️ AI detected category: " + this.selectedCategory);
            } else {
                this.selectedCategory = "General"; // Default if AI finds nothing
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

    // =========================================================================
    // 7. SAVE TASK HANDLER (Main Logic)
    // =========================================================================

    @FXML
    public void onSaveTask(ActionEvent event) {
        if (taskUseCase == null) {
            showAlert("Erreur Base de données", "Non connecté à la base de données.");
            return;
        }

        String currentUserId;
        if (UserSession.getInstance().getUser() != null) {
            currentUserId = UserSession.getInstance().getUser().id();
        } else {
            showAlert("Erreur d'authentification", "Aucun utilisateur connecté !");
            return;
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
            boolean isRecurring = selectedReccurring != null;
            RecurrenceType recurrence = mapRecurrence(selectedReccurring);

            TaskDTO newTaskDto = new TaskDTO(
                    title,
                    finalDescription,
                    selectedCategory,
                    priority,
                    taskDeadline,
                    isRecurring,
                    recurrence,
                    currentUserId,
                    null                 // Project ID (null for Inbox)
            );

            String newTaskId = taskUseCase.create(newTaskDto);
            System.out.println("✅ Task successfully saved! DB ID: " + newTaskId);

            // 2. 👇 SYNC TO GOOGLE CALENDAR
            if (taskDeadline != null) {
                System.out.println("🔄 Syncing to Google Calendar...");

                Task taskForSync = new Task(
                        newTaskId,          // ID
                        title,              // Titre
                        currentUserId,  // ✅ UTILISATION DE L'ID RÉEL ICI AUSSI
                        LocalDateTime.now() // Date de création
                );

                // B. Remplir les détails via la méthode officielle du domaine ✅
                taskForSync.updateDetails(
                        title,
                        finalDescription,
                        selectedCategory,
                        priority,
                        taskDeadline,
                        isRecurring, // boolean
                        recurrence,
                        null // ProjectID
                );

                // C. Envoyer au service Google
                new Thread(() -> {
                    try {
                        calendarService.syncTask(taskForSync);
                        System.out.println("✅ Google Calendar Sync Complete!");
                    } catch (Exception e) {
                        System.err.println("❌ Google Sync Failed: " + e.getMessage());
                    }
                }).start();
            }

            // 6. Reset Form
            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de sauvegarder : " + e.getMessage());
        }
    }

    // =========================================================================
    // 8. UI HELPER METHODS
    // =========================================================================

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
        this.selectedReccurring = recurringName;
        btnReccurring.setText(recurringName);
        String colorHex = switch (recurringName) {
            case "Daily"   -> "#25b84c";
            case "Weekly"  -> "#0D89FF";
            case "Monthly" -> "#eb8909";
            case "Yearly"  -> "#db4c3f";
            default        -> "#939595";
        };
        applyButtonStyle(btnReccurring, colorHex);
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
            case "HIGH", "ÉLEVÉE" -> PriorityLevel.NOT_URGENT_IMPORTANT;
            case "MEDIUM", "MOYENNE" -> PriorityLevel.URGENT_NOT_IMPORTANT;
            case "LOW", "BASSE" -> PriorityLevel.NOT_URGENT_NOT_IMPORTANT;
            default -> PriorityLevel.NOT_URGENT_NOT_IMPORTANT;
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

        // Reset Description Field
        descField.setText("");
        descField.setPromptText("Description");

        // Reset Variables
        taskDeadline = null;
        selectedPriority = "Low";
        selectedStatus = "Todo";
        selectedReccurring = null;
        selectedCategory = "General"; // Reset to default

        // Reset Buttons
        resetButtonStyle(btnDate, "Date");
        resetButtonStyle(btnPriority, "Priorité");
        resetButtonStyle(btnStatus, "Status");
        resetButtonStyle(btnReccurring, "Recurring");
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