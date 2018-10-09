package org.iii.wheelpiedemo.course;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.iii.wheelpiedemo.R;
import org.iii.wheelpiedemo.sample.VideoPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;

public class CourseActivity extends AppCompatActivity {

    LinearLayout contentLayout;
    long videoDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        String apiResponse = "{\"planDayView\":{\"id\":355,\"dayTraining\":{\"id\":32261,\"done\":false,\"action\":\"E30(HRR60%)+2ST\",\"description\":\"E心率區間60%跑30分鐘\\n快步跑2組\",\"contents\":[{\"title\":\"靜態熱身\",\"steps\":[\"上半身熱身操5分鐘\",\"下半身熱身操5分鐘\"]},{\"title\":\"第1階段訓練\",\"steps\":[\"輕鬆跑(維持儲備心律60%)30分鐘\"],\"hrrChartInfo\":{\"chart\":{\"type\":\"block\"},\"title\":{},\"subtitle\":{},\"xAxis\":{\"text\":\"時間(分鐘)\",\"tickInterval\":10},\"yAxis\":{\"text\":\"HRR心率(%)\",\"tickInterval\":20},\"series\":{\"data\":[{\"xStart\":0,\"xEnd\":30,\"yStart\":59,\"yEnd\":74,\"color\":\"#55FFFF\"}]}}},{\"title\":\"第2階段訓練\",\"steps\":[\"快步跑(維持步頻180)10秒鐘\",\"靜/動態休息45秒鐘\",\"快步跑(維持步頻180)10秒鐘\"],\"strideChartInfo\":{\"chart\":{},\"title\":{\"text\":\"第2階段訓練\"},\"subtitle\":{\"text\":\"\"},\"xAxis\":{\"text\":\"時間(sec)\"},\"yAxis\":{\"text\":\"步頻(spm)\"},\"series\":{\"data\":[{\"xStart\":0,\"xEnd\":10,\"yStart\":160,\"yEnd\":200},{\"xStart\":10,\"xEnd\":55,\"yStart\":0,\"yEnd\":160},{\"xStart\":55,\"xEnd\":65,\"yStart\":160,\"yEnd\":200}]}}},{\"title\":\"靜態收操\",\"steps\":[\"上半身收操5分鐘\",\"下半身收操5分鐘\"]}],\"trainable\":true,\"dayInfo\":\"輕鬆跑(DAY1)\",\"classInfo\":{\"code\":\"EZ00001\",\"contents\":[{\"text\":\"\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"跑前的暖身這是為了提高體溫、提升心跳、並增加關節與肌肉的活動範圍，如此能降低受傷的風險，且跑起步來也會更舒適流暢。暖身首部曲應該從腳踝、膝蓋、髖部、腰、肩、手腕、頸部依序進行，接著以快走或小跑步的方式讓身體熱起來，感覺到身體微微出汗即可\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=Ts5A0-n5-J8\",\"image\":\"\",\"description\":\"關節操\"},{\"text\":\"\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"輕鬆跑訓練是有氧耐力訓練的有效方法之一，體感是可以聊天且舒服的速度，如果你覺得喘不過氣來，就代表你的心率太高的，此時請放慢你的速度。\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=7LCgUMsod1Q&feature=youtu.be\",\"image\":\"\",\"description\":\"Eazy Running HRR60%\"},{\"text\":\"快步跑是指在不耗盡體力的情況下提升你的整體速度並消除E配速與LSD的副作用。快步跑的重點是要加快你的「步頻」，再來才是腳掌上拉的「幅度」，如果前兩項都能做到再來才要求「速度」。也就是說速度並非快步跑的重點，步頻才是，跑此項目時，不要跨大步，不要管速度，甚至原地跑也可以。\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=t0ufhZ8AULI&feature=youtu.be\",\"image\":\"\",\"description\":\"ST\"},{\"text\":\"\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"將主運動強度減緩，讓代謝恢復正常，同時排除運動時產生的代謝廢物，冷卻與再伸展合稱為「收操」。在這個逐漸放慢節奏的過程中，運動所產生的代謝廢物也會逐漸消散，原本沉重的雙腿會漸感輕鬆，不若剛練習完的當下那麼緊繃。\",\"url\":\"\",\"image\":\"\",\"description\":\"\"},{\"text\":\"\",\"url\":\"https://www.youtube.com/watch?v=dxDeO-5KT9o&feature=youtu.be\",\"image\":\"\",\"description\":\"收操\"}]}}},\"result\":true}";
        initViewByAPIResponse(apiResponse);
    }

    private void initViewByAPIResponse(String apiResponse) {
        contentLayout = findViewById(R.id.content_layout);
        try {
            JSONObject jsonResp = new JSONObject(apiResponse);
            JSONObject dayView = jsonResp.getJSONObject("planDayView");
            JSONObject dayTraining = dayView.getJSONObject("dayTraining");
            // 取出訓練課程內容
            JSONArray trainingContents = dayTraining.getJSONArray("contents");
            for (int i=0;i<trainingContents.length();i+=1) {
                JSONObject content = trainingContents.getJSONObject(i);
                // 取出title
                String titleText = content.getString("title");
                TextView title = createTitle(titleText);
                contentLayout.addView(title);
                // 取出steps
                JSONArray steps = content.getJSONArray("steps");
                for(int stepIdx=0; stepIdx<steps.length(); stepIdx+=1) {
                    TextView step = createStep(steps.getString(stepIdx));
                    contentLayout.addView(step);
                }
                // Insert熱身影片
                if (titleText instanceof String && titleText.endsWith("熱身")) {
//                    RelativeLayout videoRL = new RelativeLayout(this);
//                    setViewLayout(contentLayout, videoRL,
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            200);
//                    VideoView v = createVideo(
//                            "v_warm_up",
//                            "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
//                    );
//                    setViewLayout(videoRL, v,
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                    );
                    VideoPlayer video = new VideoPlayer(this);
                    video.showController(true);
                    video.setVideo(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
                    setViewLayout(contentLayout, video, ViewGroup.LayoutParams.MATCH_PARENT, 200);
                    setMargins(video, 0, 10, 0, 10);
                }
                // 取出圖表
                JSONObject chart = getChartInfo(content);
                // 處理圖表呈現
                if (chart != null) {
                    BubbleChartView chartView = createBubbleChart();
                    setViewLayout(contentLayout, chartView, ViewGroup.LayoutParams.MATCH_PARENT, 250);
                    CourseChart info = extractChartInfo(chart);
                    updateBubbleChart(chartView, info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CourseChart {
        private String xAxisTitle;
        private String yAxisTitle;
        public ArrayList<SquareBlock> data = new ArrayList<SquareBlock>();

        public CourseChart(JSONObject chart) {
            if (chart != null) {
                // 取得x軸名稱
                try {
                    JSONObject xAxis = chart.getJSONObject("xAxis");
                    this.xAxisTitle = xAxis.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 取得y軸名稱
                try {
                    JSONObject xAxis = chart.getJSONObject("yAxis");
                    this.yAxisTitle = xAxis.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 取得data
                try {
                    JSONObject series = chart.getJSONObject("series");
                    JSONArray chartData = series.getJSONArray("data");
                    for (int i=0; i< chartData.length(); i+=1) {
                        data.add(new SquareBlock(chartData.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getxAxisTitle() {
            return xAxisTitle;
        }

        public String getyAxisTitle() {
            return yAxisTitle;
        }
    }

    private class SquareBlock {
        private int xStart;
        private int xEnd;
        private int yStart;
        private int yEnd;
        private int x;
        private int y;
        private int z;

        public SquareBlock (JSONObject block) {
            if (block != null) {
                try {
                    xStart = block.getInt("xStart");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    xEnd = block.getInt("xEnd");
                    x = xStart + (xEnd - xStart )/2;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    yStart = block.getInt("yStart");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    yEnd = block.getInt("yEnd");
                    y = yStart + (yEnd - yStart )/2;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private CourseChart extractChartInfo (JSONObject chart) {
        return new CourseChart(chart);
    }

    private void updateBubbleChart(BubbleChartView chart, CourseChart chartInfo){
        int BUBBLES_NUM = chartInfo != null ? chartInfo.data.size() : 0;

        BubbleChartData data;
        boolean hasAxes = true;
        boolean hasAxesNames = true;
        ValueShape shape = ValueShape.SQUARE;
        boolean hasLabels = false;
        boolean hasLabelForSelected = false;


        List<BubbleValue> values = new ArrayList<BubbleValue>();
        //for (int i = 0; i < chartInfo.data.size(); ++i) {
        for (SquareBlock sb : chartInfo.data) {
            //BubbleValue value = new BubbleValue(i, (float) Math.random() * 100, (float) Math.random() * 1000);
            BubbleValue value = new BubbleValue(sb.x, sb.y, 50);
            value.setColor(ChartUtils.pickColor());
            value.setShape(shape);
            values.add(value);
        }

        data = new BubbleChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName(chartInfo.getxAxisTitle());
                axisY.setName(chartInfo.getyAxisTitle());
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setBubbleChartData(data);
    }

    private BubbleChartView createBubbleChart() {
        BubbleChartView chart = new BubbleChartView(this);

//        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
//        chart.setScrollEnabled(true);
        Viewport viewport = new Viewport(chart.getMaximumViewport());
        viewport.top = 220;
        viewport.bottom = 0;
//        viewport.right = 10;
        viewport.left = 0;
        chart.setMaximumViewport(viewport);
        chart.setCurrentViewport(viewport);

        return chart;
    }

    private JSONObject getChartInfo (JSONObject trainingContent) {
        JSONObject chart = null;
        if (trainingContent != null) {
            try {
                chart = trainingContent.getJSONObject("hrrChartInfo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (chart == null) {
                try {
                    chart = trainingContent.getJSONObject("strideChartInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return chart;
    }

    private TextView createTitle(String text) {
        //return this.createTitle(text, "@style/title");
        return this.createTextView(text, "courseTitle");
    }
    private TextView createStep(String text) {

        //return this.createTitle(text, "@style/step");
        return this.createTextView(text, "courseStep");
    }

    private TextView createTextView(String text, String style) {
        int resId = getResources().getIdentifier(style, "style", getPackageName());
        TextView title = new TextView(this, null, 0, resId);
        title.setText(text);
        return title;
    }

    private VideoView createVideo(String id, String videoURI) {
        final VideoView vid = new VideoView(this);

        MediaController m = new MediaController(this);
        vid.setMediaController(m);

        try {
            final Uri u = Uri.parse(videoURI);
            vid.setVideoURI(u);
            vid.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // Your code goes here
                    Log.d("Dbg", "OnErrorListener: onError: " + what + ", " + extra);
                    return true;
                }
            });
            vid.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    VideoView vid = (VideoView)v;

                    if (!vid.isPlaying()) {
                        vid.requestFocus();
                        vid.start();
                    } else {
                        vid.pause();
                    }
                    return false;
                }
            });

            vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //vid.stopPlayback(); //不可呼叫，不然會無法再法再度播放
                    //vid.seekTo(0);
                    //vid.setVideoURI(u); //爛方法，不設定的話，touch event的restart不會work
                    Toast.makeText(getApplicationContext(), "影片播放完畢...", Toast.LENGTH_LONG).show();
                }
            });

            vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoDuration = vid.getDuration();
                }
            });
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return vid;
    }

    private void setViewLayout(ViewGroup parent, View v, int widthDP, int heightDP) {
        if (parent !=null && v != null) {
            //
            parent.addView(v);
            v.getLayoutParams().width = convertDPtoPx(widthDP);
            v.getLayoutParams().height = convertDPtoPx(heightDP);
        }
    }

    private void setMargins(View v, int left, int top, int right, int buttom) {
        if (v instanceof ViewGroup) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.setMargins(
                convertDPtoPx(left),
                convertDPtoPx(top),
                convertDPtoPx(right),
                convertDPtoPx(buttom)
            );
            v.requestLayout();
        }
    }

    private int convertDPtoPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);  // replace 100 with your dimensions
        return px;
    }

    private int getStyleResourceId(String name) {
        return getResourceId(name, "style");
    }

    private int getResourceId (String name, String defType) {
        return getResources().getIdentifier(name, defType, getPackageName());
    }
}
