package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.factory.AuthDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.FeedsDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.StoriesDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.factory.FactoryInterface;

public class DynamoFactory implements FactoryInterface {
    @Override
    public AuthDAOInterface createAuthDAO() {
        return new DynamoAuthDAO();
    }

    @Override
    public FeedsDAOInterface createFeedsDAO() {
        return new DynamoFeedsDAO();
    }

    @Override
    public FollowDAOInterface createFollowDAO() {
        return new DynamoFollowDAO();
    }

    @Override
    public StoriesDAOInterface createStoriesDAO() {
        return new DynamoStoriesDAO();
    }

    @Override
    public UserDAOInterface createUserDAO() {
        return new DynamoUserDAO();
    }
}
