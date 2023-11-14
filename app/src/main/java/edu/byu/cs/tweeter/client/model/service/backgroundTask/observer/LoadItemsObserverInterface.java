package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import java.util.List;

public interface LoadItemsObserverInterface<T> extends ObserverInterface {
    void handleSuccess(List<T> items, boolean hasMorePages);
}
