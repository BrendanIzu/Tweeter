package edu.byu.cs.tweeter.server.dto;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class StoriesDTO {
    private String alias;
    private Long timestamp;
    private String post;
    private List<String> mentions;
    private List<String> urls;

    public StoriesDTO () {}

    public StoriesDTO (Status status) {
        this.alias = status.getUser().getAlias();
        this.timestamp = status.getTimestamp();
        this.post = status.getPost();
        this.urls = status.getUrls();
        this.mentions = status.getMentions();
    }

    @DynamoDbPartitionKey
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDbAttribute("post")
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @DynamoDbAttribute("mentions")
    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    @DynamoDbAttribute("urls")
    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Status convertToStatus(User user) {
        return new Status(post, user, timestamp, urls, mentions);
    }
}
