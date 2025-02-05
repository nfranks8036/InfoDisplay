package net.cybercake.display.browser.youtube;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import net.cybercake.display.args.ArgumentReader;
import net.cybercake.display.utils.Log;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Scanner;

@SuppressWarnings("CallToPrintStackTrace")
public class YouTubeAuthentication {

    private static final String CLIENT_SECRET_FILE = "client_secret.json";
    private static final String REFRESH_TOKEN_FILE = "refresh_token.txt";
    private static final String REDIRECT_URI = "https://localhost";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final ArgumentReader args;
    private @Nullable AuthDialogBox dialogBox;

    private String refreshToken;

    public YouTubeAuthentication(ArgumentReader args) {
        this.args = args;
        this.dialogBox = null;
    }

    private boolean checkAlreadyAuthenticated() {
        try {
            File file = new File(REFRESH_TOKEN_FILE);
            if (!file.exists())
                return false;
            if (file.length() == 0)
                return false;

            Scanner scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            this.refreshToken = builder.toString();

            return true;
        } catch (Exception exception) {
            Log.line("Failed to check if YouTube is already authenticating, continuing as if without...");
            exception.printStackTrace();
            return false;
        }
    }

    public void requestAuthentication() {
        try {
            if (checkAlreadyAuthenticated()) {
                return;
            }

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CLIENT_SECRET_FILE));
            Log.line("Client secret located at " + CLIENT_SECRET_FILE);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singleton("https://www.googleapis.com/auth/youtube.force-ssl")
            ).setAccessType("offline").build();

            String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
            Log.line("Opening URL to authorize application... " + authorizationUrl);
            Desktop.getDesktop().browse(new URI(authorizationUrl));

            Log.line("Enter authorization code when ready in the dialog box.");
            long start = System.currentTimeMillis();
            this.dialogBox = new AuthDialogBox();
            SwingUtilities.invokeLater(() -> YouTubeAuthentication.this.dialogBox.open());
            do {
                if (System.currentTimeMillis() - start >= 120_000) {
                    throw new IllegalStateException("No authorization code was inputted after 120s, exiting program!");
                }

            } while (this.dialogBox.getAuthorizationCode() == null);

            System.out.println("Authorization code found: " + this.dialogBox.getAuthorizationCode());

            GoogleTokenResponse googleTokenResponse = flow.newTokenRequest(this.dialogBox.getAuthorizationCode())
                    .setRedirectUri(REDIRECT_URI)
                    .execute();
            Log.line("Creating new token request and executing... (" + googleTokenResponse.toPrettyString() + ")");

            String refreshToken = googleTokenResponse.getRefreshToken();
            Log.line("Found refresh token: " + refreshToken);

            Files.writeString(
                    Path.of(REFRESH_TOKEN_FILE),
                    refreshToken,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception exception) {
            throw new RuntimeException("Failed to authenticate YouTube correctly: " + exception, exception);
        }
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

}
