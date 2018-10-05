package org.iii.wheelpiedemo.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;

import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.common.Logs;

import java.util.ArrayList;

public class SpeechActivity extends Activity
{

    private int voiceRecognitionRequestCode = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        findViewById(R.id.imageViewSpeechBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startVoiceRecognitionActivity();
            }
        });
    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說出您現在的狀況");

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent
                .LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        startActivityForResult(intent, voiceRecognitionRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == voiceRecognitionRequestCode && resultCode == Activity.RESULT_OK)
        {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent
                                                                             .EXTRA_RESULTS);
            // 語音識別會有多個結果，第一個是最精確的
            String text = matches.get(0);
            Logs.showTrace("speech: " + text);
            ((TextView)findViewById(R.id.textViewSpeech)).setText(text);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
