package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.client.view.ViewInterface;

public abstract class SimpleObserver<T extends ViewInterface> implements ObserverInterface {
    private T view;
    protected abstract String getError();
    protected abstract void failureLogic(String message);
    protected abstract void exceptionLogic(Exception ex);

    public SimpleObserver(T view) {
        this.view = view;
    }

    @Override
    public void handleFailure(String message) {
        view.displayMessage(getError());
        failureLogic(message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayMessage(getError() + " because of exception: " + ex.getMessage());
        exceptionLogic(ex);
    }
}
