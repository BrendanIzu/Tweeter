package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends Response {
    private int count;

    FollowingCountResponse(boolean success, int count) { // TODO: not exactly sure why this is here
        super(success);
    }

    public FollowingCountResponse(int count) {
        super(true);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
