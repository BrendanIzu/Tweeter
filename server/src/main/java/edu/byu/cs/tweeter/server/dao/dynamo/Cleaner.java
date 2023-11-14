package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dto.FeedsDTO;
import edu.byu.cs.tweeter.server.dto.FollowingDTO;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Cleaner {
    public static void main(String[] args) {
        DynamoDAOTools tools = new DynamoDAOTools();

        // DynamoDB client
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        DynamoDbTable<UserDTO> usersTable = enhancedClient.table("users", TableSchema.fromBean(UserDTO.class));
        DynamoDbTable<FollowingDTO> followingTable = enhancedClient.table("follows", TableSchema.fromBean(FollowingDTO.class));
        DynamoDbTable<FeedsDTO> feedsTable = enhancedClient.table("feeds", TableSchema.fromBean(FeedsDTO.class));

        for (int i=0; i<Integer.parseInt(args[0]); i++) {
            String alias = "@dummyUser" + i;

            Key userKey = Key.builder()
                    .partitionValue(alias)
                    .build();

            usersTable.deleteItem(userKey);

            Key followingKey = Key.builder()
                    .partitionValue(alias)
                    .sortValue("@BrendanIzu")
                    .build();

            followingTable.deleteItem(followingKey);
        }

        Key key = Key.builder()
                .partitionValue("@BrendanIzu")
                .build();

        // Adjust the followers count for the main user
        DynamoUserDAO dao = new DynamoUserDAO();
        UserDTO userDTO = dao.get("@BrendanIzu");
        int followersCount = userDTO.getFollowersCount() - Integer.parseInt(args[0]);
        userDTO.setFollowersCount(followersCount);

        dao.delete(key);
        dao.insert(userDTO);
    }
}
