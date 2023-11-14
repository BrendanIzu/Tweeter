package edu.byu.cs.tweeter.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFactory;
import edu.byu.cs.tweeter.server.dto.AuthDTO;
import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FollowersDTO;
import edu.byu.cs.tweeter.server.dto.FollowingDTO;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import edu.byu.cs.tweeter.server.pojo.UpdateFeedBatchPOJO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends ServiceTools {
    String UPDATE_FEED_QUEUE = "https://sqs.us-west-2.amazonaws.com/083321675250/UpdateFeedQueue";

    public FollowService(DynamoFactory factory) {
        this.factory = factory;
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        DataPage<FollowersDTO> result = getFollowingDAO().getPageOfFollowers(request.followerAlias, request.getLastFollowerAlias(), request.getLimit());

        List<User> followers = new ArrayList<>();
        for (FollowersDTO dto : result.getValues()) {
            User addUser = dto.convertToUser();
            followers.add(addUser);
        }

        return new FollowersResponse(followers, result.isHasMorePages());
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        UserDTO userDTO = getUserDAO().get(request.getTargetUserAlias());

        return new FollowersCountResponse(userDTO.getFollowersCount());
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        DataPage<FollowingDTO> result = getFollowingDAO().getPageOfFollowees(request.followerAlias, request.getLastFolloweeAlias(), request.getLimit());

        List<User> followees = new ArrayList<>();
        for (FollowingDTO dto : result.getValues()) {
            User addUser = dto.convertToUser();
            followees.add(addUser);
        }

        return new FollowingResponse(followees, result.isHasMorePages());
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        UserDTO userDTO = getUserDAO().get(request.getTargetUserAlias());

        return new FollowingCountResponse(userDTO.getFollowingCount());
    }

    public IsFollowerResponse isfollower(IsFollowerRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        } else if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }

        FollowingDTO followingDTO = getFollowingDAO().getFollowing(request.getFollower().getAlias(), request.getFollowee().getAlias());

        return new IsFollowerResponse(request.getAuthToken(), followingDTO != null);
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }

        // 1. get the user alias by the authtoken
        AuthDTO authDTO = getAuthDAO().get(request.getAuthToken().getToken());

        String follower = authDTO.getAlias();
        String followee = request.getTargetUser().getAlias();

        // 2. Add into follows table the follower (the alias we just got) and the followee, the user in the request
        FollowingDTO followingDTO = new FollowingDTO();
        followingDTO.setFollowee_handle(followee);
        followingDTO.setFollower_handle(follower);

        getFollowingDAO().insert(followingDTO);

        // 3. Increment followee's followers count in users table
        UserDTO userDTO = getUserDAO().get(followee);
        int followers = userDTO.getFollowersCount();

        userDTO.setFollowersCount(followers + 1);
        getUserDAO().update(userDTO);

        // 4. Increment follower's following count in users table
        userDTO = getUserDAO().get(follower);
        int following = userDTO.getFollowingCount();

        userDTO.setFollowingCount(following + 1);
        getUserDAO().update(userDTO);

        // 5. Return new FollowResponse
        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        // 1. get the user alias by the authtoken
        AuthDTO authDTO = getAuthDAO().get(request.getAuthToken().getToken());

        String follower = authDTO.getAlias();
        String followee = request.getTargetUser().getAlias();

        // 2. remove following from following table
        getFollowingDAO().delete(follower, followee);

        // 3. Decrement the follower's following count in users table
        UserDTO userDTO = getUserDAO().get(follower);
        int following = userDTO.getFollowingCount();

        userDTO.setFollowingCount(following - 1);
        getUserDAO().update(userDTO);

        // 4. Decrement the followee's followers count in users table
        userDTO = getUserDAO().get(followee);
        int followers = userDTO.getFollowersCount();

        userDTO.setFollowersCount(followers - 1);
        getUserDAO().update(userDTO);

        return new UnfollowResponse();
    }

    public void sendToUpdateFeedQueue(String message) {
        // 1. Convert message String into a Status
        Gson gson = new GsonBuilder().create();
        Status status = gson.fromJson(message, Status.class);

        // 2. Get followers from table by pages (NOTE: it will be faster to read 25 items from the table then convert them to a list that read them from table one by one)
        String lastFollowerAlias = null;
        boolean hasMorePages = true;

        while (hasMorePages) {
            // 1. Read page of followers and create list of 25 of them
            DataPage<FollowersDTO> dataPage = getFollowingDAO().getPageOfFollowers(status.getUser().getAlias(), lastFollowerAlias, 25);
            List<String> aliases = new ArrayList<>();

            for (FollowersDTO dto: dataPage.getValues()) {
                lastFollowerAlias = dto.getFollower_handle();
                aliases.add(lastFollowerAlias);
            }

            hasMorePages = dataPage.isHasMorePages();

            // 2. Create updateFeedBatchPOJO with Status and all 25 aliases we just got
            UpdateFeedBatchPOJO pojo = new UpdateFeedBatchPOJO(status, aliases);

            // 3. Serialize pojo and send it to SQS Update Feed Queue
            String jsonString = gson.toJson(pojo);

            // TODO: need to change this url so it sends to the second queue instead
            insertMessageIntoSQS(jsonString, UPDATE_FEED_QUEUE);
        }
    }
}
