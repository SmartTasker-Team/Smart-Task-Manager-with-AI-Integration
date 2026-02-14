package com.smarttask.manager.presentation.controllers.components;

import com.smarttask.manager.models.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
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

    @Override @FXML
    public void initialize(URL location, ResourceBundle resources) {
        updateSidebarState();

        allNavItems = Arrays.asList(inboxItem, todayItem, notificationBtn);

        setActivePage("Inbox");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Inbox");
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
            // HIDE Expanded, SHOW Collapsed
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

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            // 1. Get the Main Window (The "Owner")
            Stage ownerStage = (Stage) projectModalBtn.getScene().getWindow();
            stage.initOwner(ownerStage);

            // 2. IMPORTANT: Use WINDOW_MODAL
            // This blocks input to YOUR app, but lets you click other apps/taskbar
            stage.initModality(Modality.WINDOW_MODAL);

            // 3. Match the size and position of the Main Window exactly
            // This makes the "dimmed background" cover only your app
            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            // 4. (Optional) Sync them so if you move the main app, the popup follows
            // Since it's a modal, you can't move the main app anyway, so step 3 is usually enough.

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddTeamProjectDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddTeamProjectDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            // 1. Get the Main Window (The "Owner")
            Stage ownerStage = (Stage) teamProjectModalBtn.getScene().getWindow();
            stage.initOwner(ownerStage);

            // 2. IMPORTANT: Use WINDOW_MODAL
            // This blocks input to YOUR app, but lets you click other apps/taskbar
            stage.initModality(Modality.WINDOW_MODAL);

            // 3. Match the size and position of the Main Window exactly
            // This makes the "dimmed background" cover only your app
            stage.setX(ownerStage.getX());
            stage.setY(ownerStage.getY());
            stage.setWidth(ownerStage.getWidth());
            stage.setHeight(ownerStage.getHeight());

            // 4. (Optional) Sync them so if you move the main app, the popup follows
            // Since it's a modal, you can't move the main app anyway, so step 3 is usually enough.

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddTeamDialog(MouseEvent event) { // Change argument to MouseEvent (optional but good practice)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/modalDialog/AddTeamDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            // 2. USE THE HBOX TO GET THE WINDOW
            // We use 'addTeamBox' here because 'teamModalBtn' no longer exists
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
            e.printStackTrace();
        }
    }

}