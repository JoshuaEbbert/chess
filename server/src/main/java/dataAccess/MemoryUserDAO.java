package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    private final static HashSet<UserData> users = new HashSet<UserData>();

    public static void createUser(String username, String password, String email) throws DataAccessException {
        users.add(new UserData(username, password, email));
    }

    public static UserData getUser(String username) {
        for (UserData u : users) {
            if (u.username().equals(username)) {
                return u;
            }
        }

        return null;
    }

    public static void clear() throws DataAccessException {
        users.clear();
    }
}
