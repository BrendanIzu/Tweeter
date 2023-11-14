package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.NotificationObserverInterface;

public class SimpleNotificationHandler extends BackgroundTaskHandler<NotificationObserverInterface> {
    public SimpleNotificationHandler(NotificationObserverInterface observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, NotificationObserverInterface observer) {
        observer.handleSuccess();
    }
}
