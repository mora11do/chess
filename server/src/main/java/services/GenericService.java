package services;

import dataaccess.AuthDAO;


public class GenericService {
    public final AuthDAO authDAO;

    public GenericService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public boolean authIsReal(String authToken){
        return authDAO.getAuth(authToken) != null;
    }
}
