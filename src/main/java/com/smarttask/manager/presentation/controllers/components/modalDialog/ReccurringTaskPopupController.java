package com.smarttask.manager.presentation.controllers.components.modalDialog;

import javafx.fxml.FXML;
import javafx.stage.Popup;
import java.util.function.Consumer;

public class ReccurringTaskPopupController {
    private Popup parentPopup;
    private Consumer<String> onSelectCallback;

    public void setParentPopup(Popup popup) {
        this.parentPopup = popup;
    }

    public void setOnSelect(Consumer<String> callback) {
        this.onSelectCallback = callback;
    }


    @FXML
    void setDaily() { select("Daily"); }
    @FXML void setMonthly()   { select("Monthly"); }
    @FXML void setWeekly() { select("Weekly"); }
    @FXML void setYearly()    { select("Yearly"); }

    private void select(String name) {
        if (onSelectCallback != null) {
            onSelectCallback.accept(name);
        }
        if (parentPopup != null) parentPopup.hide();
    }
}
