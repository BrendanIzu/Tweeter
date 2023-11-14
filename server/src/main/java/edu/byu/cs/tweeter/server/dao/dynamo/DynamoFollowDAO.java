package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.factory.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FollowersDTO;
import edu.byu.cs.tweeter.server.dto.FollowingDTO;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoFollowDAO extends DynamoDAOTools implements FollowDAOInterface {
    DynamoDbTable<FollowingDTO> table = enhancedClient.table("follows", TableSchema.fromBean(FollowingDTO.class));
    DynamoDbIndex<FollowersDTO> index = enhancedClient.table("follows", TableSchema.fromBean(FollowersDTO.class)).index(IndexName);

    String followeeAttr = "followee_handle";
    String followerAttr = "follower_handle";

    @Override
    public DataPage<FollowersDTO> getPageOfFollowers(String alias, String lastFollower, int limit) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(isNonEmptyString(lastFollower)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(followeeAttr, AttributeValue.builder().s(alias).build());
            startKey.put(followerAttr, AttributeValue.builder().s(lastFollower).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<FollowersDTO> result = new DataPage<>();

        SdkIterable<Page<FollowersDTO>> sdkIterable = index.query(queryEnhancedRequest);
        PageIterable<FollowersDTO> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowersDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(followers -> result.getValues().add(followers));
                });

        return result;
    }

    @Override
    public DataPage<FollowingDTO> getPageOfFollowees(String alias, String lastFollowee, int limit) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if (isNonEmptyString(lastFollowee)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(followerAttr, AttributeValue.builder().s(alias).build());
            startKey.put(followeeAttr, AttributeValue.builder().s(lastFollowee).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<FollowingDTO> result = new DataPage<> ();

        PageIterable<FollowingDTO> pages = table.query(queryEnhancedRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowingDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows));
                });

        return result;
    }

    @Override
    public List<FollowersDTO> getAllFollowers(String alias) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<FollowersDTO> result = new DataPage<>();

        SdkIterable<Page<FollowersDTO>> sdkIterable = index.query(queryEnhancedRequest);
        PageIterable<FollowersDTO> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowersDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(followers -> result.getValues().add(followers));
                });

        return result.getValues();
    }

    @Override
    public List<FollowingDTO> getAllFollowees(String alias) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<FollowingDTO> result = new DataPage<> ();

        PageIterable<FollowingDTO> pages = table.query(queryEnhancedRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowingDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows));
                });

        return result.getValues();
    }

    @Override
    public FollowingDTO getFollowing(String follower, String followee) {
        Key key = Key.builder()
                .partitionValue(follower)
                .sortValue(followee)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<FollowingDTO> result = new DataPage<> ();

        PageIterable<FollowingDTO> pages = table.query(queryEnhancedRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowingDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows));
                });

        if (result.getValues().size() > 0) {
            return result.getValues().get(0);
        }
        return null;
    }

    @Override
    public void insert(FollowingDTO dto) {
        table.putItem(dto);
    }

    @Override
    public void delete(String follower, String followee) {
        Key followingKey = Key.builder()
                .partitionValue(follower)
                .sortValue(followee)
                .build();

        table.deleteItem(followingKey);
    }
}
