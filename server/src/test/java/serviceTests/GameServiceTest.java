package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @BeforeEach
    void setUp() throws DataAccessException {
        MemoryUserDAO.clear();
        MemoryAuthDAO.clear();
    }

    @Test
    void positiveListGames() throws DataAccessException {
        GameService gameService = new GameService();
        UserService userService = new UserService();

        AuthData authData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
        String authToken = authData.authToken();
        gameService.createGame(authToken, "testGame1");
        gameService.createGame(authToken, "testGame2");

        assertEquals(2, gameService.listGames(authToken).size());
        for (int i = 0; i < 2; i++) {
            assertEquals("testGame" + (i + 1), gameService.listGames(authToken).get(i).get("gameName"));
        }
    }

    @Test
    void negativeListGames() throws DataAccessException {
        GameService gameService = new GameService();
        UserService userService = new UserService();

        AuthData authData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
        String authToken = authData.authToken();
        gameService.createGame(authToken, "testGame1");
        gameService.createGame(authToken, "testGame2");
        assertThrows(DataAccessException.class, () -> gameService.listGames("badToken"));
    }

    @Test
    void positiveCreateGame() throws DataAccessException {
        GameService gameService = new GameService();
        UserService userService = new UserService();

        AuthData authData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
        String authToken = authData.authToken();
        gameService.createGame(authToken, "testGame1");
        gameService.createGame(authToken, "testGame2");

        assertEquals(2, gameService.listGames(authToken).size());
    }

    @Test
    void negativeCreateGame() throws DataAccessException {
        GameService gameService = new GameService();
        UserService userService = new UserService();

        AuthData authData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
        String authToken = authData.authToken();
        gameService.createGame(authToken, "testGame1");
        gameService.createGame(authToken, "testGame2");
        assertThrows(DataAccessException.class, () -> gameService.createGame(authToken, "testGame2"));
    }

    @Test
    void joinGame() {
    }
}