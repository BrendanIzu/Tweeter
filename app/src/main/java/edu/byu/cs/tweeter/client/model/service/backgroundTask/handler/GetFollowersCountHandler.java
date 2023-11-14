package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;

public class GetFollowersCountHandler extends Handler {
    private final FollowService.GetFollowersCountObserver observer;

    public GetFollowersCountHandler(FollowService.GetFollowersCountObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(GetFollowersCountTask.SUCCESS_KEY);
        if (success) {
            int count = bundle.getInt(GetFollowersCountTask.FOLLOWERS_COUNT_KEY);
            observer.handleSuccess(R.string.followerCount, String.valueOf(count));
        } else if (bundle.containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(GetFollowersCountTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
