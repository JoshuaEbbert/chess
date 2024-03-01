package dataAccess.DBDAO;

import dataAccess.DBDAO.SQLUserDAO;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dataAccess.DatabaseManager.createDatabase;
import static dataAccess.DatabaseManager.initializeDatabase;
import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    @BeforeAll
    static void setUp() {
        try {
            createDatabase();
            initializeDatabase();
            SQLUserDAO.clear();
        } catch (Throwable ex) {
            System.out.println("Error creating database: " + ex.getMessage());
        }
    }

    @BeforeEach
    void emptyDB() {
        try {
            SQLUserDAO.clear();
        } catch (Throwable ex){
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            SQLUserDAO.clear();
        } catch (Throwable ex){
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @Test
    void positiveCreateUser() throws DataAccessException {
        SQLUserDAO.createUser("testUsername", "testPassword", "testEmail");
        SQLUserDAO.createUser("testUsername1", "testPassword1", "testEmail1");
        SQLUserDAO.createUser("testUsername2", "testPassword2", "testEmail2");
    }

    @Test
    void negativeCreateUser() throws DataAccessException {
        SQLUserDAO.createUser("testUsername", "testPassword", "testEmail");
        assertThrows(DataAccessException.class, () -> SQLUserDAO.createUser("testUsername", "testPassword", "testEmail"));
    }

    @Test
    void positiveGetUser() throws DataAccessException{
        SQLUserDAO.createUser("testUsername", "testPassword", "testEmail");
        SQLUserDAO.createUser("testUsername1", "testPassword1", "testEmail1");
        SQLUserDAO.createUser("testUsername2", "testPassword2", "testEmail2");
        assertNotNull(SQLUserDAO.getUser("testUsername"));
        assertNotNull(SQLUserDAO.getUser("testUsername1"));
        assertNotNull(SQLUserDAO.getUser("testUsername2"));
    }

    @Test
    void negativeGetUser() throws DataAccessException{
        SQLUserDAO.createUser("testUsername", "testPassword", "testEmail");
        assertNull(SQLUserDAO.getUser("testUsername1"));
        assertNull(SQLUserDAO.getUser("testUsername2"));
    }

    @Test
    void clear() throws DataAccessException {
        SQLUserDAO.createUser("testUsername", "testPassword", "testEmail");
        SQLUserDAO.clear();
        SQLUserDAO.createUser("testUsername", "testPassword", "testEmail");

    }
}