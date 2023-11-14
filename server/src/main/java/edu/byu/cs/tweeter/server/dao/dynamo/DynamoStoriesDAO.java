package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.factory.StoriesDAOInterface;
import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.StoriesDTO;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoStoriesDAO extends DynamoDAOTools implements StoriesDAOInterface {
    @Override
    public DataPage<StoriesDTO> getPageOfStories(String alias, String lastStatus, int limit) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(isNonEmptyString(lastStatus)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(aliasAttr, AttributeValue.builder().s(alias).build());
            startKey.put(statusAttr, AttributeValue.builder().s(lastStatus).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<StoriesDTO> result = new DataPage<>();

        PageIterable<StoriesDTO> pages = storiesTable.query(queryEnhancedRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<StoriesDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(dto -> result.getValues().add(dto));
                });

        return result;
    }

    @Override
    public void insert(StoriesDTO dto) {
        storiesTable.putItem(dto);
    }
}
