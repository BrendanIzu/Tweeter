package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface FollowStatusObserverInterface extends ObserverInterface {
    void handleSuccess(boolean isFollower);
}
