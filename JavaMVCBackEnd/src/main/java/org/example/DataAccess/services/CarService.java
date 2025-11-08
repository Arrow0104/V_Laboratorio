package org.example.DataAccess.services;

import org.example.Domain.models.Car;
import org.example.Domain.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Hibernate;

import java.util.List;

public class CarService {
    private final SessionFactory sessionFactory;

    public CarService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------
    // CREATE
    // -------------------------
    public Car createCar(String make, String model, int year, Long ownerId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();  // INICIAR TRANSACCIÓN PRIMERO

            User owner = session.find(User.class, ownerId);

            if (owner == null) {  // VALIDAR QUE EL USUARIO EXISTE
                throw new IllegalArgumentException("User with ID " + ownerId + " not found");
            }

            Car car = new Car();
            car.setMake(make);
            car.setModel(model);
            car.setYear(year);
            car.setOwner(owner);

            session.persist(car);
            tx.commit();

            // Inicializar el owner antes de devolver
            Hibernate.initialize(car.getOwner());

            return car;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String message = String.format("An error occurred when processing: %s. Details: %s", "createCar", e.getMessage());
            System.out.println(message);
            e.printStackTrace();
            throw e;
        }
    }

    // -------------------------
    // READ
    // -------------------------
    public Car getCarById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Car.class, id);
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getCarById", e);
            System.out.println(message);
            throw e;
        }
    }

    public List<Car> getAllCars() {
        try (Session session = sessionFactory.openSession()) {
            List<Car> cars = session.createQuery("FROM Car", Car.class).list();
            cars.forEach(car -> Hibernate.initialize(car.getOwner()));
            return cars;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getAllCars", e);
            System.out.println(message);
            throw e;
        }
    }

    public List<Car> getCarsByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            List<Car> cars = session.createQuery("FROM Car WHERE owner = :owner", Car.class)
                    .setParameter("owner", user)
                    .list();
            cars.forEach(car -> Hibernate.initialize(car.getOwner()));
            return cars;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getCarsByUser", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // UPDATE
    // -------------------------
    public Car updateCar(Long carId, String make, String model, int year) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Car car = session.find(Car.class, carId);
            if (car != null) {
                car.setMake(make);
                car.setModel(model);
                car.setYear(year);
                session.merge(car);
                Hibernate.initialize(car.getOwner());
            }

            tx.commit();
            return car;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String message = String.format("An error occurred when processing: %s. Details: %s", "updateCar", e);
            System.out.println(message);
            throw e;
        }
    }

    // -------------------------
    // DELETE
    // -------------------------
    public boolean deleteCar(Long carId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Car car = session.find(Car.class, carId);
            if (car != null) {
                session.remove(car);
                tx.commit();
                return true;
            }

            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String message = String.format("An error occurred when processing: %s. Details: %s", "deleteCar", e);
            System.out.println(message);
            throw e;
        }
    }
}