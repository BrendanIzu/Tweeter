package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.factory.FeedsDAOInterface;
import edu.byu.cs.tweeter.server.dto.DataPage;
import edu.byu.cs.tweeter.server.dto.FeedsDTO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoFeedsDAO extends DynamoDAOTools implements FeedsDAOInterface {
    DynamoDbTable<FeedsDTO> table = enhancedClient.table("feeds", TableSchema.fromBean(FeedsDTO.class));

    @Override
    public DataPage<FeedsDTO> getPageOfFeeds(String alias, String lastStatus, int limit) {
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

        DataPage<FeedsDTO> result = new DataPage<>();

        PageIterable<FeedsDTO> pages = table.query(queryEnhancedRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<FeedsDTO> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(dto -> result.getValues().add(dto));
                });

        return result;
    }

    @Override
    public void writeBatchOfFeeds(List<FeedsDTO> dtos) {
        if (dtos.size() > 25) {
            throw new RuntimeException("Too many Users to write");
        }

        WriteBatch.Builder<FeedsDTO> writeBuilder = WriteBatch.builder(FeedsDTO.class).mappedTableResource(table);
        for (FeedsDTO item : dtos) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeBatchOfFeeds(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
