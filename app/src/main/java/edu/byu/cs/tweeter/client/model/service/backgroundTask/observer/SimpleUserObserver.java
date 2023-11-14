package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.client.view.ViewInterface;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class SimpleUserObserver<T extends ViewInterface> extends SimpleNotificationsObserver<T> implements UserObserverInterface {
    protected abstract void successLogic(User user);

    public SimpleUserObserver(T View) {
        super(View);
    }

    @Override
    protected String getError() {
        return "Failed to get user's profile";
    }

    @Override
    public void handleSuccess(User user) {
        successLogic(user);
    }
}
