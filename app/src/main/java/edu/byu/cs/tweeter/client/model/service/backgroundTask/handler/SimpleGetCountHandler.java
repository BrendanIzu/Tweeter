package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetCountObserverInterface;

public abstract class SimpleGetCountHandler extends BackgroundTaskHandler<GetCountObserverInterface> {
    private int count;
    private String value;

    protected abstract void loadData(Bundle data);

    public SimpleGetCountHandler(GetCountObserverInterface observer) {
        super(observer);
    }

    protected void setCount(int count) {
        this.count = count;
    }

    protected void setValue(String value) {
        this.value = value;
    }


    @Override
    protected void handleSuccess(Bundle data, GetCountObserverInterface observer) {
        loadData(data);
        observer.handleSuccess(count, value);
    }
}
