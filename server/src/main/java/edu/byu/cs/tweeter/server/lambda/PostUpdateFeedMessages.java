package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        // receives one message from SQS Post Status Queue containing the Status information that needs to be distributed to each feed

        // call updateFeeds which will handle posting to the next queue
        FollowService followService = new FollowService(new DynamoFactory());

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            followService.sendToUpdateFeedQueue(msg.getBody());
        }
        return null;
    }
}
