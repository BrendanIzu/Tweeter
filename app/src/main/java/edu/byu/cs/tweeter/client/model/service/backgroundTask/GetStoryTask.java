package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends BackgroundTask {
    private static final String LOG_TAG = "GetStoryTask";

    public static final String STATUSES_KEY = "statuses";
    public static final String MORE_PAGES_KEY = "hasMorePages";

    protected AuthToken authToken;
    protected User targetUser;
    protected Status lastStatus;
    private List<Status> statuses;
    protected int limit;
    private boolean hasMorePages;

    public GetStoryTask(StatusService statusService, AuthToken authToken, User targetUser, int limit, Status lastStatus, Handler messageHandler) {
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

            StoryRequest request = new StoryRequest(authToken, targetUserAlias, limit, lastStatus);
            StoryResponse response = getServerFacade().getStory(request, StatusService.GET_STORY);

            if (response.isSuccess()) {
                this.statuses = response.getStatuses();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get story", ex);
            sendExceptionMessage(ex);
        }
    }
}