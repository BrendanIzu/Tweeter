package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingHandler implements RequestHandler<FollowingRequest, FollowingResponse> {
    @Override
    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        FollowService service = new FollowService(new DynamoFactory());
        return service.getFollowees(request);
    }
}
