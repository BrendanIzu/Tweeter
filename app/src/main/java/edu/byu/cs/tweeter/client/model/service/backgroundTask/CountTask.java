package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class CountTask extends AuthenticatedTask {
    public static final String COUNT_KEY = "count";
    public static final int COUNT = 20;

    protected User targetUser;

    public CountTask(Handler messageHandler, AuthToken authToken, User targetUser) {
        super(messageHandler, authToken);
        this.targetUser = targetUser;
    }

    public int getCount() {
        return COUNT;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        // TODO: not sure about getting 20 count implementation
        msgBundle.putInt(COUNT_KEY, getCount());
    }

    @Override
    protected void runTask() {

    }
}
