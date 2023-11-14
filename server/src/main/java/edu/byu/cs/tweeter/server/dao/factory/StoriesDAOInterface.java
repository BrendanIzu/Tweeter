package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FeedsDTO;
import edu.byu.cs.tweeter.server.dto.StoriesDTO;

public interface StoriesDAOInterface {
    DataPage<StoriesDTO> getPageOfStories(String alias, String lastStatus, int limit);
    void insert(StoriesDTO dto);
}
