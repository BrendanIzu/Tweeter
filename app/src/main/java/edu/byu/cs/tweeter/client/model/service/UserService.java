package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.NotificationObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.ObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserverInterface;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {
    public static final String LOGIN = "/login";
    public static final String GET_USER = "/getuser";
    public static final String LOGOUT = "/logout";
    public static final String REGISTER = "/register";

    public interface LogoutObserver {
        void handleSuccess();
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public interface LoginObserver {
        void handleSuccess(User user, AuthToken authToken);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public interface GetUserObserver extends UserObserverInterface {
        void handleSuccess(User user);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public interface RegisterObserver {
        void handleSuccess(User user, AuthToken authToken);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public UserService() {}

    public void login(String username, String password, LoginObserver observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        BackgroundTaskUtils.runTask(loginTask);
    }

    LoginTask getLoginTask(String username, String password, LoginObserver observer) {
        return new LoginTask(this, username, password, new LoginTaskHandler(observer));
    }

    public void getUser(TextView userAlias, GetUserObserver observer) {
        BackgroundTaskUtils.runTask(new GetUserTask(this, Cache.getInstance().getCurrUserAuthToken(), userAlias.getText().toString(), new GetUserHandler(observer)));
    }

    public void getUserFromHandle(String alias, GetUserObserver observer) {
        BackgroundTaskUtils.runTask(new GetUserTask(this, Cache.getInstance().getCurrUserAuthToken(), alias, new GetUserHandler(observer)));
    }

    public void login(EditText alias, EditText password, UserService.LoginObserver observer) {
        BackgroundTaskUtils.runTask(new LoginTask(this, alias.getText().toString(), password.getText().toString(), new LoginTaskHandler(observer)));
    }

    public void logout(LogoutObserver observer) {
        BackgroundTaskUtils.runTask(new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer)));
    }

    public void register(ImageView imageToUpload, EditText firstName, EditText lastName, EditText alias, EditText password, UserService.RegisterObserver observer) {
        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        System.out.println(imageBytesBase64);

        BackgroundTaskUtils.runTask(new RegisterTask(firstName.getText().toString(), lastName.getText().toString(), alias.getText().toString(), password.getText().toString(), imageBytesBase64, new RegisterHandler(observer)));
    }

    public void validateLogin(EditText alias, EditText password) {
        if (alias.getText().length() > 0 && alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public void validateRegistration(ImageView imageToUpload, EditText firstName, EditText lastName, EditText alias, EditText password) {
        if (firstName.getText().length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.getText().length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.getText().length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}
