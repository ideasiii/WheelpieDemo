package org.iii.wheelpiedemo.menu;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.menu.adapter.CustomAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherDialogFragment extends DialogFragment {

    private Activity activity;

//    public OtherDialogFragment() {
//        // Required empty public constructor
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用AlertDialog建立'更多功能表
        activity = getActivity();

        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_other_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.menu_other))
                .setIcon(R.drawable.ic_more_horiz_white_24dp)
                .setView(view);

        //設定'更多功能'功能清單列表
        String[] options = { // 功能表文字
            activity.getResources().getString(R.string.menu_setting),
            activity.getResources().getString(R.string.menu_dashboard),
            activity.getResources().getString(R.string.menu_assistant)
        };
        int[] drawableIds = { // 功能表icon
            R.drawable.ic_settings_black_24dp,
            R.drawable.ic_developer_board_black_24dp,
            R.drawable.ic_mic_black_24dp
        };
        ListView listView = view.findViewById(R.id.other_list_view);
        CustomAdapter arrayAdapter = new CustomAdapter(activity,  options, drawableIds);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onDialogItemClicked(position);
            }
        });

        return builder.create();
    }

    OnDialogItemClickedListener mListener;

    public void setOnDialogItemClickedListener(Activity activity) {
        mListener = (OnDialogItemClickedListener) activity;
    }

    public interface OnDialogItemClickedListener {
        public void onDialogItemClicked(int menuId);
    }
}
