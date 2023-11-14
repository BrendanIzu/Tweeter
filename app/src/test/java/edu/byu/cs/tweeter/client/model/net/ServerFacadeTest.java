package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeTest {
    private final String INVALID_URL_PATH = "/invalid";

    private ServerFacade serverFacade;
    private AuthToken authToken;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
        authToken = new AuthToken();
    }

    @Test
    public void test_register() {
        RegisterRequest request = new RegisterRequest("Brendan", "Izu", "@Bubs", "password", "path");

        Assertions.assertThrows(RuntimeException.class, () -> serverFacade.register(request, INVALID_URL_PATH));
        Assertions.assertDoesNotThrow(() -> serverFacade.register(request, "/register"));

        try {
            RegisterResponse response  = serverFacade.register(request, "/register");
            Assertions.assertNotNull(response.getUser());

        } catch(Exception ex) {
            System.out.println("got exception: " + ex.getMessage());
        }
    }

    @Test
    public void test_getFollowers() {
        FollowersRequest request = new FollowersRequest(authToken, "@Bob", 3, null);

        Assertions.assertThrows(RuntimeException.class, () -> serverFacade.getFollowers(request, INVALID_URL_PATH));
        Assertions.assertDoesNotThrow(() -> serverFacade.getFollowers(request, "/getfollowers"));

        try {
            FollowersResponse response  = serverFacade.getFollowers(request, "/getfollowers");
            Assertions.assertNotNull(response.getFollowers());

        } catch(Exception ex) {
            System.out.println("got exception: " + ex.getMessage());
        }
    }

    @Test
    public void test_getFollowersCount() {
        FollowersCountRequest request = new FollowersCountRequest(authToken, "@Bob");

        Assertions.assertThrows(RuntimeException.class, () -> serverFacade.getFollowersCount(request, INVALID_URL_PATH));
        Assertions.assertDoesNotThrow(() -> serverFacade.getFollowersCount(request, "/getfollowerscount"));

        try {
            FollowersCountResponse response  = serverFacade.getFollowersCount(request, "/getfollowerscount");
            Assertions.assertNotNull(response.getCount());

        } catch(Exception ex) {
            System.out.println("got exception: " + ex.getMessage());
        }
    }
}
