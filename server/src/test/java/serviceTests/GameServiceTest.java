package serviceTests;

import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DBDAO.SQLUserDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import dataAccess.MemoryDAO.MemoryGameDAO;
import dataAccess.MemoryDAO.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @BeforeEach
    void setUp() throws DataAccessException {
        SQLUserDAO.clear();
        SQLAuthDAO.clear();
        SQLGameDAO.clear();
    }

    @AfterAll
    static void cleanUp() throws DataAccessException {
        SQLUserDAO.clear();
        SQLAuthDAO.clear();
        SQLGameDAO.clear();
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
    void positiveJoinGame() throws DataAccessException {
        GameService gameService = new GameService();
        UserService userService = new UserService();

        AuthData authData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
        String authToken = authData.authToken();
        int gameID1 = gameService.createGame(authToken, "testGame1");
        int gameID2 = gameService.createGame(authToken, "testGame2");

        gameService.joinGame(authToken, "WHITE", gameID1);
        gameService.joinGame(authToken, "BLACK", gameID2);

        GameData game = SQLGameDAO.getGame(gameID1);
        assert(game.whiteUsername().equals("testUser"));
        assertNull(game.blackUsername());

        GameData game2 = SQLGameDAO.getGame(gameID2);
        assertNull(game2.whiteUsername());
        assert(game2.blackUsername().equals("testUser"));

        gameService.joinGame(authToken, null, gameID2);
        assertNull(SQLGameDAO.getGame(gameID2).whiteUsername());
    }

    @Test
    void negativeJoinGame() throws DataAccessException {
        GameService gameService = new GameService();
        UserService userService = new UserService();

        AuthData authData = userService.register(new UserData("testUser", "testPassword", "testEmail"));
        String authToken = authData.authToken();
        int gameID1 = gameService.createGame(authToken, "testGame1");
        int gameID2 = gameService.createGame(authToken, "testGame2");

        gameService.joinGame(authToken, "WHITE", gameID1);
        gameService.joinGame(authToken, "BLACK", gameID2);
        assertThrows(DataAccessException.class, () -> gameService.joinGame(authToken, "WHITE", gameID1));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(authToken, "BLACK", gameID2));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(authToken, "WHITE", 1002));
    }
}