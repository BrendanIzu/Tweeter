package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.FollowStatusObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetCountObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.NotificationObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    /** urls used by the task to determine what api call we are making **/
    public static final String GET_FOLLOWERS = "/getfollowers";
    public static final String GET_FOLLOWING = "/getfollowing";
    public static final String GET_FOLLOWERS_COUNT = "/getfollowerscount";
    public static final String GET_FOLLOWING_COUNT = "/getfollowingcount";
    public static final String FOLLOW = "/follow";
    public static final String UNFOLLOW = "/unfollow";
    public static String IS_FOLLOWER = "/isfollower";

    public interface UpdateFollowStatusObserver extends FollowStatusObserverInterface {}

    public interface FollowObserver {
        void handleSuccess();
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface UnfollowObserver {

        void handleSuccess();
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface GetFollowersCountObserver {
        void handleSuccess(int count, String value);
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface GetFollowingCountObserver {
        void handleSuccess(int count, String value);
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface GetItemsObserver {
        void handleSuccess(List<User> items, boolean hasMoreItems);
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface IsfollowerObserver {
        void handleSuccess(Boolean value);
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public FollowService() {}

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetItemsObserver observer) {
        GetFollowersTask task = newGetFollowersTask(authToken, targetUser, limit, lastFollower, observer);
        BackgroundTaskUtils.runTask(task);
    }

    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetItemsObserver observer) {
        GetFollowingTask task = newGetFollowingTask(authToken, targetUser, limit, lastFollowee, observer);
        BackgroundTaskUtils.runTask(task);
    }

    public void follow(User targetUser, FollowObserver observer) {        // TODO: MAKE GET METHOD
        BackgroundTaskUtils.runTask(new FollowTask(Cache.getInstance().getCurrUserAuthToken(), targetUser, new FollowHandler(observer)));
    }

    public void isFollower(User targetUser, IsfollowerObserver observer) {        // TODO: MAKE GET METHOD
        BackgroundTaskUtils.runTask(new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), targetUser, new IsFollowerHandler(observer)));
    }

    public void unfollow(User selectedUser, UnfollowObserver observer) {        // TODO: MAKE GET METHOD
        BackgroundTaskUtils.runTask(new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowHandler(observer)));
    }

    public void updateSelectedUserFollowingAndFollowers(User targetUser, GetFollowersCountObserver followersObserver, GetFollowingCountObserver followingObserver) { // TODO: make be a parameter and passed in by something else maybe
        AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();

        GetFollowersCountTask followersCountTask = newGetFollowersCountTask(authToken, targetUser, followersObserver);
        GetFollowingCountTask followingCountTask = newGetFollowingCountTask(authToken, targetUser, followingObserver);

        BackgroundTaskUtils.runTask(followersCountTask);
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    /**  GET METHODS FOR TESTING **/
    public GetFollowersTask newGetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower, GetItemsObserver observer) {
        return new GetFollowersTask(this, authToken, targetUser, limit, lastFollower, new GetFollowersTaskHandler(observer));
    }

    public GetFollowingTask newGetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetItemsObserver observer) {
        return new GetFollowingTask(this, authToken, targetUser, limit, lastFollowee, new GetFollowingTaskHandler(observer));
    }

    public GetFollowersCountTask newGetFollowersCountTask(AuthToken authtoken, User targetUser, GetFollowersCountObserver observer) {
        return new GetFollowersCountTask(authtoken, targetUser, new GetFollowersCountHandler(observer));
    }

    public GetFollowingCountTask newGetFollowingCountTask(AuthToken authtoken, User targetUser, GetFollowingCountObserver observer) {
        return new GetFollowingCountTask(authtoken, targetUser, new GetFollowingCountHandler(observer));
    }
}
