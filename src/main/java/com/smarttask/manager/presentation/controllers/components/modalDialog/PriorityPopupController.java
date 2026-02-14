package com.smarttask.manager.presentation.controllers.components.modalDialog;

import javafx.fxml.FXML;
import javafx.stage.Popup;
import java.util.function.Consumer;

public class PriorityPopupController {

    private Popup parentPopup;
    private Consumer<String> onSelectCallback;

    public void setParentPopup(Popup popup) {
        this.parentPopup = popup;
    }

    public void setOnSelect(Consumer<String> callback) {
        this.onSelectCallback = callback;
    }

    // These methods just pass the name string
    @FXML void setUrgent() { select("Urgent"); }
    @FXML void setHigh()   { select("High"); }
    @FXML void setMedium() { select("Medium"); }
    @FXML void setLow()    { select("Low"); }

    private void select(String name) {
        if (onSelectCallback != null) {
            onSelectCallback.accept(name);
        }
        if (parentPopup != null) parentPopup.hide();
    }
}