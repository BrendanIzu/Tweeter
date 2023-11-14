package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.view.ViewInterface;

public abstract class Presenter<T extends ViewInterface> {
    protected T view;

    protected abstract String getErrorTag();

    public Presenter(T view) {
        this.view = view;
    }
}
