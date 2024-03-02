package dataAccessTests;

import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;

import static dataAccess.DatabaseManager.createDatabase;
import static dataAccess.DatabaseManager.initializeDatabase;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    @BeforeAll
    static void setUp() {
        try {
            createDatabase();
            initializeDatabase();
            SQLGameDAO.clear();
        } catch (Throwable ex) {
            System.out.println("Error creating database: " + ex.getMessage());
        }
    }

    @BeforeEach
    void emptyDB() {
        try {
            SQLGameDAO.clear();
        } catch (Throwable ex){
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            SQLGameDAO.clear();
        } catch (Throwable ex){
            System.out.println("Error clearing database: " + ex.getMessage());
        }
    }

    @Test
    void positiveCreateGame() throws DataAccessException {
        SQLGameDAO.createGame("testGame");
        assertNotNull(SQLGameDAO.getGame("testGame"));
    }

    @Test
    void negativeCreateGame() throws DataAccessException { // negative test difficult; checks for duplicate games occurs in gameService prior to game insertion
        assertDoesNotThrow(() -> SQLGameDAO.createGame("testGame"));
        assertDoesNotThrow(() -> SQLGameDAO.createGame("testGame2"));
        assertDoesNotThrow(() -> SQLGameDAO.createGame("testGame3"));
        assertDoesNotThrow(() -> SQLGameDAO.createGame("testGame4"));
    }

    @Test
    void positiveStrGetGame() throws DataAccessException {
        SQLGameDAO.createGame("testGame");
        assertNotNull(SQLGameDAO.getGame("testGame"));
    }

    @Test
    void negativeStrGetGame() throws DataAccessException {
        assertNull(SQLGameDAO.getGame("nonexistentGame"));
    }

    @Test
    void positiveIntGetGame() throws DataAccessException {
        int gameID = SQLGameDAO.createGame("testGame");
        assertNotNull(SQLGameDAO.getGame(gameID));
    }

    @Test
    void negativeIntGetGame() throws DataAccessException {
        assertNull(SQLGameDAO.getGame(1000));
    }

    @Test
    void positiveListGames() throws DataAccessException {
        SQLGameDAO.createGame("testGame");
        SQLGameDAO.createGame("testGame2");
        SQLGameDAO.createGame("testGame3");

        assertEquals(3, SQLGameDAO.listGames().size());
    }

    @Test
    void negativeListGames() throws DataAccessException {
        assertEquals(0, SQLGameDAO.listGames().size());
    }

    @Test
    void positiveAddPlayer() throws DataAccessException {
        int gameID = SQLGameDAO.createGame("testGame");
        SQLGameDAO.addPlayer("WHITE", gameID, "testUser");
        SQLGameDAO.addPlayer("BLACK", gameID, "testUser2");
        assertNotNull(SQLGameDAO.getGame(gameID));

        GameData game = SQLGameDAO.getGame(gameID);
        assertEquals("testUser", game.whiteUsername());
        assertEquals("testUser2", game.blackUsername());
    }

    @Test
    void negativeAddPlayer() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> SQLGameDAO.addPlayer("WHITE", 1000, "testUser"));
        assertThrows(DataAccessException.class, () -> SQLGameDAO.addPlayer("BLACK", 1000, "testUser2"));
    }

    @Test
    void clear() throws DataAccessException {
        SQLGameDAO.createGame("testGame");
        SQLGameDAO.clear();
        assertEquals(0, SQLGameDAO.listGames().size());
    }
}