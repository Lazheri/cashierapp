package com.cashier.controller;

import com.cashier.SessionContext;
import com.cashier.dao.UserDAO;
import com.cashier.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SessionContext.clear();
        userDAO = new UserDAO();
        clearMessage();
    }

    @FXML
    private void handleLogin() {
        clearMessage();

        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez saisir un nom d'utilisateur et un mot de passe.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            showMessage("Nom d'utilisateur ou mot de passe incorrect.");
            passwordField.clear();
            return;
        }

        SessionContext.setCurrentUser(user);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            showMessage("Erreur lors de l'ouverture de l'application: " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }
}
