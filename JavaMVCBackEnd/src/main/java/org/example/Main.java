package org.example;

import org.example.API.controllers.AuthController;
import org.example.API.controllers.CarController;
import org.example.API.controllers.MaintenanceController;
import org.example.DataAccess.services.AuthService;
import org.example.DataAccess.services.CarService;
import org.example.DataAccess.services.MaintenanceService;
import org.example.DataAccess.HibernateUtil;
import org.example.Server.SocketServer;
import org.example.Server.MessageBroadcaster;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var sessionFactory = HibernateUtil.getSessionFactory();

        // Inicializar servicios y controladores
        AuthService authService = new AuthService(sessionFactory);
        AuthController authController = new AuthController(authService);

        CarService carService = new CarService(sessionFactory);
        CarController carController = new CarController(carService, authService);

        MaintenanceService maintenanceService = new MaintenanceService(sessionFactory);
        MaintenanceController maintenanceController = new MaintenanceController(maintenanceService, carService);

        // Servidor para request/response (API-like)
        int requestPort = 7000;
        SocketServer requestServer = new SocketServer(
                requestPort,
                authController,
                carController,
                maintenanceController // <-- Agregado aquí
        );

        // Servidor para chat/broadcasting (conexiones persistentes)
        int messagePort = 7001;
        MessageBroadcaster messageBroadcaster = new MessageBroadcaster(messagePort, requestServer);

        // Registrar el broadcaster
        requestServer.setMessageBroadcaster(messageBroadcaster);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down servers...");
            requestServer.stop();
            messageBroadcaster.stop();
        }));

        var createUsers = true;
        if(createUsers) {
            authService.register("user1", "user1@example.com", "pass1", "USER");
            authService.register("user2", "user2@example.com", "pass2", "USER");
        }

        // Iniciar servidores
        requestServer.start();
        messageBroadcaster.start();
        System.out.println("Servers started - Requests: " + requestPort + ", Messages: " + messagePort);
    }
}

//Backend version final

