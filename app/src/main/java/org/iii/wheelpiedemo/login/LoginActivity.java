package org.iii.wheelpiedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.course.CourseActivity;


public class LoginActivity extends AppCompatActivity {
    private Button showLoginBtn;
    private Button fbLoginBtn;
    private ImageButton closeLoginBtn;

    private View.OnClickListener showLoginBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            showLoginBtnClicked(view);
        }
    };

    private View.OnClickListener fbLoginBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fbButtonClicked();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        showLoginBtn = findViewById(R.id.button2);

        showLoginBtn.setOnClickListener(showLoginBtnOnClickListener);
    }

    private void showLoginBtnClicked(View view){
        View popupContentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupContentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,true);
        popupWindow.setAnimationStyle(R.style.login_popUpAnim);
        popupWindow.showAtLocation(view, 0,0,0);

        closeLoginBtn = popupContentView.findViewById(R.id.imageButton2);
        closeLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }

        });

        fbLoginBtn = popupContentView.findViewById(R.id.button4);
        fbLoginBtn.setOnClickListener(fbLoginBtnOnClickListener);
    }

    private void fbButtonClicked() {
        startActivity(new Intent(this, CourseActivity.class));
//        fbLoginBtn.setText("AWESOME");
    }
}
