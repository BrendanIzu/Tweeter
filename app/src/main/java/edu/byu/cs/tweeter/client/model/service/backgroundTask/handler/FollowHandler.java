package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;

public class FollowHandler extends Handler {
    private final FollowService.FollowObserver observer;

    public FollowHandler(FollowService.FollowObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(FollowTask.SUCCESS_KEY);
        if (success) {
            observer.handleSuccess();
        } else if (bundle.containsKey(FollowTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(FollowTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(FollowTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(FollowTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
