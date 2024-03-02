package dataAccessTests;

import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

import java.util.HashSet;

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
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername"));
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername"));
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername2"));
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername2"));
    }
    @Test
    void negativeCreateAuth() { // I am not sure what a negative test would look like for this as all verification happens BEFORE it is created...
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername"));
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername"));
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername2"));
        assertDoesNotThrow(() -> SQLAuthDAO.createAuth("testUsername2"));
    }

    @Test
    void positiveDeleteAuth() throws DataAccessException {
        AuthData auth1 = SQLAuthDAO.createAuth("testUsername");
        AuthData auth2 = SQLAuthDAO.createAuth("testUsername");
        AuthData auth3 = SQLAuthDAO.createAuth("testUsername2");
        AuthData auth4 = SQLAuthDAO.createAuth("testUsername2");
        assertDoesNotThrow(() -> SQLAuthDAO.deleteAuth(auth1.authToken()));
        assertDoesNotThrow(() -> SQLAuthDAO.deleteAuth(auth2.authToken()));
        assertDoesNotThrow(() -> SQLAuthDAO.deleteAuth(auth3.authToken()));
        assertDoesNotThrow(() -> SQLAuthDAO.deleteAuth(auth4.authToken()));
    }
    @Test
    void negativeDeleteAuth() {
        assertThrows(DataAccessException.class, () -> SQLAuthDAO.deleteAuth("bogusToken"));
    }

    @Test
    void positiveVerifyAuth() throws DataAccessException{
        AuthData auth1 = SQLAuthDAO.createAuth("testUsername");
        AuthData auth2 = SQLAuthDAO.createAuth("testUsername");
        assertDoesNotThrow(() -> SQLAuthDAO.verifyAuth(auth1.authToken()));
        assertDoesNotThrow(() -> SQLAuthDAO.verifyAuth(auth2.authToken()));
    }
    @Test
    void negativeVerifyAuth() throws DataAccessException {
        AuthData auth1 = SQLAuthDAO.createAuth("testUsername");
        AuthData auth2 = SQLAuthDAO.createAuth("testUsername");
        assertThrows(DataAccessException.class, () -> SQLAuthDAO.verifyAuth("bogusToken"));
        assertDoesNotThrow(() -> SQLAuthDAO.verifyAuth(auth1.authToken()));
        assertDoesNotThrow(() -> SQLAuthDAO.verifyAuth(auth2.authToken()));
        SQLAuthDAO.deleteAuth(auth1.authToken());
        SQLAuthDAO.deleteAuth(auth2.authToken());
        assertThrows(DataAccessException.class, () -> SQLAuthDAO.verifyAuth(auth1.authToken()));
        assertThrows(DataAccessException.class, () -> SQLAuthDAO.verifyAuth(auth2.authToken()));
    }

    @Test
    void clear() throws DataAccessException {
        AuthData auth = SQLAuthDAO.createAuth("testUsername");
        SQLAuthDAO.clear();
        assertThrows(DataAccessException.class, () -> SQLAuthDAO.verifyAuth(auth.authToken()));
    }

    @Test
    void positiveListAuths() throws DataAccessException {
        AuthData auth1 = SQLAuthDAO.createAuth("testUsername");
        AuthData auth2 = SQLAuthDAO.createAuth("testUsername2");

        assertTrue(SQLAuthDAO.listAuths().contains(auth1));
        assertTrue(SQLAuthDAO.listAuths().contains(auth2));
    }

    @Test
    void negativeListAuths() throws DataAccessException {
        assertFalse(SQLAuthDAO.listAuths().contains(new AuthData("bogusToken", "bogusUsername")));

        AuthData auth1 = SQLAuthDAO.createAuth("testUsername");
        AuthData auth2 = SQLAuthDAO.createAuth("testUsername2");

        assertEquals(SQLAuthDAO.listAuths(), new HashSet<AuthData>() {{ // make sure no extra auths are in the hashset
            add(auth1);
            add(auth2);
        }});
    }
}