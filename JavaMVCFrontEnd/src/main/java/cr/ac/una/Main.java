package cr.ac.una;

import Presentation.Controllers.LoginController;
import Presentation.Views.LoginView;
import Services.AuthService;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Intentar activar Nimbus antes de crear componentes Swing
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Si Nimbus no está disponible, usar el LAF del sistema como fallback
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            AuthService authService = new AuthService("localhost", 7000);
            LoginController loginController = new LoginController(loginView, authService);
            loginController.addObserver(loginView);
            loginView.setVisible(true);
        });
    }
}
//Version Final


