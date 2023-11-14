package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.client.view.ViewInterface;

public abstract class SimpleNotificationsObserver<T extends ViewInterface> extends SimpleObserver<T> {
    public SimpleNotificationsObserver(T view) {
        super(view);
    }

    @Override
    public void failureLogic(String message) {}

    @Override
    public void exceptionLogic(Exception ex) {}
}
