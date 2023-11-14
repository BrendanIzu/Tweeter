package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginFragment;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class PostStatusTest {
    private static final String POST_TEXT = "new one";

    private ServerFacade server;
    private MainActivityPresenter.View viewMock;
    private MainActivityPresenter presenter;
    private CountDownLatch latch;
    private Status status;

    @BeforeEach
    public void setup() {
        // A. Login a user.
        server = new ServerFacade();
        LoginRequest loginRequest = new LoginRequest("@dummyUser41", "pass");

        try {
            LoginResponse loginResponse = server.login(loginRequest, UserService.LOGIN);
            Cache.getInstance().setCurrUser(loginResponse.getUser());
            Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

        status = new Status(POST_TEXT, Cache.getInstance().getCurrUser(), 5L, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    public void testPostStatus() {
        viewMock = Mockito.mock(MainActivity.class);
        latch = Mockito.mock(CountDownLatch.class);

        Mockito.doAnswer((invocation -> {
            latch.countDown();
            return null;
        })).when(viewMock).displayMessage("Successfully Posted!");

        presenter = Mockito.spy(new MainActivityPresenter(viewMock));
        Mockito.when(presenter.getNewStatus(Mockito.anyString())).thenReturn(status);

        // B. Post a status from the user to the server by calling the "post status" operation on the relevant Presenter.
        presenter.onStatusPosted(POST_TEXT);

        // C. Verify that the "Successfully Posted!" message was displayed to the user.
        Mockito.verify(latch).countDown();

        try {
            server.logout(new LogoutRequest(Cache.getInstance().getCurrUserAuthToken()), UserService.LOGOUT);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        // D. Retrieve the user's story from the server to verify that the new status was correctly appended to the user's story, and that all status details are correct.
        StoryRequest request = new StoryRequest(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser().getAlias(), 1000, null);
        Status foundStatus = null;

        try {
            StoryResponse response = server.getStory(request, StatusService.GET_STORY);
            System.out.println(response.getStatuses());
            System.out.println(status);
            for (Status s : response.getStatuses()) {
                if (Objects.equals(s.post, status.post) && Objects.equals(s.timestamp, status.timestamp)) {
                    foundStatus = s;
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        Assertions.assertNotNull(foundStatus);
        Assertions.assertEquals(foundStatus, status);
    }
}