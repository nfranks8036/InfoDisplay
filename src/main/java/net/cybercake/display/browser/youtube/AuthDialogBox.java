package net.cybercake.display.browser.youtube;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.nio.charset.Charset;

public class AuthDialogBox extends JFrame {

    private volatile String authorizationCode;
    private JTextField authCodeField;
    private JButton submitButton;

    public AuthDialogBox() {
        this.authorizationCode = null;
        this.authCodeField = null;
        this.submitButton = null;
    }

    public void open() {
        if (this.submitButton != null || this.authCodeField != null)
            throw new IllegalStateException("Already open");

        this.setTitle("Enter Authorization Code");
        this.setSize(600, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel explanation = new JLabel("A browser window was opened, follow the prompts and copy the code in the URL.");
        panel.add(explanation);

        JLabel label = new JLabel("Enter URL when authorized: ");
        panel.add(label);

        this.authCodeField = new JTextField(25);
        panel.add(this.authCodeField);

        this.submitButton = new JButton("Submit");
        panel.add(submitButton);

        this.submitButton.addActionListener(e -> {
            try {
                String url = this.authCodeField.getText().trim();
                for (NameValuePair param : URLEncodedUtils.parse(new URI(url), Charset.defaultCharset())) {
                    if (!param.getName().equalsIgnoreCase("code"))
                        continue;
                    this.authorizationCode = param.getValue();
                }
                if (this.authorizationCode == null || this.authorizationCode.isEmpty()) {
                    JOptionPane.showMessageDialog(AuthDialogBox.this, "No authorization code entered, exiting!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                this.dispose();
            } catch (Exception exception) {
                throw new RuntimeException("Authorization code failed to submit dialog box: " + exception, exception);
            }
        });

        this.add(panel);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void dispose() {
        this.authCodeField = null;
        this.submitButton = null;
        super.dispose();
    }

    public @Nullable String getAuthorizationCode() {
        return this.authorizationCode;
    }
}
