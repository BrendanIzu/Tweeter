package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;

import edu.byu.cs.tweeter.server.dto.FollowingDTO;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import edu.byu.cs.tweeter.server.service.ServiceTools;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Filler extends ServiceTools {
    public static void main(String[] args) {
        ServiceTools tools = new ServiceTools();

        // DynamoDB client
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        DynamoDbTable<UserDTO> usersTable = enhancedClient.table("users", TableSchema.fromBean(UserDTO.class));
        DynamoDbTable<FollowingDTO> followingTable = enhancedClient.table("follows", TableSchema.fromBean(FollowingDTO.class));

        UserDTO userEntry = new UserDTO();
        FollowingDTO followingEntry = new FollowingDTO();

        // set default values which will be the same for all dummy users
        userEntry.setPassword(tools.getMD5Hash("pass"));
        userEntry.setImageUrl("https://picsum.photos/200");
        followingEntry.setFollowee_handle("@BrendanIzu");

        for (int i=0; i<Integer.parseInt(args[0]); i++) {
            String alias = "@dummyUser" + i;

            userEntry.setAlias(alias);
            userEntry.setFirstName("Dummy" + i);
            userEntry.setLastName("User" + i);
            userEntry.setFollowingCount(1);
            usersTable.putItem(userEntry);

            followingEntry.setFollower_handle(alias);
            followingTable.putItem(followingEntry);
        }

        Key key = Key.builder()
                .partitionValue("@BrendanIzu")
                .build();

        // Adjust the followers count for the main user
        DynamoUserDAO dao = new DynamoUserDAO();
        UserDTO userDTO = dao.get("@BrendanIzu");
        int followersCount = userDTO.getFollowersCount() + Integer.parseInt(args[0]);
        userDTO.setFollowersCount(followersCount);

        dao.delete(key);
        dao.insert(userDTO);
    }
}
