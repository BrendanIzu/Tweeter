package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.NotificationObserverInterface;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {
    public static final String GET_FEED = "/getfeed";
    public static final String GET_STORY = "/getstory";
    public static final String POST_STATUS = "/poststatus";

    public interface PostStatusObserver {
        void handleSuccess();
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface GetItemsObserver<T> {
        void handleSuccess(List<T> items, boolean hasMoreItems);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetItemsObserver<Status> observer) {
        GetFeedTask feedTask = new GetFeedTask(this, Cache.getInstance().getCurrUserAuthToken(), targetUser, limit, lastStatus, new GetFeedHandler(observer));
        BackgroundTaskUtils.runTask(feedTask);
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetItemsObserver<Status> observer) {
        GetStoryTask storyTask = new GetStoryTask(this, Cache.getInstance().getCurrUserAuthToken(), targetUser, limit, lastStatus, new GetStoryHandler(observer));
        BackgroundTaskUtils.runTask(storyTask);
    }

    public void onStatusPosted(Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusHandler(observer));
        BackgroundTaskUtils.runTask(statusTask);
    }

    public GetFeedTask newGetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus, GetItemsObserver<Status> observer) {
        return new GetFeedTask(this, authToken, targetUser, limit, lastStatus, new GetFeedHandler(observer));
    }
}
