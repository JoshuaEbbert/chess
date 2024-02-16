package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    private final HashSet<UserData> users = new HashSet<UserData>();

    public void createUser(String username, String password, String email) throws DataAccessException {
        users.add(new UserData(username, password, email));
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData u : users) {
            if (u.username().equals(username)) {
                return u;
            }
        }

        throw new DataAccessException("User not found");
    }

    public HashSet<UserData> listUsers() throws DataAccessException {
        return users;
    }

    public void clear() throws DataAccessException {
        users.clear();
    }
}
