package org.example.DataAccess.services;

import org.example.Domain.models.Car;
import org.example.Domain.models.Maintenance;
import org.example.Domain.models.MaintenanceType;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceService {
    private final SessionFactory sessionFactory;

    public MaintenanceService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------
    // CREATE
    // -------------------------
    public Maintenance createMaintenance(String description, MaintenanceType type, Car carMaintenance, Date cardate) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Maintenance maintenance = new Maintenance();
            maintenance.setDescription(description);
            maintenance.setType(type);
            maintenance.setCarMaintenance(carMaintenance);
            maintenance.setCardate(cardate);

            session.persist(maintenance);
            tx.commit();

            // Asegurar que la relación esté inicializada antes de cerrar la sesión
            Hibernate.initialize(maintenance.getCarMaintenance());
            return maintenance;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "createMaintenance", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // READ
    // -------------------------
    public Maintenance getMaintenanceById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Maintenance maintenance = session.find(Maintenance.class, id);
            if (maintenance != null) {
                Hibernate.initialize(maintenance.getCarMaintenance());
            }
            return maintenance;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getMaintenanceById", e);
            System.out.println(message);
            throw e;
        }
    }

    public List<Maintenance> getAllMaintenanceByCarId(Long carId) {
        try (Session session = sessionFactory.openSession()) {
            Car car = session.find(Car.class, carId);
            if (car == null) return new ArrayList<>();

            List<Maintenance> maintenances = session
                    .createQuery("FROM Maintenance WHERE carMaintenance = :car", Maintenance.class)
                    .setParameter("car", car)
                    .list();

            maintenances.forEach(m -> Hibernate.initialize(m.getCarMaintenance()));
            return maintenances;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getAllMaintenanceByCarId", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // UPDATE
    // -------------------------
    public Maintenance updateMaintenance(Long maintenanceId, String description, MaintenanceType type, Car carMaintenance, Date cardate) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Maintenance maintenance = session.find(Maintenance.class, maintenanceId);
            if (maintenance != null) {
                maintenance.setDescription(description);
                maintenance.setType(type);
                maintenance.setCarMaintenance(carMaintenance);
                maintenance.setCardate(cardate);
                session.merge(maintenance);

                // Inicializar relación antes de cerrar sesión
                Hibernate.initialize(maintenance.getCarMaintenance());
            }

            tx.commit();
            return maintenance;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "updateMaintenance", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // DELETE
    // -------------------------
    public boolean deleteMaintenance(Long maintenanceId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Maintenance maintenance = session.find(Maintenance.class, maintenanceId);
            if (maintenance != null) {
                session.remove(maintenance);
                tx.commit();
                return true;
            }

            tx.rollback();
            return false;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "deleteMaintenance", e);
            System.out.println(message);
            throw e;
        }
    }
}

