package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryHandler extends Handler {
    private final StatusService.GetItemsObserver<Status> observer;

    public GetStoryHandler(StatusService.GetItemsObserver<Status> observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle bundle = message.getData();
        boolean success = bundle.getBoolean(GetStoryTask.SUCCESS_KEY);
        if (success) {
            List<Status> statuses = (List<Status>) bundle.getSerializable(GetStoryTask.STATUSES_KEY);
            boolean hasMorePages = bundle.getBoolean(GetStoryTask.MORE_PAGES_KEY);
            observer.handleSuccess(statuses, hasMorePages);
        } else if (bundle.containsKey(GetStoryTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(GetStoryTask.MESSAGE_KEY);
            observer.handleFailure(errorMessage);
        } else if (bundle.containsKey(GetStoryTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(GetStoryTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
