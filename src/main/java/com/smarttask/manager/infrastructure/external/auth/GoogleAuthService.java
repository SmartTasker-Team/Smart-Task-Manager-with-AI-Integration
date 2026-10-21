package com.smarttask.manager.infrastructure.external.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.smarttask.manager.application.usecase.auth.GoogleAuthUseCase;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;

public class GoogleAuthService {

    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private GoogleAuthUseCase googleAuthUseCase;

    private static final List<String> LOGIN_SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email",
            CalendarScopes.CALENDAR
    );

    private static final List<String> CALENDAR_SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email",
            CalendarScopes.CALENDAR
    );

    public GoogleAuthService() {}

    /**
     * Optional: Set the GoogleAuthUseCase for handling user creation/retrieval.
     * If not set, only authentication will work.
     */
    public void setGoogleAuthUseCase(GoogleAuthUseCase useCase) {
        this.googleAuthUseCase = useCase;
    }

    public Userinfo login() {
        return authenticate(LOGIN_SCOPES);
    }

    public boolean connectCalendar() {
        Userinfo user = authenticate(CALENDAR_SCOPES);
        return user != null;
    }

    public String registerOrLoginGoogleUser(Userinfo userinfo) {
        if (googleAuthUseCase == null) {
            System.err.println("ERROR: GoogleAuthUseCase not set. Cannot register Google user.");
            return null;
        }

        try {
            String userId = googleAuthUseCase.registerOrLoginGoogleUser(
                userinfo.getId(),
                userinfo.getName(),
                userinfo.getEmail()
            );
            System.out.println("Google user registered/logged in: " + userId);
            return userId;
        } catch (Exception e) {
            System.err.println("Error registering Google user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Calendar getCalendarClient() {
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    GsonFactory.getDefaultInstance(),
                    new InputStreamReader(new FileInputStream("src/main/resources/credentials.json"))
            );

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets,
                    CALENDAR_SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            Credential credential = flow.loadCredential("user");

            if (credential == null) {
                return null;
            }

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Task AI")
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    public Userinfo authenticate(List<String> scopes) {
        try {
            FileInputStream in = new FileInputStream("src/main/resources/credentials.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    GsonFactory.getDefaultInstance(),
                    new InputStreamReader(in)
            );

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets,
                    scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            if (scopes.contains(CalendarScopes.CALENDAR) &&
                    !credential.getExpirationTimeMilliseconds().equals(0L)) {
                System.out.println("✅ Calendar permission granted!");
            }

            return new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Taske AI")
                    .build()
                    .userinfo().get().execute();

        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}