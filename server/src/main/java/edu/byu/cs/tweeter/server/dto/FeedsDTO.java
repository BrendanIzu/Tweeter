package edu.byu.cs.tweeter.server.dto;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FeedsDTO {
    private String alias;     // NOTE: the alias in this case is the user who will see the post, the author is the one who wrote it
    private Long timestamp;
    private String post;
    private String author;
    private List<String> mentions;
    private List<String> urls;

    public FeedsDTO () {}

    public FeedsDTO(String alias, Status status) {
        this.alias = alias;
        this.timestamp = status.getTimestamp();
        this.post = status.getPost();
        this.author = status.getUser().getAlias();
        this.mentions = status.getMentions();
        this.urls = status.getUrls();
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

    @DynamoDbAttribute("author")
    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public Status convertToStatus(User user) {
        return new Status(post, user, timestamp, urls, mentions);
    }
}
