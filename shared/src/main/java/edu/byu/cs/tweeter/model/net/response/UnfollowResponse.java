package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowResponse extends Response {
    public UnfollowResponse(String message) {
        super(false, message);
    }

    public UnfollowResponse() {
        super(true, null);
    }
}
