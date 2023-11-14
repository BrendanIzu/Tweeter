package edu.byu.cs.tweeter.server.dto;

import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@DynamoDbBean
public class FollowingDTO {
    private String follower_handle;
    private String followee_handle;

    // DynamoDB client
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    @DynamoDbPartitionKey
    @DynamoDbSecondaryPartitionKey(indexNames = "follows_index")
    public String getFollower_handle() {
        return follower_handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondarySortKey(indexNames = "follows_index")
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    @Override
    public String toString() {
        return "Follows{" +
                "follower='" + follower_handle + '\'' +
                ", followee='" + followee_handle + '\'' +
                '}';
    }

    /**
     * gets followee information from Users table and returns a new User object from that information received
     * @return new User
     */
    public User convertToUser() {
        DynamoDbTable<UserDTO> table = enhancedClient.table("users", TableSchema.fromBean(UserDTO.class));

        Key key = Key.builder()
                .partitionValue(followee_handle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(1);

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<UserDTO> result = new DataPage<UserDTO>();

        PageIterable<UserDTO> pages = table.query(queryEnhancedRequest);

        pages.stream()
                .limit(1)
                .forEach((Page<UserDTO> page) -> {
                    page.items().forEach(user -> result.getValues().add(user));
                });

        if (result.getValues().size() == 1) {
            UserDTO data = result.getValues().get(0);
            return new User(data.getFirstName(), data.getLastName(), data.getAlias(), data.getImageUrl());
        }

        // means that no matching entry was found in the Users table
        return null;
    }
}