package edu.byu.cs.tweeter.server.dao.factory;

import java.util.List;

import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FeedsDTO;

public interface FeedsDAOInterface {
    DataPage<FeedsDTO> getPageOfFeeds(String alias, String lastStatus, int limit);

    void writeBatchOfFeeds(List<FeedsDTO> dtos);

//    StoryResponse getStory(StoryRequest request);
//    PostStatusResponse postStatus(PostStatusRequest request);
}
