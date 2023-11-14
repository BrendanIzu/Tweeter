package edu.byu.cs.tweeter.server.service;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoFactory;
import edu.byu.cs.tweeter.server.dto.AuthDTO;
import edu.byu.cs.tweeter.server.dto.UserDTO;

public class UserService extends ServiceTools {
    public UserService(DynamoFactory factory) {
        this.factory = factory;
    }

    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null) {
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        if (request.getUsername().charAt(0) != '@') {
            return new LoginResponse("username must begin with '@'");
        } else if (!checkUsernameExists(request.getUsername())) {
            return new LoginResponse("user not found");
        } else if (!validateLogin(request.getUsername(), request.getPassword())) {
            return new LoginResponse("incorrect password");
        }

        AuthToken authToken = generateNewAuthToken();
        AuthDTO authDTO = new AuthDTO();

        authDTO.setAlias(request.getUsername());
        authDTO.setToken(authToken.getToken());

        getAuthDAO().insert(authDTO);

        UserDTO userDTO = getUserDAO().get(request.getUsername());
        User user = userDTO.convertToUser();

        return new LoginResponse(user, authToken);
    }

    public UserResponse getUser(UserRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing a username");
        }

        UserDTO userDTO = getUserDAO().get(request.getAlias());
        User user = userDTO.convertToUser();

        // TODO: I am suspect of this convert to user business going on here
        return new UserResponse(user, generateNewAuthToken());
    }

    public LogoutResponse logout(LogoutRequest request) {
        AuthDTO authDTO = new AuthDTO();

        authDTO.setToken(request.getAuthToken().getToken());

        getAuthDAO().delete(authDTO);

        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getUsername() == null) {
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if (request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        if (request.getUsername().charAt(0) != '@') {
            return new RegisterResponse("username must begin with '@'");
        } else if (checkUsernameExists(request.getUsername())) {
            return new RegisterResponse("Failed to register new user, alias taken");
        }

        String filename = request.getUsername() + "_profile_pic";
        insertImageIntoS3(request.getImage(), filename);

        // 2. get imageUrl from S3 and hashed password
        String s3ImageUrl = getImageFromS3(filename);
        String hashedPassword = getMD5Hash(request.getPassword());

        // 3. Add new Users into users table
        UserDTO userDTO = new UserDTO();
        userDTO.setAlias(request.getUsername());
        userDTO.setFirstName(request.getFirstname());
        userDTO.setLastName(request.getLastname());
        userDTO.setImageUrl(s3ImageUrl);
        userDTO.setPassword(hashedPassword);
        userDTO.setFollowersCount(0);
        userDTO.setFollowingCount(0);

        getUserDAO().insert(userDTO);

        User user = userDTO.convertToUser();
        AuthToken authToken = generateNewAuthToken();
        AuthDTO authDTO = new AuthDTO();

        authDTO.setToken(authToken.getToken());
        authDTO.setAlias(request.getUsername());

        getAuthDAO().insert(authDTO);

        return new RegisterResponse(user, authToken);
    }

    private Boolean checkUsernameExists(String alias) {
        return getUserDAO().get(alias) != null;
    }

    private Boolean validateLogin(String alias, String password) {
        UserDTO userDTO = getUserDAO().get(alias);

        if (userDTO != null) {
            return Objects.equals(userDTO.getPassword(), getMD5Hash(password));
        }
        return false;
    }
}
