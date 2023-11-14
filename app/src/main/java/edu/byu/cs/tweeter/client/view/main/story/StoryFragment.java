package edu.byu.cs.tweeter.client.view.main.story;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.FeedPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryFragment extends Fragment implements StoryPresenter.View {
    private static final String LOG_TAG = "StoryFragment";
    private static final String USER_KEY = "UserKey";

    private static final int LOADING_DATA_VIEW = 0;
    private static final int ITEM_VIEW = 1;

    private User user;

    private StoryRecyclerViewAdapter storyRecyclerViewAdapter;

    private StoryPresenter presenter;

    public static StoryFragment newInstance(User user) {
        StoryFragment fragment = new StoryFragment();

        Bundle args = new Bundle(1);
        args.putSerializable(USER_KEY, user);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        //noinspection ConstantConditions
        user = (User) getArguments().getSerializable(USER_KEY);

        AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();

        presenter = new StoryPresenter(this, user, authToken);

        RecyclerView storyRecyclerView = view.findViewById(R.id.storyRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        storyRecyclerView.setLayoutManager(layoutManager);

        storyRecyclerViewAdapter = new StoryRecyclerViewAdapter();
        storyRecyclerView.setAdapter(storyRecyclerViewAdapter);

        storyRecyclerView.addOnScrollListener(new StoryRecyclerViewPaginationScrollListener(layoutManager));

        presenter.loadMoreItems();

        return view;
    }

    @Override
    public void addItems(List<Status> statuses) {
        storyRecyclerViewAdapter.addItems(statuses);
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getUser(User user) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        startActivity(intent);
    }

    @Override
    public void setLoading(boolean value) {
        if (value) {
            storyRecyclerViewAdapter.addLoadingFooter();
        } else {
            storyRecyclerViewAdapter.removeLoadingFooter();
        }
    }

    private class StoryHolder extends RecyclerView.ViewHolder {

        private final ImageView userImage;
        private final TextView userAlias;
        private final TextView userName;
        private final TextView post;
        private final TextView datetime;

        StoryHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.statusImage);
            userAlias = itemView.findViewById(R.id.statusAlias);
            userName = itemView.findViewById(R.id.statusName);
            post = itemView.findViewById(R.id.statusPost);
            datetime = itemView.findViewById(R.id.statusDatetime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.getUser(userAlias);
                }
            });
        }

        void bindStatus(Status status) {
            Picasso.get().load(status.getUser().getImageUrl()).into(userImage);
            userAlias.setText(status.getUser().getAlias());
            userName.setText(status.getUser().getName());
            datetime.setText(status.getFormattedDate());

            // @mentions and urls clickable
            SpannableString spannableString = new SpannableString(status.getPost());


            for (String mention : status.getMentions()) {
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        TextView textView = (TextView) widget;

                        Spanned s = (Spanned) textView.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        String alias = s.subSequence(start, end).toString();

                        presenter.getUserFromHandle(alias);
                    }

                    @Override
                    public void updateDrawState(@NotNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        ds.setUnderlineText(false);
                    }
                };

                int startIndex = status.getPost().indexOf(mention);
                spannableString.setSpan(span, startIndex, (startIndex + mention.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            for (String url : status.getUrls()) {
                int startIndex = status.getPost().indexOf(url);
                spannableString.setSpan(new URLSpan(url), startIndex, (startIndex + url.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            post.setText(spannableString);
            post.setClickable(true);
            post.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private class StoryRecyclerViewAdapter extends RecyclerView.Adapter<StoryHolder> {

        private final List<Status> story = new ArrayList<>();

        void addItems(List<Status> newStory) {
            int startInsertPosition = story.size();
            story.addAll(newStory);
            this.notifyItemRangeInserted(startInsertPosition, newStory.size());
        }

        void addItem(Status status) {
            story.add(status);
            this.notifyItemInserted(story.size() - 1);
        }

        void removeItem(Status status) {
            int position = story.indexOf(status);
            story.remove(position);
            this.notifyItemRemoved(position);
        }

        @NonNull
        @Override
        public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(StoryFragment.this.getContext());
            View view;

            if (viewType == LOADING_DATA_VIEW) {
                view = layoutInflater.inflate(R.layout.loading_row, parent, false);

            } else {
                view = layoutInflater.inflate(R.layout.status_row, parent, false);
            }

            return new StoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StoryHolder storyHolder, int position) {
            if (!presenter.isLoading()) {
                storyHolder.bindStatus(story.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return story.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (position == story.size() - 1 && presenter.isLoading()) ? LOADING_DATA_VIEW : ITEM_VIEW;
        }

        void loadMoreItems() {
            presenter.loadMoreItems();

        }

        private void addLoadingFooter() {
            addItem(new Status("Dummy Post", new User("firstName", "lastName", "@coolAlias"), System.currentTimeMillis(), new ArrayList<String>() {{
                add("https://youtube.com");
            }}, new ArrayList<String>() {{
                add("@Dude1");
            }}));
        }

        private void removeLoadingFooter() {
            removeItem(story.get(story.size() - 1));
        }
    }

    private class StoryRecyclerViewPaginationScrollListener extends RecyclerView.OnScrollListener {

        private final LinearLayoutManager layoutManager;

        StoryRecyclerViewPaginationScrollListener(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!presenter.isLoading() && presenter.hasMorePages()) {
                if ((visibleItemCount + firstVisibleItemPosition) >=
                        totalItemCount && firstVisibleItemPosition >= 0) {
                    // Run this code later on the UI thread
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        storyRecyclerViewAdapter.loadMoreItems();
                    }, 0);
                }
            }
        }
    }

}
