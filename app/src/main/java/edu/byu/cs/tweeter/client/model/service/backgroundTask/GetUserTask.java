package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

public class GetUserTask extends BackgroundTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private String alias;
    protected User user;
    protected AuthToken authToken;

    public GetUserTask(UserService userService, AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler);

        this.alias = alias;
    }

    @Override
    protected void runTask() {
        try {
            UserRequest request = new UserRequest(alias);
            UserResponse response = getServerFacade().getUser(request, UserService.GET_USER);

            if (response.isSuccess()) {
                this.user = response.getUser();
                this.authToken = response.getAuthToken();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, this.user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }
}
