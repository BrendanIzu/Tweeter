package edu.byu.cs.tweeter.server.pojo;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class UpdateFeedBatchPOJO {
    public Status status;
    public List<String> aliases;

    public UpdateFeedBatchPOJO(Status status, List<String> aliases) {
        this.status = status;
        this.aliases = aliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
}
