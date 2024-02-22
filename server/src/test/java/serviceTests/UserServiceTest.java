package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

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
}