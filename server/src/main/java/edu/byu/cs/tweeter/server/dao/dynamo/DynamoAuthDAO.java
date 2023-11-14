package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.factory.AuthDAOInterface;
import edu.byu.cs.tweeter.server.dto.AuthDTO;
import edu.byu.cs.tweeter.server.dto.DataPage;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

public class DynamoAuthDAO extends DynamoDAOTools implements AuthDAOInterface {
    @Override
    public AuthDTO get(String token) {
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(1);

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<AuthDTO> result = new DataPage<>();

        PageIterable<AuthDTO> pages = authtokensTable.query(queryEnhancedRequest);

        pages.stream()
                .limit(1)
                .forEach((Page<AuthDTO> page) -> {
                    page.items().forEach(status -> result.getValues().add(status));
                });

        if (result.getValues().size() == 1) {
            return result.getValues().get(0);
        }
        return null;
    }

    @Override
    public void insert(AuthDTO dto) {
        authtokensTable.putItem(dto);
    }

    @Override
    public void delete(AuthDTO dto) {
        Key key = Key.builder()
                .partitionValue(dto.getToken())
                .build();

        authtokensTable.deleteItem(dto);
    }
}
