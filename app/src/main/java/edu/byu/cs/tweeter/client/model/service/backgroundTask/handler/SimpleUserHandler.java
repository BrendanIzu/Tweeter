package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.ObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserverInterface;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class SimpleUserHandler extends BackgroundTaskHandler<UserObserverInterface> {
    public SimpleUserHandler(UserObserverInterface observer) {
        super(observer);
    }

    public abstract User loadUser(Bundle data);

    @Override
    protected void handleSuccess(Bundle data, UserObserverInterface observer) {
        User user = loadUser(data);
        observer.handleSuccess(user);
    }
}
