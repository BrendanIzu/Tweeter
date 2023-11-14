package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface GetCountObserverInterface extends ObserverInterface {
    void handleSuccess(int count, String value);
}
