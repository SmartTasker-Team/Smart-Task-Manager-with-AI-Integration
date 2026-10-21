package com.smarttask.manager.presentation.controllers.components;

import com.smarttask.manager.application.usecase.project.ProjectUseCase;
import com.smarttask.manager.domain.model.Project;
import com.smarttask.manager.infrastructure.persistence.DatabaseConnection;
import com.smarttask.manager.infrastructure.persistence.PostgresProjectRepository;
import com.smarttask.manager.infrastructure.persistence.PostgresTeamRepository;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.models.Model;
import com.smarttask.manager.presentation.controllers.components.modalDialog.AddProjectController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.AddTeamController;
import com.smarttask.manager.presentation.controllers.components.modalDialog.AddTeamProjectController;
import com.smarttask.manager.application.usecase.team.TeamUseCase;
import com.smarttask.manager.domain.model.Team;
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
import java.util.stream.Collectors;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

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

    @FXML private VBox teamsContainer;
    @FXML private VBox teamContent;
    @FXML private SVGPath teamIcon;
    private boolean isTeamExpanded = true;
    @FXML private Button projectModalBtn;
    @FXML private HBox addTeamBox;
    private static final String ICON_EXPANDED  = "m19.5 8.25-7.5 7.5-7.5-7.5";
    private static final String ICON_COLLAPSED = "M4.5 15.75l7.5-7.5 7.5 7.5";

    // 👇 NEW USER PROFILE FIELDS
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Text userInitial;

    private List<Region> allNavItems;
    private ProjectUseCase projectUseCase;
    private TeamUseCase teamUseCase;

    @Override @FXML
    public void initialize(URL location, ResourceBundle resources) {
        updateSidebarState();

        allNavItems = Arrays.asList(inboxItem,analyticsItem, todayItem, doneTasksItem, calendarItem, notificationBtn);

        setActivePage("Inbox");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Inbox");
        loadUserProfile();
        new Thread(this::loadData).start();
    }

    private void loadUserProfile() {
        if (UserSession.getInstance().getUser() != null) {
            var user = UserSession.getInstance().getUser();

            if (userNameLabel != null) {
                userNameLabel.setText(user.username());
            }

            if (userEmailLabel != null) {
                userEmailLabel.setText(user.email());
            }

            if (userInitial != null && user.username() != null && !user.username().isEmpty()) {
                userInitial.setText(user.username().substring(0, 1).toUpperCase());
            }
        }
    }
    public void loadData() {
        try {
            if (this.projectUseCase == null || this.teamUseCase == null) {
                var connection = DatabaseConnection.getConnection();

                var projectRepo = new PostgresProjectRepository(connection);
                this.projectUseCase = new ProjectUseCase(projectRepo);

                var teamRepo = new PostgresTeamRepository(connection);
                this.teamUseCase = new TeamUseCase(teamRepo);
            }

            String currentUserId = null;
            if (UserSession.getInstance().getUser() != null) {
                currentUserId = UserSession.getInstance().getUser().id();
            } else {
                throw new RuntimeException("Utilisateur non connecté !");
            }

            List<Project> personalProjects = projectUseCase.getPersonalProjects(currentUserId);
            List<Project> allTeamProjects = projectUseCase.getTeamProjects(currentUserId);
            List<Team> userTeams = teamUseCase.getTeamsForUser(currentUserId);

            Platform.runLater(() -> {
                updatePersonalProjects(personalProjects);
                updateTeamsList(userTeams, allTeamProjects);
            });

        } catch (Exception e) {
            System.err.println("Error loading sidebar data: " + e.getMessage());
        }
    }

    private void updatePersonalProjects(List<Project> projects) {
        if (projectsContent != null) {
            projectsContent.getChildren().clear();
            for (Project p : projects) {
                projectsContent.getChildren().add(createProjectItem(p, "#0D89FF"));
            }
        }
    }

    private void updateTeamsList(List<Team> teams, List<Project> allTeamProjects) {
        if (teamsContainer == null) return;
        teamsContainer.getChildren().clear();

        for (Team team : teams) {
            HBox teamHeader = createTeamHeader(team);

            VBox teamProjectsBox = new VBox();
            teamProjectsBox.getStyleClass().add("equipe-projects-list");

            List<Project> projectsForThisTeam = allTeamProjects.stream()
                    .filter(p -> p.getTeamId() != null && p.getTeamId().equals(team.getId()))
                    .collect(Collectors.toList());

            for (Project p : projectsForThisTeam) {
                teamProjectsBox.getChildren().add(createProjectItem(p, "red"));
            }

            teamsContainer.getChildren().add(teamHeader);
            teamsContainer.getChildren().add(teamProjectsBox);
        }
    }
    private HBox createTeamHeader(Team team) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("equipe-header");

        Label nameLabel = new Label(team.getName());
        nameLabel.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addProjectBtn = new Button();
        addProjectBtn.getStyleClass().add("icon-button-transparent");

        SVGPath plusIcon = new SVGPath();
        plusIcon.setContent("M12 10.5v6m3-3H9m4.06-7.19-2.12-2.12a1.5 1.5 0 0 0-1.061-.44H4.5A2.25 2.25 0 0 0 2.25 6v12a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9a2.25 2.25 0 0 0-2.25-2.25h-5.379a1.5 1.5 0 0 1-1.06-.44Z");
        plusIcon.setFill(Color.TRANSPARENT);
        plusIcon.setStroke(Color.web("#939595"));
        plusIcon.setStrokeWidth(1.75);
        plusIcon.setScaleX(0.7);
        plusIcon.setScaleY(0.7);

        addProjectBtn.setGraphic(plusIcon);

        addProjectBtn.setOnAction(e -> openAddTeamProjectDialog(team.getId()));

        header.getChildren().addAll(nameLabel, spacer, addProjectBtn);
        return header;
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

        hbox.getChildren().addAll(iconLabel, nameLabel, spacer);

        hbox.setOnMouseClicked(e -> {
            System.out.println("Open Project: " + project.getName());
            // TODO: Naviguer vers la vue projet
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
                this.loadData();
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
    private void openAddTeamProjectDialog(String teamId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddTeamProjectDialog.fxml"));
            Parent root = loader.load();

            AddTeamProjectController controller = loader.getController();

            controller.setTeamId(teamId);

            controller.setOnProjectAdded(() -> {
                System.out.println("🔄 Nouveau projet d'équipe ajouté, rafraîchissement...");
                this.loadData();
            });

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            if (teamsContainer.getScene() != null) {
                Stage ownerStage = (Stage) teamsContainer.getScene().getWindow();
                stage.initOwner(ownerStage);

                stage.setX(ownerStage.getX());
                stage.setY(ownerStage.getY());
                stage.setWidth(ownerStage.getWidth());
                stage.setHeight(ownerStage.getHeight());
            }

            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error Opening Add Team Project Dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddTeamDialog(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddTeamDialog.fxml"));
            Parent root = loader.load();

            AddTeamController controller = loader.getController();

            controller.setOnTeamAdded(() -> {
                System.out.println("🔄 Nouvelle équipe détectée, rafraîchissement...");
                this.loadData();
            });

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