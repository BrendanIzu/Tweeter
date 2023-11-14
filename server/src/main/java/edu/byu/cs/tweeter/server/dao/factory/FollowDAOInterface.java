package edu.byu.cs.tweeter.server.dao.factory;

import java.util.List;

import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FollowersDTO;
import edu.byu.cs.tweeter.server.dto.FollowingDTO;

public interface FollowDAOInterface {
    DataPage<FollowersDTO> getPageOfFollowers(String alias, String lastFollower, int limit);
    DataPage<FollowingDTO> getPageOfFollowees(String alias, String lastFollowee, int limit);
    List<FollowersDTO> getAllFollowers(String alias);
    List<FollowingDTO> getAllFollowees(String alias);
    FollowingDTO getFollowing(String follower, String followee);
    void insert(FollowingDTO dto);
    void delete(String follower, String followee);
}
