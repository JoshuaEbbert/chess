package clientTests;

import dataAccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import service.BaseService;
import serverLogic.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    BaseService service = new BaseService();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDB() throws DataAccessException {
        service.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void positiveTestLogin() throws ResponseException {
        AuthData auth1 = facade.register("username", "password", "email");
        AuthData auth2 = facade.login("username", "password");
        assertTrue(auth2.authToken().length() > 10);
        assertNotEquals(auth1.authToken(), auth2.authToken());
    }

    @Test
    void negativeTestLogin() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.login("unregisteredUsername", "wrongPassword"));
    }

    @Test
    void positiveTestRegister() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void negativeTestRegister() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register(null, "newPassword", "newEmail"));;
    }

    @Test
    void negativeTestLogout() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    void positiveTestLogout() throws ResponseException {
        AuthData authData1 = facade.register("newUsername", "newPassword", "newEmail");
        facade.logout(authData1.authToken());
        AuthData authData2 = facade.login("newUsername", "newPassword");
        facade.logout(authData2.authToken());

        assertThrows(ResponseException.class, () -> facade.createGame(authData2.authToken(), "newGame!"));
    }

    @Test
    void negativeTestCreateGame() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.createGame(authData.authToken(), "newGame!"));
    }

    @Test
    void positiveTestCreateGame() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        int gameID = facade.createGame(authData.authToken(), "newGame!");
        assertTrue(gameID > 0);
    }

    @Test
    void positiveTestListGames() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        int gameID = facade.createGame(authData.authToken(), "newGame!");
        assertFalse(facade.listGames(authData.authToken()).isEmpty());
    }

    @Test
    void negativeTestListGames() throws ResponseException {
        AuthData login = facade.register("newUsername", "newPassword", "newEmail");
        facade.logout(login.authToken());
        assertThrows(ResponseException.class, () -> facade.listGames(login.authToken()));

        AuthData newLogin = facade.login("newUsername", "newPassword");
        facade.createGame(newLogin.authToken(), "newGame1!");
        facade.createGame(newLogin.authToken(), "newGame2!");
        facade.createGame(newLogin.authToken(), "newGame3!");

        assertEquals(3, facade.listGames(newLogin.authToken()).size());
    }

    @Test
    void positiveTestJoinGame() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        int gameID = facade.createGame(authData.authToken(),"newGame!");
        facade.joinGame(authData.authToken(),"WHITE", gameID);
    }

    @Test
    void negativeTestJoinGame() throws ResponseException {
        AuthData authData = facade.register("newUsername", "newPassword", "newEmail");
        int gameID = facade.createGame(authData.authToken(), "newGame!");
        facade.joinGame(authData.authToken(), "WHITE", gameID);
        assertThrows(ResponseException.class, () -> facade.joinGame(authData.authToken(), "WHITE", gameID));

        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.joinGame(authData.authToken(), "BLACK", gameID));
    }
}
