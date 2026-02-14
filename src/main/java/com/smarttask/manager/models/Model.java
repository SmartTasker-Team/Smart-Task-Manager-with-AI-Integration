package com.smarttask.manager.models;

import com.smarttask.manager.application.dto.UserDTO;
import com.smarttask.manager.infrastructure.external.auth.GoogleAuthService;
import com.google.api.services.oauth2.model.Userinfo;
import com.smarttask.manager.infrastructure.session.UserSession;
import com.smarttask.manager.presentation.views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;

    // On garde la logique "calculée" (c'est plus sûr)
    private Model() {
        this.viewFactory = new ViewFactory();

        // 👇 C'EST ICI LA MAGIE : On tente de restaurer la session au démarrage
        checkExistingSession();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    /**
     * Vérifie si l'utilisateur est connecté (en regardant la session).
     * C'est cette méthode que votre App.java appelle pour choisir la vue.
     */
    public boolean isUserLoggedIn() {
        return UserSession.getInstance().getUser() != null;
    }

    /**
     * Tente de récupérer les infos Google sans ouvrir le navigateur.
     * Si le fichier 'tokens' existe, ça marche tout seul !
     */
    private void checkExistingSession() {
        try {
            GoogleAuthService authService = new GoogleAuthService();

            // Si on a déjà un token valide sur le disque...
            // Note: Vous devrez peut-être ajouter une méthode simple dans GoogleAuthService
            // pour vérifier l'existence du token sans faire l'appel réseau complet si vous voulez être très rapide.
            // Mais pour l'instant, essayons de nous connecter silencieusement :

            Userinfo googleUser = authService.login(); // Tente le login silencieux

            if (googleUser != null) {
                // ✅ SUCCÈS : On a retrouvé l'utilisateur !
                UserDTO restoredUser = new UserDTO(
                        googleUser.getId(),
                        googleUser.getEmail(),
                        googleUser.getName()
                );

                // On remplit la session immédiatement
                UserSession.getInstance().login(restoredUser);

                System.out.println("⚡ Auto-login success: Welcome back " + googleUser.getName());
            }
        } catch (Exception e) {
            // Pas de panique, c'est juste que l'utilisateur n'était pas connecté
            System.out.println("ℹ️ No previous session found. Starting fresh.");
        }
    }
}