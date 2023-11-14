package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;

public class UnfollowHandler extends Handler {
    private final FollowService.UnfollowObserver observer;

    public UnfollowHandler(FollowService.UnfollowObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(UnfollowTask.SUCCESS_KEY);
        if (success) {
            observer.handleSuccess();
        } else if (bundle.containsKey(UnfollowTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(UnfollowTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(UnfollowTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(UnfollowTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
