package edu.byu.cs.tweeter.client.view.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.feed.FeedFragment;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.client.view.main.story.StoryFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to one of the sections/tabs/pages
 * of the Main Activity.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {
    private static final int REGULAR_FEED_FRAGMENT_POSITION = 0;
    private static final int REGULAR_STORY_FRAGMENT_POSITION = 1;
    private static final int REGULAR_FOLLOWING_FRAGMENT_POSITION = 2;
    private static final int REGULAR_FOLLOWERS_FRAGMENT_POSITION = 3;

    private static final int OTHER_STORY_FRAGMENT_POSITION = 0;
    private static final int OTHER_FOLLOWING_FRAGMENT_POSITION = 1;
    private static final int OTHER_FOLLOWERS_FRAGMENT_POSITION = 2;


    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.feedTabTitle, R.string.storyTabTitle, R.string.followingTabTitle, R.string.followersTabTitle};
    private static final int[] TAB_TITLES_OTHER_USERS = new int[]{R.string.storyTabTitle, R.string.followingTabTitle, R.string.followersTabTitle};
    private final Context mContext;
    private final User user;
    private final AuthToken authToken;

    public SectionsPagerAdapter(Context context, FragmentManager fm, User user, AuthToken authToken) {
        super(fm);
        mContext = context;
        this.user = user;
        this.authToken = authToken;
    }

    public Fragment getItem(int position) {

        if (this.user.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            if (position == REGULAR_FEED_FRAGMENT_POSITION) {
                return FeedFragment.newInstance(user);
            } else if (position == REGULAR_STORY_FRAGMENT_POSITION) {
                return StoryFragment.newInstance(user);
            } else if (position == REGULAR_FOLLOWING_FRAGMENT_POSITION) {
                return FollowingFragment.newInstance(user, authToken);
            } else if (position == REGULAR_FOLLOWERS_FRAGMENT_POSITION) {
                return FollowersFragment.newInstance(user, authToken);
            } else {
                return null;
            }
        } else {
            if (position == OTHER_STORY_FRAGMENT_POSITION) {
                return StoryFragment.newInstance(user);
            } else if (position == OTHER_FOLLOWING_FRAGMENT_POSITION) {
                return FollowingFragment.newInstance(user, authToken);
            } else if (position == OTHER_FOLLOWERS_FRAGMENT_POSITION) {
                return FollowersFragment.newInstance(user, authToken);
            } else {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (this.user.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        } else {
            return mContext.getResources().getString(TAB_TITLES_OTHER_USERS[position]);
        }
    }

    @Override
    public int getCount() {
        if (this.user.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            return 4;
        } else {
            return 3;
        }
    }
}