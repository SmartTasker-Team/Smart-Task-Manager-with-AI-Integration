package com.smarttask.manager;

import com.smarttask.manager.application.dto.ProjectDTO;
import com.smarttask.manager.application.usecase.project.ProjectUseCase;
import com.smarttask.manager.application.usecase.team.TeamUseCase;
import com.smarttask.manager.domain.model.User;
import com.smarttask.manager.infrastructure.persistence.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.UUID;



public class PostgresProjectFullTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PostgresUserRepository userRepo = new PostgresUserRepository(conn);
            PostgresTeamRepository teamRepo = new PostgresTeamRepository(conn);
            PostgresProjectRepository projectRepo = new PostgresProjectRepository(conn);

            // Initialisation des Use Cases
            TeamUseCase teamUseCase = new TeamUseCase(teamRepo);
            ProjectUseCase projectUseCase = new ProjectUseCase(projectRepo);

            // 1. Création du propriétaire
            String userId = UUID.randomUUID().toString();
            User owner = new User(userId, "Manager", "manager@task.com", "Secure123");
            userRepo.save(owner);

            // 2. CRÉATION DE L'ÉQUIPE VIA LE USE CASE
            // C'est ici que team_members sera rempli !
            System.out.println("--- Création de l'équipe via Use Case ---");
            String teamId = teamUseCase.createTeam("Alpha Team", userId);
            System.out.println("Équipe créée et membre ajouté.");

            // 3. Création du projet
            ProjectDTO dto = new ProjectDTO("Logiciel IA", "Description", userId, teamId);
            String projectId = projectUseCase.createProject(dto);

            System.out.println("Projet créé avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}