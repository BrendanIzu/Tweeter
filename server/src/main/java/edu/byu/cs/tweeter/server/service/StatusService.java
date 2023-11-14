package edu.byu.cs.tweeter.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFactory;
import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FeedsDTO;
import edu.byu.cs.tweeter.server.dto.StoriesDTO;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import edu.byu.cs.tweeter.server.pojo.UpdateFeedBatchPOJO;

public class StatusService extends ServiceTools {
    String POST_STATUS_QUEUE = "https://sqs.us-west-2.amazonaws.com/083321675250/PostStatusQueue";

    public StatusService(DynamoFactory factory) {
        this.factory = factory;
    }

    public FeedResponse getFeed(FeedRequest request) {
        if (request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        Status lastStatus = request.getLastStatus();
        String lastStatusId = "";

        if (request.getLastStatus() != null) {
            lastStatusId = getMD5Hash(lastStatus.getUser() + lastStatus.getPost() + lastStatus.getTimestamp().toString());
        }

        DataPage<FeedsDTO> result = getFeedsDAO().getPageOfFeeds(request.getUserAlias(), lastStatusId, request.getLimit());

        List<Status> feed = new ArrayList<>();

        for (FeedsDTO dto : result.getValues()) {
            UserDTO userDTO = getUserDAO().get(dto.getAuthor());
            User user = userDTO.convertToUser();
            Status status = dto.convertToStatus(user);

            feed.add(status);
        }

        return new FeedResponse(feed, result.isHasMorePages());
    }

    public StoryResponse getStory(StoryRequest request) {
        if (request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        Status lastStatus = request.getLastStatus();

        String lastStatusId = "";

        if (request.getLastStatus() != null) {
            lastStatusId = getMD5Hash(lastStatus.getUser() + lastStatus.getPost() + lastStatus.getTimestamp().toString());
        }

        DataPage<StoriesDTO> result = getStoriesDAO().getPageOfStories(request.getUserAlias(), lastStatusId, request.getLimit());

        UserDTO userDTO = getUserDAO().get(request.getUserAlias());
        User user = userDTO.convertToUser();

        List<Status> story = new ArrayList<>();

        for (StoriesDTO dto : result.getValues()) {
            Status status = dto.convertToStatus(user);
            story.add(status);
        }

        return new StoryResponse(story, result.isHasMorePages());
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException ("[Bad Request] Request needs to have a status");
        }

        // 1. Create new DTO from request Status
        StoriesDTO storiesDTO = new StoriesDTO(request.getStatus());

        // 2. Insert DTO into stories table
        getStoriesDAO().insert(storiesDTO);

        // 3. write message to queue containing the status information and the alias that we need to write each new feed object
        Gson gson = new GsonBuilder().create();

        String jsonString = gson.toJson(request.getStatus());
        insertMessageIntoSQS(jsonString, POST_STATUS_QUEUE);

        return new PostStatusResponse();
    }

    public void updateFeeds(String message) {
        Gson gson = new GsonBuilder().create();
        UpdateFeedBatchPOJO pojo = gson.fromJson(message, UpdateFeedBatchPOJO.class);
        Status status = pojo.getStatus();
        List<FeedsDTO> dtos = new ArrayList<>();

        // 1. Create batch of FeedsDTOs
        for (String alias : pojo.getAliases()) {
            FeedsDTO feedsDTO = new FeedsDTO(alias, status);
            dtos.add(feedsDTO);
        }

        // 2. Write batch
        getFeedsDAO().writeBatchOfFeeds(dtos);
    }
}
