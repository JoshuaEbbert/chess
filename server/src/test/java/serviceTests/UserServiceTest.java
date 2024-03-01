package serviceTests;

import dataAccess.DBDAO.SQLUserDAO;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @BeforeEach
    void setUp() throws DataAccessException {
        SQLUserDAO.clear();
        SQLAuthDAO.clear();
    }

    @Test
    void positiveRegister() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        service.register(newUser1);

        assertEquals(newUser1.username(), SQLUserDAO.getUser("testUser").username());

        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");
        service.register(newUser2);

        assertEquals(newUser2.username(), SQLUserDAO.getUser("kate").username());
    }

    @Test
    void negativeRegister() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        service.register(newUser1);

        assertThrows(DataAccessException.class, () -> service.register(newUser1));
    }

    @Test
    void positiveLogin() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");

        SQLUserDAO.createUser(newUser1.username(), newUser1.password(), newUser1.email());
        SQLUserDAO.createUser(newUser2.username(), newUser2.password(), newUser2.email());

        AuthData auth1 = service.login(newUser1);
        AuthData auth2 = service.login(newUser2);

        assertNotNull(auth1);
        assertNotNull(auth2);

        assert(SQLAuthDAO.listAuths().contains(auth1));
        assert(SQLAuthDAO.listAuths().contains(auth2));
        assert(SQLAuthDAO.listAuths().size() == 2);
    }

    @Test
    void negativeLogin() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");
        UserData unauthorizedUser2 = new UserData("kate", "wrongPassword", "katestrong92@gmail.com");

        SQLUserDAO.createUser(newUser1.username(), newUser1.password(), newUser1.email());
        SQLUserDAO.createUser(newUser2.username(), newUser2.password(), newUser2.email());

        AuthData auth1 = service.login(newUser1);

        assert(SQLAuthDAO.listAuths().size() == 1);
        assertThrows(DataAccessException.class, () -> service.login(unauthorizedUser2));
        assert(SQLAuthDAO.listAuths().size() == 1);
    }

    @Test
    void positiveLogout() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");

        AuthData auth1 = service.register(newUser1);
        AuthData auth2 = service.register(newUser2);

        assert(SQLAuthDAO.listAuths().size() == 2);
        service.logout(new AuthData(auth1.authToken(), ""));
        assert(SQLAuthDAO.listAuths().size() == 1);
        service.logout(new AuthData(auth2.authToken(), ""));
        assert(SQLAuthDAO.listAuths().isEmpty());
    }

    @Test
    void negativeLogout() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");

        AuthData auth1 = service.register(newUser1);
        AuthData auth2 = service.register(newUser2);

        service.logout(new AuthData(auth1.authToken(), ""));
        assertThrows(DataAccessException.class, () -> service.logout(new AuthData(auth1.authToken(), "")));
    }
}