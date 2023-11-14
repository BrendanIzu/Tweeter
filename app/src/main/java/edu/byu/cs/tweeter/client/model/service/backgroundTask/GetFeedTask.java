package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public class GetFeedTask extends BackgroundTask {
    private static final String LOG_TAG = "GetFeedTask";

    public static final String STATUSES_KEY = "statuses";
    public static final String MORE_PAGES_KEY = "hasMorePages";

    private ServerFacade serverFacade;

    protected AuthToken authToken;
    protected User targetUser;
    protected Status lastStatus;
    private List<Status> statuses;
    protected int limit;
    private boolean hasMorePages;

    public GetFeedTask(StatusService statusService, AuthToken authToken, User targetUser, int limit, Status lastStatus, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }

    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(STATUSES_KEY, (Serializable) this.statuses);
        msgBundle.putBoolean(MORE_PAGES_KEY, this.hasMorePages);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            FeedRequest request = new FeedRequest(authToken, targetUserAlias, limit, lastStatus);
            FeedResponse response = getServerFacade().getFeed(request, StatusService.GET_FEED);

            if (response.isSuccess()) {
                this.statuses = response.getStatuses();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get feed", ex);
            sendExceptionMessage(ex);
        }
    }
}
