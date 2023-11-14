package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter {
    private static final String LOG_TAG = "FollowingPresenter";
    public static final int PAGE_SIZE = 10;

    private final View view;
    private final User user;
    private final AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    private FollowService followService;
    private UserService userService;

    public interface View {
        void getUser(User user);
        void setLoading(boolean value);
        void addItems(List<User> newUsers);
        void displayMessage(String message);
    }

    public FollowingPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    public FollowService getFollowingService() {
        if(followService == null) {
            followService = new FollowService();
        }
        return followService;
    }

    public UserService getUserService() {
        if(userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    public User getLastFollowee() {
        return lastFollowee;
    }

    private void setLastFollowee(User lastFollowee) {
        this.lastFollowee = lastFollowee;
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

            getFollowing(authToken, user, PAGE_SIZE, lastFollowee);
        }
    }

    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowingService().getFollowees(authToken, targetUser, limit, lastFollowee, new GetItemsObserver());
    }

    public void getUser(TextView userAlias) {
        getUserService().getUser(userAlias, new GetUserObserver());
    }

    public class GetItemsObserver implements FollowService.GetItemsObserver {
        @Override
        public void handleSuccess(List<User> items, boolean hasMoreItems) {
            setLastFollowee((items.size() > 0) ? items.get(items.size() - 1) : null);
            setHasMorePages(hasMoreItems);

            view.setLoading(false);
            view.addItems(items);
            setLoading(false);
        }

        @Override
        public void handleFailure(String message) {
            String errorMessage = "Failed to retrieve items: " + message;
            Log.e(LOG_TAG, errorMessage);

            view.setLoading(false);
            //view.displayMessage(errorMessage);
            setLoading(false);
        }

        @Override
        public void handleException(Exception ex) {
            String errorMessage = "Failed to retrieve items because of exception: " + ex.getMessage();
            Log.e(LOG_TAG, errorMessage, ex);

            view.setLoading(false);
            //view.displayMessage(errorMessage);
            setLoading(false);
        }
    }

    public class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void handleSuccess(User user) {
            view.getUser(user);
            //view.displayMessage("Getting user's profile...");
        }

        @Override
        public void handleFailure(String message) {
            //view.displayMessage("Failed to get user: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            //view.displayMessage("Failed to get user" + " because of exception: " + ex.getMessage());
        }
    }
}