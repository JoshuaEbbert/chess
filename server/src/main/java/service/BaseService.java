package service;

import dataAccess.*;
import dataAccess.DBDAO.SQLAuthDAO;
import dataAccess.DBDAO.SQLGameDAO;
import dataAccess.DBDAO.SQLUserDAO;

public class BaseService {
    public void clear() throws DataAccessException {
        SQLGameDAO.clear();
        SQLAuthDAO.clear();
        SQLUserDAO.clear();
    }
}
