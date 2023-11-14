package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;

public class GetFollowersTask extends BackgroundTask {
    private static final String LOG_TAG = "GetFollowersTask";

    public static final String FOLLOWERS_KEY = "followers";
    public static final String MORE_PAGES_KEY = "morePages";

    protected AuthToken authToken;
    protected User targetUser;
    protected User lastFollower;
    private List<User> followers;
    protected int limit;
    private boolean hasMorePages;

    public GetFollowersTask(FollowService followService, AuthToken authToken, User targetUser, int limit, User lastFollower, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastFollower = lastFollower;
    }

    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(FOLLOWERS_KEY, (Serializable) this.followers);
        msgBundle.putBoolean(MORE_PAGES_KEY, this.hasMorePages);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastFollowerAlias = lastFollower == null ? null : lastFollower.getAlias();

            FollowersRequest request = new FollowersRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
            FollowersResponse response = getServerFacade().getFollowers(request, FollowService.GET_FOLLOWERS);

            if (response.isSuccess()) {
                this.followers = response.getFollowers();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followers", ex);
            sendExceptionMessage(ex);
        }
    }
}
