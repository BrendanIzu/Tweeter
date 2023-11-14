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
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

public class GetFollowingCountTask extends BackgroundTask {
    private static final String LOG_TAG = "GetFollowingCountTask";

    public static final String FOLLOWING_COUNT_KEY = "count";

    protected AuthToken authToken;
    protected User targetUser;

    protected int count;

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler);

        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    /** remember this is called by the background task (template method) it tells you what to do with the info we just got for the view) **/
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(FOLLOWING_COUNT_KEY, (Serializable) this.count);
    }

    @Override
    protected void runTask() {
        try {

            /**  we pass stuff into the request so we can access it from the DAO **/
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            FollowingCountRequest request = new FollowingCountRequest(authToken, targetUserAlias);
            FollowingCountResponse response = getServerFacade().getFollowingCount(request, FollowService.GET_FOLLOWING_COUNT);

            if (response.isSuccess()) {
                this.count = response.getCount();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }

        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get following count", ex);
            sendExceptionMessage(ex);
        }
    }
}
