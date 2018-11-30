package org.iii.wheelpiedemo.menu;

import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.chat.ScoreActivity;
import org.iii.wheelpiedemo.chat.SpeechActivity;
import org.iii.wheelpiedemo.course.CourseActivity;
import org.iii.wheelpiedemo.dashboard.DashboardActivity;
import org.iii.wheelpiedemo.setting.SettingActivity;
import org.iii.wheelpiedemo.training.TrainingActivity;

import java.util.HashMap;
import java.util.Map;

public enum MenuItemObject {
    FREE(R.string.menu_free, R.layout.training_main, 0, TrainingActivity.class),
    TRAINING(R.string.menu_training, R.layout.training_main, 1, TrainingActivity.class),
    EVALUATION(R.string.menu_evaluation, R.layout.activity_score, 2, ScoreActivity.class),
    COURSE(R.string.menu_course, R.layout.activity_course, 3, CourseActivity.class),
    SETTING(R.string.menu_setting, R.layout.activity_setting, 4, SettingActivity.class),
    DASHBOARD(R.string.menu_dashboard, R.layout.dashboard, 5, DashboardActivity.class),
    ASSISTANT(R.string.menu_evaluation, R.layout.activity_speech, 6, SpeechActivity.class);

    private int mTitleResId;
    private int mLayoutResId;
    private int mMenuIndex;
    private Class<?> activityClass;

    MenuItemObject(int titleResId, int layoutResId, int menuIndex, Class<?> cls) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
        mMenuIndex = menuIndex;
        activityClass = cls;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

    public int getmMenuIndex() {
        return mMenuIndex;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    private static final Map<String, MenuItemObject> classNameIndex =
            new HashMap<String, MenuItemObject>(MenuItemObject.values().length);

    static {
        for (MenuItemObject model : MenuItemObject.values()) {
            classNameIndex.put(model.getActivityClass().getName(), model);
        }
    }
    public static MenuItemObject lookupByClassName(String name) {
        return classNameIndex.get(name);
    }


    private static final Map<Integer, MenuItemObject> menuIndex =
            new HashMap<Integer, MenuItemObject>(MenuItemObject.values().length);

    static {
        for (MenuItemObject model : MenuItemObject.values()) {
            menuIndex.put(model.getmMenuIndex(), model);
        }
    }
    public static MenuItemObject lookupByMenuIndex(Integer index) {
        return menuIndex.get(index);
    }
}
