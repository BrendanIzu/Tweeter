package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dto.AuthDTO;
import edu.byu.cs.tweeter.server.dto.FeedsDTO;
import edu.byu.cs.tweeter.server.dto.StatusesDTO;
import edu.byu.cs.tweeter.server.dto.StoriesDTO;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDAOTools {
    protected static final String AUTHTOKENS_TABLE = "authtokens";
    protected static final String STATUS_TABLE = "statuses";
    protected static final String STORIES_TABLE = "stories";

    protected static final String IndexName = "follows_index";
    protected static final String aliasAttr = "alias";
    protected static final String statusAttr = "status";

    // DynamoDB client
    protected static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    protected static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    DynamoDbTable<AuthDTO> authtokensTable = enhancedClient.table(AUTHTOKENS_TABLE, TableSchema.fromBean(AuthDTO.class));
    DynamoDbTable<StoriesDTO> storiesTable = enhancedClient.table(STORIES_TABLE, TableSchema.fromBean(StoriesDTO.class));


    protected static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }
}
