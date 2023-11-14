package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {
    private static final String LOG_TAG = "RegisterPresenter";

    private final View view;

    public interface View {
        void registerSuccessful(User user, AuthToken authToken);
        void registerUnsuccessful(String message);
        void setErrorText(String errorText);
        void displayMessage(String message);
    }

    public RegisterPresenter(View view) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(view == null) {
            throw new NullPointerException();
        }
        this.view = view;
    }

    public void validateHelper(String message) {
        view.setErrorText(null);
        view.displayMessage(message);
    }

    public void validateRegistration(ImageView imageToUpload, EditText firstName, EditText lastName, EditText alias, EditText password) {
        try {
            UserService userService = new UserService();
            userService.validateRegistration(imageToUpload, firstName, lastName, alias, password);
            validateHelper("Registering...");
            userService.register(imageToUpload, firstName, lastName, alias, password, new RegisterObserver());
        } catch (Exception e) {
            view.setErrorText(e.getMessage());
        }
    }

    public class RegisterObserver implements UserService.RegisterObserver {
        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            Cache.getInstance().setCurrUser(user);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.registerSuccessful(user, authToken);
        }

        @Override
        public void handleFailure(String message) {
            String errorMessage = "Failed to register: " + message;
            Log.e(LOG_TAG, errorMessage);
            view.registerUnsuccessful(errorMessage);
        }

        @Override
        public void handleException(Exception ex) {
            String errorMessage = "Failed to register because of exception: " + ex.getMessage();
            Log.e(LOG_TAG, errorMessage, ex);
            view.registerUnsuccessful(errorMessage);
        }
    }
}
