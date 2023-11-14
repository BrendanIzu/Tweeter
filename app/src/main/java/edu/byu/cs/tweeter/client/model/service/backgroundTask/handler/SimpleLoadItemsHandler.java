package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.LoadItemsObserverInterface;

public abstract class SimpleLoadItemsHandler<T> extends BackgroundTaskHandler<LoadItemsObserverInterface> {
    private List<T> items;
    private boolean hasMorePages;

    protected abstract void loadData(Bundle data);

    public SimpleLoadItemsHandler(LoadItemsObserverInterface observer) {
        super(observer);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    @Override
    protected void handleSuccess(Bundle data, LoadItemsObserverInterface observer) {
        // feed and story use different methods to load things
        loadData(data);
        observer.handleSuccess(items, hasMorePages);
    }
}
