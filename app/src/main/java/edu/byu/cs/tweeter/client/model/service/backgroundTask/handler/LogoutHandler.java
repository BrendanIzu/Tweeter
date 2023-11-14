package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;


public class LogoutHandler extends Handler {
    private final UserService.LogoutObserver observer;

    public LogoutHandler(UserService.LogoutObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(LogoutTask.SUCCESS_KEY);
        if (success) {
            observer.handleSuccess();
        } else if (bundle.containsKey(LogoutTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(LogoutTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(LogoutTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(LogoutTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
