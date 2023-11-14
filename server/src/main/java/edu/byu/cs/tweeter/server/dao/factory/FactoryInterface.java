package edu.byu.cs.tweeter.server.dao.factory;

public interface FactoryInterface {
    AuthDAOInterface createAuthDAO();
    FeedsDAOInterface createFeedsDAO();
    FollowDAOInterface createFollowDAO();
    StoriesDAOInterface createStoriesDAO();
    UserDAOInterface createUserDAO();
}
