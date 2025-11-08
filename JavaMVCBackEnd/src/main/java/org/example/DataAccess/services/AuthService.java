package org.example.DataAccess.services;

import org.example.Domain.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class AuthService {
    private final SessionFactory sessionFactory;

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public AuthService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User register(String username, String email, String password, String role) {
        try (Session session = sessionFactory.openSession()) {
            if (getUserByUsername(username) != null || getUserByEmail(email) != null) {
                throw new IllegalArgumentException("Username or email already in use");
            }
            String salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setSalt(salt);
            user.setPasswordHash(hashedPassword);
            user.setRole(role);

            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();

            return user;
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "register", e);
            System.out.println(message);
            throw e;
        }
    }

    public boolean login(String usernameOrEmail, String password) {
        try{
            User user = getUserByUsername(usernameOrEmail);
            if (user == null) {
                user = getUserByEmail(usernameOrEmail);
            }
            if (user == null) {
                return false;
            }
            String hashedInput = hashPassword(password, user.getSalt());
            return hashedInput.equals(user.getPasswordHash());
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "login", e);
            System.out.println(message);
            throw e;
        }
    }

    public User getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getUserByUsername", e);
            System.out.println(message);
            throw e;
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            String message = String.format("An error occurred when processing: %s. Details: %s", "getUserByEmail", e);
            System.out.println(message);
            throw e;
        }
    }

    // Modificado para aceptar el ID como token
    public User getUserByToken(String token) {
        if (token == null || token.isEmpty()) return null;
        try {
            long userId = Long.parseLong(token);
            try (Session session = sessionFactory.openSession()) {
                return session.get(User.class, userId);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[SALT_LENGTH];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error inesperado al intentar crear el hash del usuario.", e);
        }
    }
}


