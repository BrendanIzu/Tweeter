package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

public class LoginPresenter implements UserService.LoginObserver {
    private static final String LOG_TAG = "LoginPresenter";

    private final View view;

    public interface View {
        void loginSuccessful(User user, AuthToken authToken);
        void loginUnsuccessful(String message);
    }

    public LoginPresenter(View view) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(view == null) {
            throw new NullPointerException();
        }
        this.view = view;
    }

    public void validateLogin(String username, String password) {
        UserService userService = new UserService();
        userService.login(username, password, this);
    }

    @Override
    public void handleSuccess(User user, AuthToken authToken) {
        // Cache user session information
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        view.loginSuccessful(user, authToken);
    }

    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to login: " + message;
        Log.e(LOG_TAG, errorMessage);
        view.loginUnsuccessful(errorMessage);
    }

    @Override
    public void handleException(Exception ex) {
        String errorMessage = "Failed to login because of exception: " + ex.getMessage();
        Log.e(LOG_TAG, errorMessage, ex);
        view.loginUnsuccessful(errorMessage);
    }
}
