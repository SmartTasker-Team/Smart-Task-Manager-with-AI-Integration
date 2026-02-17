package com.smarttask.manager.presentation.controllers.components;

import com.smarttask.manager.application.usecase.project.ProjectUseCase;
import com.smarttask.manager.domain.model.Project;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresProjectRepository;
import com.smarttask.manager.models.Model;
import com.smarttask.manager.presentation.controllers.components.modalDialog.AddProjectController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;

public class SidebarController implements Initializable {
    @FXML private VBox expandedSidebar;
    @FXML private VBox collapsedSidebar;
    private boolean isSidebarOpen = true;

    @FXML private HBox inboxItem;
    @FXML private HBox analyticsItem;
    @FXML private HBox todayItem;
    @FXML private HBox doneTasksItem;
    @FXML private HBox calendarItem;

    @FXML private Button notificationBtn;

    @FXML private VBox projectsContent;
    @FXML private SVGPath projectsIcon;
    private boolean isProjectsExpanded = true;

    @FXML private VBox teamContent;
    @FXML private SVGPath teamIcon;
    private boolean isTeamExpanded = true;
    @FXML private Button projectModalBtn;
    @FXML private Button teamProjectModalBtn;
    @FXML private HBox addTeamBox;
    private static final String ICON_EXPANDED  = "m19.5 8.25-7.5 7.5-7.5-7.5";
    private static final String ICON_COLLAPSED = "M4.5 15.75l7.5-7.5 7.5 7.5";

    private List<Region> allNavItems;
    private ProjectUseCase projectUseCase;

    @Override @FXML
    public void initialize(URL location, ResourceBundle resources) {
        updateSidebarState();

        allNavItems = Arrays.asList(inboxItem,analyticsItem, todayItem, doneTasksItem, calendarItem, notificationBtn);

        setActivePage("Inbox");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Inbox");

        new Thread(() -> {
            loadProjects();
        }).start();
    }

    public void loadProjects() {
        new Thread(() -> {
            try {
                if (this.projectUseCase == null) {
                    var connection = DatabaseConnection.getConnection();
                    var repo = new PostgresProjectRepository(connection);
                    this.projectUseCase = new ProjectUseCase(repo);
                }

                String userId = "685f976d-ef7d-4209-bea2-0ec5ffea7571";

                List<Project> personalProjects = projectUseCase.getPersonalProjects(userId);
                List<Project> teamProjects = projectUseCase.getTeamProjects(userId);

                Platform.runLater(() -> {
                    updateProjectLists(personalProjects, teamProjects);
                });

            } catch (Exception e) {
                System.err.println("Error loading projects: " + e.getMessage());
            }
        }).start();
    }

