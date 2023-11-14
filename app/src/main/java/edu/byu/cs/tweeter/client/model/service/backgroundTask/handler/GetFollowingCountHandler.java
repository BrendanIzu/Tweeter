package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;

public class GetFollowingCountHandler extends Handler {
    private final FollowService.GetFollowingCountObserver observer;

    public GetFollowingCountHandler(FollowService.GetFollowingCountObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(GetFollowingCountTask.SUCCESS_KEY);
        if (success) {
            int count = bundle.getInt(GetFollowingCountTask.FOLLOWING_COUNT_KEY);
            observer.handleSuccess(R.string.followeeCount, String.valueOf(count));
        } else if (bundle.containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(GetFollowingCountTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
