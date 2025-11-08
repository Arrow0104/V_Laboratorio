package Presentation.Controllers;

import Domain.Dtos.auth.UserResponseDto;
import Presentation.Observable;
import Presentation.Views.LoginView;
import Presentation.Views.MainView;
import Services.AuthService;
import Services.CarService;
import Services.MaintenanceService;
import Utilities.EventType;

import javax.swing.*;

public class LoginController extends Observable {

    private final LoginView loginView;
    private final AuthService authService;

    public LoginController(LoginView loginView, AuthService authService) {
        this.loginView = loginView;
        this.authService = authService;

        this.loginView.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Username or password cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginView.showLoading(true);

        SwingWorker<UserResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected UserResponseDto doInBackground() throws Exception {
                return authService.login(username, password).get();
            }

            @Override
            protected void done() {
                loginView.showLoading(false);
                try {
                    UserResponseDto user = get();
                    if (user != null) {
                        openMainView(user);
                        loginView.dispose();
                    } else {
                        JOptionPane.showMessageDialog(loginView, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(loginView, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void openMainView(UserResponseDto user) {
        MainView mainView = new MainView();

        String host = "localhost";
        int serverPort = 7000;

        CarService carService = new CarService(host, serverPort);
        MaintenanceService maintenanceService = new MaintenanceService(host, serverPort);

        Long currentUserId = user.getId();

        CarsController carsController = new CarsController(mainView, carService, currentUserId);
        MaintenancesController maintenancesController = new MaintenancesController(mainView, maintenanceService, carService, currentUserId);

        carsController.setMaintenancesController(maintenancesController);

        // Ya no se usa AddTabs, simplemente muestra la vista principal
        mainView.setVisible(true);
    }
}





