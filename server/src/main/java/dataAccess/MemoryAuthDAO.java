package dataAccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO {
    private HashSet<AuthData> auths = new HashSet<AuthData>();

    public void createAuth(String username) throws DataAccessException {
        auths.add(new AuthData(UUID.randomUUID().toString(), username));
    }

}