    private void updateProjectLists(List<Project> personalProjects, List<Project> teamProjects) {
        if (projectsContent != null) {
            projectsContent.getChildren().clear();
            for (Project p : personalProjects) {
                HBox item = createProjectItem(p, "#0D89FF");
                projectsContent.getChildren().add(item);
            }
        }

        if (teamContent != null) {
            teamContent.getChildren().clear();
            for (Project p : teamProjects) {
                HBox item = createProjectItem(p, "red");
                teamContent.getChildren().add(item);
            }
        }
    }
    private HBox createProjectItem(Project project, String colorHash) {
        HBox hbox = new HBox(12);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getStyleClass().add("nav-item");

        Label iconLabel = new Label("#");
        iconLabel.setStyle("-fx-text-fill: " + colorHash + "; -fx-font-size:16px; -fx-font-weight: bold;");

        Label nameLabel = new Label(project.getName());
        nameLabel.getStyleClass().add("nav-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label("0");
        countLabel.getStyleClass().add("badge-count");
        countLabel.setVisible(false);

        hbox.getChildren().addAll(iconLabel, nameLabel, spacer, countLabel);

        hbox.setOnMouseClicked(e -> {
            System.out.println("Selected Project: " + project.getName());
        });

        return hbox;
    }

    @FXML
    private void onToggleProjects() {
        isProjectsExpanded = !isProjectsExpanded;
        updateSectionState(projectsContent, projectsIcon, isProjectsExpanded);
    }

    @FXML
    private void onToggleTeam() {
        isTeamExpanded = !isTeamExpanded;
        updateSectionState(teamContent, teamIcon, isTeamExpanded);
    }

    private void updateSectionState(VBox content, SVGPath icon, boolean isExpanded) {
        content.setVisible(isExpanded);
        content.setManaged(isExpanded);

        if (isExpanded) {
            icon.setContent(ICON_EXPANDED);
        } else {
            icon.setContent(ICON_COLLAPSED);
        }
    }

    @FXML
    private void onInbox() {
        setActivePage("Inbox");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Inbox");
    }
    @FXML
    private void onAnalytics() {
        setActivePage("Analytics");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Analytics");
    }
    @FXML
    private void onToday() {
        setActivePage("Today");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Today");
    }
    @FXML
    private void onDoneTasks() {
        setActivePage("DoneTasks");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("DoneTasks");
    }
    @FXML
    private void onCalendar() {
        setActivePage("Calendar");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Calendar");
    }
    @FXML
    private void onNotification() {
        setActivePage("Notification");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Notification");
    }


    private void setActivePage(String pageName) {
        for (Region item : allNavItems) {
            if (item != null) {
                item.getStyleClass().removeAll("nav-item", "nav-item-selected");
                item.getStyleClass().add("nav-item");
            }
        }

        if (pageName.equals("Inbox") && inboxItem != null) {
            inboxItem.getStyleClass().add("nav-item-selected");
        } else if (pageName.equals("Today") && todayItem != null) {
            todayItem.getStyleClass().add("nav-item-selected");
        } else if (pageName.equals("Analytics") && analyticsItem != null) {
            analyticsItem.getStyleClass().add("nav-item-selected");
        } else if (pageName.equals("DoneTasks") && doneTasksItem != null) {
            doneTasksItem.getStyleClass().add("nav-item-selected");
        } else if (pageName.equals("Calendar") && calendarItem != null) {
            calendarItem.getStyleClass().add("nav-item-selected");
        } else if (pageName.equals("Notification") && notificationBtn != null) {
            notificationBtn.getStyleClass().add("nav-item-selected");
        }
    }

    @FXML
    private void onToggleSidebar() {
        isSidebarOpen = !isSidebarOpen;
        updateSidebarState();
    }

    private void updateSidebarState() {
        if (isSidebarOpen) {
            expandedSidebar.setVisible(true);
            expandedSidebar.setManaged(true);

            collapsedSidebar.setVisible(false);
            collapsedSidebar.setManaged(false);
        } else {
            expandedSidebar.setVisible(false);
            expandedSidebar.setManaged(false);

            collapsedSidebar.setVisible(true);
            collapsedSidebar.setManaged(true);
        }
    }
    @FXML
    private void openAddProjectDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddProjectDialog.fxml"));
            Parent root = loader.load();

            AddProjectController controller = loader.getController();

            controller.setOnProjectAdded(() -> {
                System.out.println("🔄 Nouveau projet détecté, rafraîchissement de la Sidebar...");
                this.loadProjects();
            });

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);


            Stage ownerStage = (Stage) projectModalBtn.getScene().getWindow();
            stage.initOwner(ownerStage);

            stage.initModality(Modality.WINDOW_MODAL);

            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error Opening Add Project Dialog: ");
        }
    }

    @FXML
    private void openAddTeamProjectDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddTeamProjectDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            Stage ownerStage = (Stage) teamProjectModalBtn.getScene().getWindow();
            stage.initOwner(ownerStage);

            stage.initModality(Modality.WINDOW_MODAL);

            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error OpeningAdd Team Project Dialog: ");
        }
    }

    @FXML
    private void openAddTeamDialog(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddTeamDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            Stage ownerStage = (Stage) addTeamBox.getScene().getWindow();

            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error Opening Add Team Dialog: ");
        }
    }

}