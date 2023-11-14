package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dto.AuthDTO;

public interface AuthDAOInterface {
    AuthDTO get(String token);
    void insert(AuthDTO dto);
    void delete(AuthDTO dto);
}
