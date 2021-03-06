package me.li2.android.tutorial.Gson.ChangeSettingsAccess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import me.li2.android.tutorial.BasicUI.BasicFragmentContainerActivity;

import static me.li2.android.tutorial.BasicUI.LogHelper.makeLogTag;

/**
 * Created by weiyi on 24/04/2017.
 * https://github.com/li2
 */

public class ChangeSettingsAccessActivity extends BasicFragmentContainerActivity {
    private static final String TAG = makeLogTag(ChangeSettingsAccessActivity.class);

    private SettingsAccessProvider mDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataProvider = new SettingsAccessProvider(this);

        SettingsAccessItem rootItem = mDataProvider.getRootItem();
        if (rootItem != null) {
            updateView(rootItem);
        }
    }

    @Override
    protected Fragment createFragment() {
        ChangeSettingsAccessFragment fragment = new ChangeSettingsAccessFragment();
        fragment.setOnSettingsAccessItemClickListener(mOnSettingsAccessItemClickListener);
        return fragment;
    }

    // Back to Previous Screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SettingsAccessItem prevItem = mDataProvider.getPrevItem();

                if (prevItem == null) {
                    finish();
                } else {
                    updateView(prevItem);
                }
                return true;

            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }

    // Enter into Next Screen
    private ChangeSettingsAccessFragment.OnSettingsAccessItemClickListener mOnSettingsAccessItemClickListener =
            new ChangeSettingsAccessFragment.OnSettingsAccessItemClickListener() {
                @Override
                public void onItemClick(SettingsAccessItem item) {
                    updateView(item);
                }

                @Override
                public void onCheckedChanged(SettingsAccessItem item, boolean checked) {
                    mDataProvider.updateItem(item, checked);
                }
            };

    private void updateView(SettingsAccessItem item) {
        if (item != null) {
            mDataProvider.setCurrentItem(item);
            getSupportActionBar().setTitle(item.mTitle);

            if (mFragment != null && mFragment instanceof ChangeSettingsAccessFragment) {
                ((ChangeSettingsAccessFragment) mFragment).setItems(item.mSubItems);
            }
        }
    }
}
