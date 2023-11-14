package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends Handler {
    private final UserService.RegisterObserver observer;

    public RegisterHandler(UserService.RegisterObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(RegisterTask.SUCCESS_KEY);
        if (success) {
            User user = (User) bundle.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            observer.handleSuccess(user, authToken);
        } else if (bundle.containsKey(RegisterTask.MESSAGE_KEY)) {
            System.out.println("THIS SHOULD NOT HAVE COME HERE");
            String errorMessage = bundle.getString(RegisterTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(RegisterTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(RegisterTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
