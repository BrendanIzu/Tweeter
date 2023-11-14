package edu.byu.cs.tweeter.client.presenter;

import android.text.style.ClickableSpan;
import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.LoadItemsObserverInterface;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleErrorObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleUserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserObserverInterface;
import edu.byu.cs.tweeter.client.view.ViewInterface;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.View> {
    private static final int PAGE_SIZE = 10;
    private boolean hasMorePages;
    private boolean isLoading;

    private UserService userService;
    private T lastItem;

    protected abstract void callService(User user, int pageSize, T lastItem, SimpleLoadItemsObserver observer);

    public PagedPresenter(View view) {
        super(view);
        this.userService = new UserService();
    }

    public interface View<T> extends ViewInterface {
        void addMoreItems(List<T> items);
        void setLoadingFooter(boolean value);
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);

            // abstract method call
            callService(user, PAGE_SIZE, lastItem, new SimpleLoadItemsObserver(view));
        }
    }

    public class SimpleLoadItemsObserver extends SimpleErrorObserver<ViewInterface> implements LoadItemsObserverInterface<T> {
        public SimpleLoadItemsObserver(ViewInterface View) {
            super(View);
        }

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            lastItem = (items.size() > 0) ? (T) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.setLoadingFooter(false);
            isLoading = false;
            view.addMoreItems(items);
        }

        @Override
        protected String getError() {
            return getErrorTag();
        }

        @Override
        protected void failureLogic() {
            isLoading = false;
            view.setLoadingFooter(false);
        }

        @Override
        protected void exceptionLogic() {
            isLoading = false;
            view.setLoadingFooter(false);
        }
    }
}
