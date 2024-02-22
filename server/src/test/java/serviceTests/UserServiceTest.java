package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import static dataAccess.MemoryAuthDAO.listAuths;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void positiveRegister() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        service.register(newUser1);

        assertEquals(newUser1, MemoryUserDAO.getUser("testUser"));

        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");
        service.register(newUser2);

        assertEquals(newUser2, MemoryUserDAO.getUser("kate"));
        assertEquals(2, MemoryUserDAO.listUsers().size());
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

        MemoryUserDAO.createUser(newUser1.username(), newUser1.password(), newUser1.email());
        MemoryUserDAO.createUser(newUser2.username(), newUser2.password(), newUser2.email());

        AuthData auth1 = service.login(newUser1);
        AuthData auth2 = service.login(newUser2);

        assertNotNull(auth1);
        assertNotNull(auth2);

        assert(MemoryAuthDAO.listAuths().contains(auth1));
        assert(MemoryAuthDAO.listAuths().contains(auth2));
        assert(MemoryAuthDAO.listAuths().size() == 2);
    }

    @Test
    void negativeLogin() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");
        UserData unauthorizedUser2 = new UserData("kate", "wrongPassword", "katestrong92@gmail.com");

        MemoryUserDAO.createUser(newUser1.username(), newUser1.password(), newUser1.email());
        MemoryUserDAO.createUser(newUser2.username(), newUser2.password(), newUser2.email());

        AuthData auth1 = service.login(newUser1);

        assertThrows(DataAccessException.class, () -> service.login(newUser1));
        assert(MemoryAuthDAO.listAuths().size() == 1);
        assertThrows(DataAccessException.class, () -> service.login(unauthorizedUser2));
        assert(MemoryAuthDAO.listAuths().size() == 1);
    }

    @Test
    void positiveLogout() throws DataAccessException {
        UserService service = new UserService();
        UserData newUser1 = new UserData("testUser", "testPassword", "testEmail");
        UserData newUser2 = new UserData("kate", "charlie123", "katestrong92@gmail.com");

        AuthData auth1 = service.register(newUser1);
        AuthData auth2 = service.register(newUser2);

        assert(MemoryAuthDAO.listAuths().size() == 2);
        service.logout(new AuthData(auth1.authToken(), ""));
        assert(MemoryAuthDAO.listAuths().size() == 1);
        service.logout(new AuthData(auth2.authToken(), ""));
        assert(MemoryAuthDAO.listAuths().isEmpty());
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