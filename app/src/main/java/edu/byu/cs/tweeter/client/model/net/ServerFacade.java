package edu.byu.cs.tweeter.client.model.net;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

public class ServerFacade {
    private static final String SERVER_URL = "https://lys5ag6qw4.execute-api.us-west-2.amazonaws.com/tweeterAPIstage1";

    private final ClientCommunicator clientCommunicator = new ClientCommunicator(SERVER_URL);

    public LoginResponse login(LoginRequest request, String urlPath) throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(urlPath, request, null, LoginResponse.class);
    }

    public FollowersResponse getFollowers(FollowersRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, FollowersResponse.class);
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, FollowersCountResponse.class);
    }

    public FollowingResponse getFollowing(FollowingRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, FollowingResponse.class);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, FollowingCountResponse.class);
    }

    public FeedResponse getFeed(FeedRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, FeedResponse.class);
    }

    public StoryResponse getStory(StoryRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, StoryResponse.class);
    }

    public UserResponse getUser(UserRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, UserResponse.class);
    }

    public FollowResponse follow(FollowRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, FollowResponse.class);
    }

    public UnfollowResponse unfollow(UnfollowRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, UnfollowResponse.class);
    }

    public LogoutResponse logout(LogoutRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, LogoutResponse.class);
    }

    public RegisterResponse register(RegisterRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, RegisterResponse.class);
    }

    public PostStatusResponse postStatus(PostStatusRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, PostStatusResponse.class);
    }

    public IsFollowerResponse isfollowing(IsFollowerRequest request, String url)
            throws IOException, TweeterRemoteException {
        return clientCommunicator.doPost(url, request, null, IsFollowerResponse.class);
    }
}
