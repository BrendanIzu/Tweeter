package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends Response {
    private int count;

    FollowersCountResponse(boolean success, int count) { // TODO: not exactly sure why this is here
        super(success);
    }

    public FollowersCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
