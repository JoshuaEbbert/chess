package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import service.BaseService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTests {
    @Test
    public void positiveTest() throws DataAccessException {
        MemoryGameDAO.createGame("testGame");
        MemoryAuthDAO.createAuth("testUser");
        MemoryUserDAO.createUser("testUser", "testPassword", "testEmail");

        BaseService service = new BaseService();
        service.clear();

        assertEquals(0, MemoryGameDAO.listGames().size());
        assertEquals(0, MemoryAuthDAO.listAuths().size());
    }

    // Dr. Rodham said that we don't need to test the negative case for this method.
}