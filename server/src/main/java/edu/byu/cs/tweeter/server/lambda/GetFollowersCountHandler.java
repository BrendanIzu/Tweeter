package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersCountHandler implements RequestHandler<FollowersCountRequest, FollowersCountResponse> {
    @Override
    public FollowersCountResponse handleRequest(FollowersCountRequest request, Context context) {
        FollowService service = new FollowService(new DynamoFactory());
        return service.getFollowersCount(request);
    }
}
