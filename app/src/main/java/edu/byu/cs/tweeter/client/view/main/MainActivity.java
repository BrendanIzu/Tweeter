package edu.byu.cs.tweeter.client.view.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, MainActivityPresenter.View {
    private static final String LOG_TAG = "MainActivity";
    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private MainActivityPresenter presenter;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if(selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser, authToken);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        presenter = new MainActivityPresenter(this);

        presenter.updateSelectedUserFollowingAndFollowers(selectedUser);

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(selectedUser.getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        presenter.setFollowing(selectedUser);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeFollowStatus(followButton, selectedUser);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item, super.onOptionsItemSelected(item));
    }

    @Override
    public void updateSelectedUserFollowingAndFollowers() {
        presenter.updateSelectedUserFollowingAndFollowers(selectedUser);
    }

    @Override
    public void cancel() {
        //logOutToast.cancel();
    }

    @Override
    public void cancelPost() {
        //postingToast.cancel();
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void enableFollowButton(boolean value) {
        followButton.setEnabled(true);
    }

    @Override
    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Cache.getInstance().clearCache();
        startActivity(intent);
    }

    @Override
    public void setFollowButtonProperties(int value, int backgroundColor, int textColor) {
        followButton.setText(value);
        followButton.setBackgroundColor(getResources().getColor(backgroundColor));
        followButton.setTextColor(getResources().getColor(textColor));
    }

    @Override
    public void setFollowButtonVisibility(int value) {
        followButton.setVisibility(value);
    }

    @Override
    public void setFolloweeCount(int count, String value) {
        followeeCount.setText(getString(count, value));
    }

    @Override
    public void setFollowersCount(int count, String value) {
        followerCount.setText(getString(count, value));
    }

    @Override
    public void setLogoutToast(String message) {
        //logOutToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
    }

    @Override
    public void setPostingToast(String message) {
        //postingToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
    }

    @Override
    public void updateFollowButton(boolean removed) {
        presenter.updateFollowButton(removed);
    }

    @Override
    public void onStatusPosted(String post) {
        presenter.onStatusPosted(post);
    }
}