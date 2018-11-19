package org.iii.wheelpiedemo.menu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.iii.wheelpiedemo.R;

public abstract class NavigationActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        OtherDialogFragment.OnDialogItemClickedListener {

    protected Bundle bundle;
    protected final static String KEY_TRAINING_MODE = "TRAINING_MODE";
    protected final static String TRAINING_MODE_FREE = "FREE";
    protected final static String TRAINING_MODE_SMART = "SMART";
    private BottomNavigationView bottomNavigationView;
    private OtherDialogFragment otherDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
    }

    /***
     * 繼承的Activity，請在setContentView後，呼叫此method
     */
    public void initCommonNavigationView() {
        // 取得BottomNavigationView
        int navId = getBottomNavigationViewId();
        bottomNavigationView = findViewById(navId);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
            initNavigationViewState();
        }
    }

    public void initNavigationViewState() {
        String subClassName = this.getClass().getName();
        MenuItemObject selectedItem = MenuItemObject.lookupByClassName(subClassName);
        int checkedMenuIndex;
        String mode;
        // 如果是自由或智慧訓練，執行參數檢查
        if (selectedItem == MenuItemObject.FREE ||
            selectedItem == MenuItemObject.TRAINING )
        {
            mode = bundle != null ? bundle.getString(KEY_TRAINING_MODE) : null;
            if (TRAINING_MODE_FREE.equals(mode)) {
                selectedItem = MenuItemObject.FREE;
            } else if (TRAINING_MODE_SMART.equals(mode)) {
                selectedItem = MenuItemObject.TRAINING;
            }
        }
        checkedMenuIndex = selectedItem.getmMenuIndex();
        if (checkedMenuIndex > 4) {
            checkedMenuIndex = 4;
        }
        bottomNavigationView.getMenu().getItem(checkedMenuIndex).setChecked(true);
    }

    /**
     * 繼承的Activity，請實作此method，回傳共用選單的id
     * @return
     */
    public abstract int getBottomNavigationViewId();

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof OtherDialogFragment) {
            otherDialogFragment = (OtherDialogFragment) fragment;
            otherDialogFragment.setOnDialogItemClickedListener(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_free:
                toView(MenuItemObject.FREE);
                return true;
            case R.id.main_training:
                toView(MenuItemObject.TRAINING);
                return true;
            case R.id.main_course:
                toView(MenuItemObject.COURSE);
                return true;
            case R.id.main_evaluation:
                toView(MenuItemObject.EVALUATION);
                return true;
            case R.id.main_other:
                showOtherMenuDialog();
                return true;
        }
        return false;
    }

    @Override
    public void onDialogItemClicked(int menuId) {
        final int moreMenuBaseIndex = 4;
        toView(MenuItemObject.lookupByMenuIndex(moreMenuBaseIndex + menuId));
    }

    protected void toView(MenuItemObject view) {
        Bundle bundle = null;
        if (view.equals(MenuItemObject.FREE)) {
            bundle = new Bundle ();
            bundle.putString(KEY_TRAINING_MODE, TRAINING_MODE_FREE);
            toView(view.getActivityClass());
        } else if (view.equals(MenuItemObject.TRAINING)){
            bundle = new Bundle ();
            bundle.putString(KEY_TRAINING_MODE, TRAINING_MODE_SMART);
        }
        toView(view.getActivityClass(), bundle);
    }

    protected void toView(Class<?> cls) {
        toView(cls, null);
    }

    protected void toView(Class<?> cls, Bundle bundle) {
        Intent it = new Intent(this, cls);
        if (bundle != null) {
            it.putExtras(bundle);
        }
        startActivity(it);
    }

    protected void showOtherMenuDialog() {
        // Create an instance of the dialog fragment and show it
        otherDialogFragment = new OtherDialogFragment();
        otherDialogFragment.show(this.getFragmentManager(), "OtherDialogFragment");
    }

    protected boolean isSmartTrainingMode() {
        boolean matched = false;
        if (bundle != null) {
            matched = TRAINING_MODE_SMART.equals(
                bundle.getString(KEY_TRAINING_MODE)
            );
        }
        return matched;
    }

    protected boolean isFreeTrainingMode() {
        boolean matched = false;
        if (bundle != null) {
            matched = TRAINING_MODE_FREE.equals(
                bundle.getString(KEY_TRAINING_MODE)
            );
        }
        return matched;
    }
}
