package dataAccess.DBDAO;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.*;

import static dataAccess.DatabaseManager.createDatabase;
import static dataAccess.DatabaseManager.initializeDatabase;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    @BeforeAll
    static void setUp() {
        try {
            createDatabase();
            initializeDatabase();
            SQLAuthDAO.clear();
        } catch (Throwable ex) {
            System.out.println("Error creating database: " + ex.getMessage());
        }
    }

    @BeforeEach
    void emptyDB() {
        try {
            SQLAuthDAO.clear();
        } catch (Throwable ex){
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            SQLAuthDAO.clear();
        } catch (Throwable ex){
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @Test
    void positiveCreateAuth() throws DataAccessException {
        SQLAuthDAO.createAuth("testUsername");
        SQLAuthDAO.createAuth("testUsername");
        SQLAuthDAO.createAuth("testUsername2");
        SQLAuthDAO.createAuth("testUsername2");
    }
    @Test
    void negativeCreateAuth() {
    }

    @Test
    void positiveDeleteAuth() {
    }
    @Test
    void negativeDeleteAuth() {
    }

    @Test
    void positiveVerifyAuth() {
    }
    @Test
    void negativeVerifyAuth() {
    }

    @Test
    void clear() {
    }

    @Test
    void positiveListAuths() {
    }

    @Test
    void negativeListAuths() {
    }
}