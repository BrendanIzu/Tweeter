package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserverInterface;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserHandler extends SimpleUserHandler {
    public GetUserHandler(UserService.GetUserObserver observer) {
        super(observer);
    }

    @Override
    public User loadUser(Bundle data) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        return user;
    }
}
