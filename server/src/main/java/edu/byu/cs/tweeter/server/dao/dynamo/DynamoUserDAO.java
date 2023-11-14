package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.factory.UserDAOInterface;
import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

public class DynamoUserDAO extends DynamoDAOTools implements UserDAOInterface {
    DynamoDbTable<UserDTO> table = enhancedClient.table("users", TableSchema.fromBean(UserDTO.class));
    @Override
    public UserDTO get(String alias) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
        DataPage<UserDTO> result = new DataPage<>();
        PageIterable<UserDTO> pages = table.query(queryEnhancedRequest);

        pages.stream()
                .limit(1)
                .forEach((Page<UserDTO> page) -> {
                    page.items().forEach(dto -> result.getValues().add(dto));
                });

        if (result.getValues().size() == 1) {
            return result.getValues().get(0);
        }
        return null;
    }

    @Override
    public void update(UserDTO dto) {
        Key key = Key.builder()
                .partitionValue(dto.getAlias())
                .build();

        table.deleteItem(key);
        table.putItem(dto);
    }

    @Override
    public void insert(UserDTO dto) {
        table.putItem(dto);
    }

    @Override
    public void delete(Key key) {
        table.deleteItem(key);
    }
}
