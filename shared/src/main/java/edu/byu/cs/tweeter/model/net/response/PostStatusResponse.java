package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;

public class PostStatusResponse extends Response {
    public PostStatusResponse(String message) {
        super(false, message);
    }

    public PostStatusResponse() {
        super(true, null);
    }
}
