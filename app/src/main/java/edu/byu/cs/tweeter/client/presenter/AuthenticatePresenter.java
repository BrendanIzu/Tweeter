package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;
import android.widget.ImageView;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleUserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserverInterface;
import edu.byu.cs.tweeter.client.view.ViewInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends Presenter<AuthenticatePresenter.View> {
    private UserService userService;

    public interface View extends ViewInterface {
        void cancel();
        void setToast(String s);
        void setErrorText(String errorText);
        void login(User authenticatedUser);
    }

    public AuthenticatePresenter(View view) {
        super(view);
        this.userService = new UserService();
    }

    public void validateHelper(String message) {
        view.setErrorText(null);
        view.setToast(message);
        view.displayMessage(message);
    }

    public void validateRegistration(ImageView imageToUpload, EditText firstName, EditText lastName, EditText alias, EditText password) {
        try {
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
            view.cancel();
            view.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
            view.login(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to register: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to register" + " because of exception: " + ex.getMessage());
        }
    }
}
