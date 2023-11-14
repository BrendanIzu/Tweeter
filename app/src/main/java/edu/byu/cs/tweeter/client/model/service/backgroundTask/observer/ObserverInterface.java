package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface ObserverInterface {
    void handleFailure(String message);
    void handleException(Exception ex);
}
