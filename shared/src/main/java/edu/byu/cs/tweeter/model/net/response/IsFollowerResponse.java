package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerResponse extends Response {
    private AuthToken authToken;
    public Boolean isFollowing;

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(AuthToken authToken, Boolean isFollowing) {
        super(true, null);
        this.authToken = authToken;
        this.isFollowing = isFollowing;
    }

    public Boolean isFollowing() {
        return isFollowing;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
