package com.smarttask.manager.infrastructure.external.calendar;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.smarttask.manager.domain.model.Task;
import com.smarttask.manager.infrastructure.external.auth.GoogleAuthService;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

public class CalendarSyncService {

    private final GoogleAuthService authService;

    public CalendarSyncService() {
        this.authService = new GoogleAuthService();
    }

    public void syncTask(Task task) {
        new Thread(() -> {
            try {
                Calendar service = authService.getCalendarClient();

                if (service == null) {
                    System.out.println("Calendar not connected yet. Skipping sync.");
                    return;
                }

                if (task.getDueDate() == null) {
                    System.out.println("Skipping Calendar Sync: Task '" + task.getTitle() + "' has no due date.");
                    return;
                }

                Event event = new Event()
                        .setSummary(task.getTitle())
                        .setDescription("Category: " + task.getCategory() + "\nPriority: " + task.getPriority());

                Date startDate = Date.from(task.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
                DateTime start = new DateTime(startDate);
                event.setStart(new EventDateTime().setDateTime(start));

                Date endDate = Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant());
                DateTime end = new DateTime(endDate);
                event.setEnd(new EventDateTime().setDateTime(end));

                service.events().insert("primary", event).execute();

                System.out.println("Task synced to Google Calendar: " + task.getTitle());

            } catch (GoogleJsonResponseException e) {
                System.err.println("Google API Error: " + e.getDetails().getMessage());
                System.err.println("Error Code: " + e.getStatusCode());

                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Network error syncing calendar.");
            }
        }).start();
    }
}