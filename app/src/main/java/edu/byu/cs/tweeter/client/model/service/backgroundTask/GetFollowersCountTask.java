package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;

public class GetFollowersCountTask extends BackgroundTask {
    private static final String LOG_TAG = "GetFollowersCountTask";

    public static final String FOLLOWERS_COUNT_KEY = "count";

    protected AuthToken authToken;
    protected User targetUser;

    protected int count;

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler);

        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    /** remember this is called by the background task (template method) it tells you what to do with the info we just got for the view) **/
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(FOLLOWERS_COUNT_KEY, (Serializable) this.count);
    }

    @Override
    protected void runTask() {
        try {
            /**  we pass stuff into the request so we can access it from the DAO **/
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            FollowersCountRequest request = new FollowersCountRequest(authToken, targetUserAlias);
            FollowersCountResponse response = getServerFacade().getFollowersCount(request, FollowService.GET_FOLLOWERS_COUNT);

            if (response.isSuccess()) {
                this.count = response.getCount();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get follower count", ex);
            sendExceptionMessage(ex);
        }
    }
}
