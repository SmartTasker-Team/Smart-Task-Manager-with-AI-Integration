package com.smarttask.manager.presentation.controllers.components.modalDialog;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Popup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Consumer;

public class DateTimePopupController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourBox;
    @FXML private ComboBox<String> minuteBox;

    private Popup parentPopup;
    private Consumer<LocalDateTime> onSaveCallback;

    @FXML
    public void initialize() {
        // 1. Fill Hours (00 - 23)
        for (int i = 0; i < 24; i++) {
            hourBox.getItems().add(String.format("%02d", i));
        }

        // 2. Fill Minutes (00 - 59) -> CHANGED HERE
        for (int i = 0; i < 60; i++) { // i++ means every minute
            minuteBox.getItems().add(String.format("%02d", i));
        }

        // 3. Set Default to NOW
        LocalTime now = LocalTime.now();
        hourBox.setValue(String.format("%02d", now.getHour()));
        minuteBox.setValue(String.format("%02d", now.getMinute()));
    }

    public void setParentPopup(Popup popup) {
        this.parentPopup = popup;
    }

    public void setOnSave(Consumer<LocalDateTime> callback) {
        this.onSaveCallback = callback;
    }

    @FXML void setToday() { selectDate(LocalDate.now()); }
    @FXML void setTomorrow() { selectDate(LocalDate.now().plusDays(1)); }
    @FXML void setWeekend() { selectDate(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY))); }

    private void selectDate(LocalDate date) {
        datePicker.setValue(date);
    }

    @FXML
    private void applyDate() {
        LocalDate date = datePicker.getValue();
        if (date == null) date = LocalDate.now();

        // Get current time for fallback
        LocalTime now = LocalTime.now();

        // Use selected value OR fallback to 'now'
        String hh = hourBox.getValue() != null ? hourBox.getValue() : String.format("%02d", now.getHour());
        String mm = minuteBox.getValue() != null ? minuteBox.getValue() : String.format("%02d", now.getMinute());

        try {
            LocalTime time = LocalTime.parse(hh + ":" + mm);

            if (onSaveCallback != null) {
                onSaveCallback.accept(LocalDateTime.of(date, time));
            }
            close();
        } catch (Exception e) {
            System.out.println("Invalid Time Format");
        }
    }

    @FXML
    private void close() {
        if (parentPopup != null) parentPopup.hide();
    }
}