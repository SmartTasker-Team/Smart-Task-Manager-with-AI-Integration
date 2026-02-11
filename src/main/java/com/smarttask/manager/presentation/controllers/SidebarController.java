package com.smarttask.manager.presentation.controllers;

import com.smarttask.manager.models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.geometry.Side;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML private VBox expandedSidebar;
    @FXML private VBox collapsedSidebar;
    private boolean isSidebarOpen = true;

    @FXML private HBox inboxItem;
    @FXML private HBox todayItem;
    @FXML private Button notificationBtn;

    @FXML private VBox projectsContent;
    @FXML private SVGPath projectsIcon;
    private boolean isProjectsExpanded = true;

    @FXML private VBox teamContent;
    @FXML private SVGPath teamIcon;
    private boolean isTeamExpanded = true;
    @FXML
    private Button addBtn;
    private static final String ICON_EXPANDED  = "m19.5 8.25-7.5 7.5-7.5-7.5";
    private static final String ICON_COLLAPSED = "M4.5 15.75l7.5-7.5 7.5 7.5";

    private List<Region> allNavItems;

    @Override @FXML
    public void initialize(URL location, ResourceBundle resources) {
        updateSidebarState();

        allNavItems = Arrays.asList(inboxItem, todayItem, notificationBtn);

        setActivePage("Inbox");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Inbox");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem addProject = new MenuItem("Ajouter un projet");
        MenuItem browseTemplates = new MenuItem("Parcourir les modèles");

        contextMenu.getItems().addAll(addProject, browseTemplates);

        addBtn.setOnMouseClicked(event -> {
            contextMenu.show(addBtn, Side.BOTTOM, 0, 0);
        });

        addProject.setOnAction(e -> {
            System.out.println("Add Project clicked!");
        });
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
}