package com.smarttask.manager.domain.model;

import java.time.LocalDateTime;
import java.time.Duration;

public class TimeLog {
    private String idTimeLog;
    private String taskId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationSeconds;

    public TimeLog(String idTimeLog, String taskId) {
        this.idTimeLog = idTimeLog;
        this.taskId = taskId;
        this.startTime = LocalDateTime.now();
        this.durationSeconds = 0;
    }

    public void stopLog() {
        this.endTime = LocalDateTime.now();
        this.durationSeconds = Duration.between(startTime, endTime).getSeconds();
    }

    public String getIdTimeLog() { return idTimeLog; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public long getDurationSeconds() { return durationSeconds; }

    /*public static void main(String[] args) {
        System.out.println("=== TEST DU TRACKING DE TEMPS ===");

        // 1. Instanciation (Le chrono démarre au constructeur)
        TimeLog monLog = new TimeLog("LOG-001", "TASK-101");
        System.out.println("Démarrage à : " + monLog.getStartTime());

        try {
            // 2. Simulation d'un travail de 3 secondes
            System.out.println("Travail en cours... (pause de 3 secondes)");
            Thread.sleep(3000);

            // 3. Arrêt du chrono
            monLog.stopLog();

            // 4. Vérification des résultats
            System.out.println("Fin à : " + monLog.getEndTime());
            System.out.println("Durée totale calculée : " + monLog.getDurationSeconds() + " secondes");

            // Test logique simple
            if (monLog.getDurationSeconds() >= 3) {
                System.out.println(" TEST RÉUSSI : Le calcul de la durée est correct.");
            } else {
                System.out.println(" TEST ÉCHOUÉ : La durée est incorrecte.");
            }

        } catch (InterruptedException e) {
            System.err.println("Le test a été interrompu.");
        }
    }
     */
}