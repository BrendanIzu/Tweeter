package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import okhttp3.Response;

public abstract class PagedTask<T, U extends PagedResponse> extends AuthenticatedTask {
    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";
    private static final String LOG_TAG = "GetFollowingTask";

    protected AuthToken authToken;

    /**
     * Maximum number of followed users to return (i.e., page size).
     */
    private int limit;
    /**
     * The last person being followed returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private T lastItem;
    /**
     * The user whose following is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    protected User targetUser;

    private List<T> items;
    protected boolean hasMorePages;

    private ServerFacade serverFacade;

    protected abstract List<T> getItems(U response);
    protected abstract U generateResponse(AuthToken authToken, String targetUserAlias, int limit) throws IOException, TweeterRemoteException;

    public PagedTask(Handler messageHandler, AuthToken authToken, User targetUser, int limit, T lastItem) {
        super(messageHandler, authToken);

        this.limit = limit;
        this.lastItem = lastItem;
        this.targetUser = targetUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public int getLimit() {
        return limit;
    }

    public T getLastItem() {
        return lastItem;
    }

//    @Override
//    protected void loadSuccessBundle(Bundle msgBundle) {
//        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
//        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
//    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            U response = generateResponse(authToken, targetUserAlias, limit);

            if (response.isSuccess()) {
                this.items = getItems(response);
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get items", ex);
            sendExceptionMessage(ex);
        }
    }
}
