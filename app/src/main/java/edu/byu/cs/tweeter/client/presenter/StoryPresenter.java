package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements StatusService.GetItemsObserver<Status>, UserService.GetUserObserver {
    private static final String LOG_TAG = "StoryPresenter";
    public static final int PAGE_SIZE = 10;

    private final StoryPresenter.View view;
    private final User user;
    private final AuthToken authToken;

    private Status lastStatus;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    private StatusService statusService;
    private UserService userService;

    public void getUserFromHandle(String alias) {
        new UserService().getUserFromHandle(alias, this);
    }

    public interface View {
        void setLoading(boolean value);
        void addItems(List<Status> newStatuses);
        void displayMessage(String message);
        void getUser(User user);
    }

    public StoryPresenter(StoryPresenter.View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    private void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    private void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);

            getStory(authToken, user, PAGE_SIZE, lastStatus);
        }
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getStatusService().getStory(authToken, targetUser, limit, lastStatus, this);
    }

    public void getUser(TextView userAlias) {
        getUserService().getUser(userAlias, this);
    }

    public StatusService getStatusService() {
        if(statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    public UserService getUserService() {
        if(userService == null) {
            userService = new UserService();
        }
        return userService;
    }


    @Override
    public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
        setLastStatus((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(statuses);
        setLoading(false);
    }

    @Override
    public void handleSuccess(User user) {
        view.getUser(user);
        //view.displayMessage("Getting user's profile...");
    }

    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to retrieve story: " + message;
        Log.e(LOG_TAG, errorMessage);

        view.setLoading(false);
        //view.displayMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleException(Exception exception) {
        String errorMessage = "Failed to retrieve story because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        //view.displayMessage(errorMessage);
        setLoading(false);
    }
}
