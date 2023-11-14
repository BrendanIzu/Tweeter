package edu.byu.cs.tweeter.client.presenter;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationsObserver;
import edu.byu.cs.tweeter.client.view.ViewInterface;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter {
    private static final String LOG_TAG = "MainActivity";
    public static final int PAGE_SIZE = 10;

    private MainActivityPresenter.View view;

    private FollowService followService;
    private StatusService statusService;
    private UserService userService;

    public interface View extends ViewInterface {
        void cancel();
        void cancelPost();
        void enableFollowButton(boolean value);
        void logout();
        boolean onOptionsItemSelected(MenuItem item);
        void setFollowButtonProperties(int value, int backgroundColor, int textColor);
        void setFollowButtonVisibility(int value);
        void setFolloweeCount(int count, String value);
        void setFollowersCount(int count, String value);
        void setLogoutToast(String message);
        void setPostingToast(String message);
        void updateFollowButton(boolean value);
        void updateSelectedUserFollowingAndFollowers();
    }

    public MainActivityPresenter(MainActivityPresenter.View view) {
        this.view = view;
    }

    public void setView(MainActivityPresenter.View view) {
        this.view = view;
    }

    public void changeFollowStatus(Button followButton, User targetUser) {
        view.enableFollowButton(true);

        if (followButton.getText().toString().equals("Following")) {
            getFollowService().unfollow(targetUser, new UnfollowObserver());
            view.displayMessage("Removing " + targetUser.getName() + "...");
        } else {
            getFollowService().follow(targetUser, new FollowObserver());
            view.enableFollowButton(true);
            view.displayMessage("Adding " + targetUser.getName() + "...");
        }

        view.enableFollowButton(true);
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item, boolean value) {
        if (item.getItemId() == R.id.logoutMenu) {
            view.setLogoutToast("Logging Out...");
            view.displayMessage("Logging Out...");
            getUserService().logout(new LogoutObserver());

            return true;
        } else {
            return value;
        }
    }

    // Factory method of unit tests
    public Status getNewStatus(String post) {
        return new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
    }

    public void onStatusPosted(String post) {
        view.setPostingToast("Posting Status...");
        view.displayMessage("Successfully Posted!");

        Status newStatus = getNewStatus(post);
        getStatusService().onStatusPosted(newStatus, new PostStatusObserver());
    }

    public String getFormattedDateTime() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(Objects.requireNonNull(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8))));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public void setFollowing(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowButtonVisibility(android.view.View.GONE);
        } else {
            view.setFollowButtonVisibility(android.view.View.VISIBLE);
            getFollowService().isFollower(selectedUser, new FollowStatusObserver());
        }
        getFollowService().isFollower(selectedUser, new FollowStatusObserver());
    }

    public void updateFollowButton(boolean removed) {
        if (removed) {
            view.setFollowButtonProperties(R.string.follow, R.color.colorAccent, R.color.white);
        } else {
            view.setFollowButtonProperties(R.string.following, R.color.white, R.color.lightGray);
        }
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        getFollowService().updateSelectedUserFollowingAndFollowers(selectedUser, new GetFollowersCountObserver(), new GetFollowingCountObserver());
    }

    public FollowService getFollowService() {
        if (followService == null) {
            followService = new FollowService();
        }
        return followService;
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

    public class FollowObserver implements FollowService.FollowObserver {

        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(false);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to follow" + " because of exception: " + ex.getMessage());
        }
    }

    public class FollowStatusObserver implements FollowService.IsfollowerObserver {
        @Override
        public void handleSuccess(Boolean isFollower) {
            if (isFollower) {
                view.setFollowButtonProperties(R.string.following, R.color.white, R.color.lightGray);
            } else {
                view.setFollowButtonProperties(R.string.follow, R.color.colorAccent, R.color.white);
            }
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("failed to determine following relationship: " + message);

        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("failed to determine following relationship due to exception: " + ex.getMessage());

        }
    }

    public class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver {
        @Override
        public void handleSuccess(int count, String value) {
            view.setFollowersCount(count, value);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("failed to get followers count due to exception: " + ex.getMessage());
        }
    }

    public class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver {
        @Override
        public void handleSuccess(int count, String value) {
            view.setFolloweeCount(count, value);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("failed to get following count due to exception: " + ex.getMessage());
        }
    }

    public class LogoutObserver implements UserService.LogoutObserver {
        @Override
        public void handleSuccess() {
            view.cancel();
            view.logout();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to logout" + " because of exception: " + ex.getMessage());
        }
    }

    public class PostStatusObserver implements StatusService.PostStatusObserver {
        @Override
        public void handleSuccess() {
            view.cancelPost();
            view.displayMessage("Successfully Posted!");
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to post status" + " because of exception: " + ex.getMessage());
        }
    }

    public class UnfollowObserver implements FollowService.UnfollowObserver {

        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(true);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to unfollow status: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to unfollow" + " because of exception: " + ex.getMessage());
        }
    }
}
