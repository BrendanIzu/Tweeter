package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.client.view.ViewInterface;

public abstract class SimpleErrorObserver<T extends ViewInterface> extends SimpleObserver<T> {
    protected abstract void failureLogic();
    protected abstract void exceptionLogic();

    public SimpleErrorObserver(T view) {
        super(view);
    }

    @Override
    public void failureLogic(String message) {
        failureLogic();
    }

    @Override
    public void exceptionLogic(Exception ex) {
        exceptionLogic();
    }
}
