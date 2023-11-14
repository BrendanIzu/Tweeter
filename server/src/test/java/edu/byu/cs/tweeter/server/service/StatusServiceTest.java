package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StatusServiceTest {
    private StoryRequest request;
    private StoryResponse expectedResponse;
    //private FakeDataStatusDAO mockStatusDAO;
    private StatusService statusServiceSpy;

    @BeforeEach
    public void setup() {
        AuthToken authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", null);

        Status status1 = new Status("post1", currentUser, null, null, null);
        Status status2 = new Status("post2", currentUser, null, null, null);
        Status status3 = new Status("post3", currentUser, null, null, null);

        // Setup a request object to use in the tests
        request = new StoryRequest(authToken, currentUser.getAlias(), 3, null);

        // Setup a mock StatusDAO that will return known responses
        expectedResponse = new StoryResponse(Arrays.asList(status1, status2, status3), false);
        //mockStatusDAO = Mockito.mock(FakeDataStatusDAO.class);
        //Mockito.when(mockStatusDAO.getStory(request)).thenReturn(expectedResponse);

        statusServiceSpy = Mockito.spy(StatusService.class);
        //Mockito.when(statusServiceSpy.getStatusDAO()).thenReturn(mockStatusDAO);
    }

    /**
     * Verify that the {@link StatusService#getStory(StoryRequest)}
     * method returns the same result as the {@link FakeDataStatusDAO} class.
     */
    @Test
    public void testGetStory_validRequest_correctResponse() {
        StoryResponse response = statusServiceSpy.getStory(request);
        Assertions.assertEquals(expectedResponse, response);
    }
}
