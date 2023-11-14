package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dto.UserDTO;
import software.amazon.awssdk.enhanced.dynamodb.Key;

public interface UserDAOInterface {
    UserDTO get(String alias);
    void update(UserDTO dto);
    void insert(UserDTO dto);
    void delete(Key key);
}
