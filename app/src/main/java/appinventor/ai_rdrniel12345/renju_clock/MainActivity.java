package appinventor.ai_rdrniel12345.renju_clock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final int BASIC = 0;
    private final int COUNTDOWN = 1;
    private final int FISCHER = 2;

    private int bg1;
    private int bg2;

    private boolean ctdw_mode1;
    private boolean ctdw_mode2;

    Thread thread1;
    Thread thread2;

    private boolean isRunning1;
    private boolean isRunning2;

    private boolean firstClick1;
    private boolean firstClick2;

    private int time_all_1; // 0.1초 단위
    private int time_all_2; // 0.1초 단위
    private int mode;
    private int ctdw_time1;
    private int ctdw_time2;
    private int ctdw_sec1;
    private int ctdw_sec2;
    private int fischer_sec1;
    private int fischer_sec2;

    TextView player1_time;
    TextView player2_time;
    TextView player1_mode;
    TextView player2_mode;

    SharedPreferences sf;
    SharedPreferences.Editor editor;

    ImageButton pause;

    ConstraintLayout time1;
    ConstraintLayout time2;

    SoundPool soundPool;
    private int clickSound;
    private int warningSound;
    private int ctdw0Sound;
    private int ctdw1Sound;
    private int ctdw2Sound;
    private int ctdw3Sound;
    private int ctdw4Sound;
    private int ctdw5Sound;
    private int ctdw6Sound;
    private int ctdw7Sound;
    private int ctdw8Sound;
    private int ctdw9Sound;
    private int timeoverSound;

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    fullscreen();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            time_all_1 = data.getIntExtra("hour1", -1) * 36000 + data.getIntExtra("min1", -1) * 600 + data.getIntExtra("sec1", -1) * 10;
                            time_all_2 = data.getIntExtra("hour2", -1) * 36000 + data.getIntExtra("min2", -1) * 600 + data.getIntExtra("sec2", -1) * 10;
                        }
                        time1_show();
                        time2_show();
                        if (data != null) {
                            if (data.getIntExtra("mode", -1) == BASIC) {
                                mode = BASIC;
                            } else if (data.getIntExtra("mode", -1) == COUNTDOWN) {
                                ctdw_time1 = data.getIntExtra("ctdw_time", -1);
                                ctdw_time2 = data.getIntExtra("ctdw_time", -1);
                                ctdw_sec1 = data.getIntExtra("ctdw_sec", -1);
                                ctdw_sec2 = data.getIntExtra("ctdw_sec", -1);
                                mode = COUNTDOWN;
                            } else if (data.getIntExtra("mode", -1) == FISCHER) {
                                fischer_sec1 = data.getIntExtra("fischer_sec", -1);
                                fischer_sec2 = data.getIntExtra("fischer_sec", -1);
                                mode = FISCHER;
                            }
                        }
                        mode1_show();
                        mode2_show();
                        saveData();
                        time1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_disabled));
                        time2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_disabled));
                        bg1 = 0;
                        bg2 = 0;
                        time1.setEnabled(true);
                        time2.setEnabled(true);
                        ctdw_mode1 = false;
                        ctdw_mode2 = false;
                    }
                }
            }
    );

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 꺼짐 방지
        fullscreen();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        clickSound = soundPool.load(this, R.raw.click_sound, 1);
        warningSound = soundPool.load(this, R.raw.warning, 1);
        ctdw0Sound = soundPool.load(this, R.raw.ctdw0, 1);
        ctdw1Sound = soundPool.load(this, R.raw.ctdw1, 1);
        ctdw2Sound = soundPool.load(this, R.raw.ctdw2, 1);
        ctdw3Sound = soundPool.load(this, R.raw.ctdw3, 1);
        ctdw4Sound = soundPool.load(this, R.raw.ctdw4, 1);
        ctdw5Sound = soundPool.load(this, R.raw.ctdw5, 1);
        ctdw6Sound = soundPool.load(this, R.raw.ctdw6, 1);
        ctdw7Sound = soundPool.load(this, R.raw.ctdw7, 1);
        ctdw8Sound = soundPool.load(this, R.raw.ctdw8, 1);
        ctdw9Sound = soundPool.load(this, R.raw.ctdw9, 1);
        timeoverSound = soundPool.load(this, R.raw.timeover, 1);

        firstClick1 = true;
        firstClick2 = true;

        ctdw_mode1 = false;
        ctdw_mode2 = false;

        bg1 = 0;
        bg2 = 0;

        pause = findViewById(R.id.pauseButton);

        time1 = findViewById(R.id.player1_container);
        time2 = findViewById(R.id.player2_container);

        sf = getSharedPreferences("MainData", Activity.MODE_PRIVATE);
        editor = sf.edit();

        player1_time = findViewById(R.id.player1_time);
        player2_time = findViewById(R.id.player2_time);
        player1_mode = findViewById(R.id.player1_mode);
        player2_mode = findViewById(R.id.player2_mode);

        setting();
        reload();
        setData();
    }

    private void fullscreen() {
        // for fullscreen
        View decorView = getWindow().getDecorView();
        // for fullscreen
        int uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOption);
    }

    private void setting() {
        ImageButton setting = findViewById(R.id.setButton);
        setting.setOnClickListener(v -> {
            if (time_all_1 > 0 && time_all_2 > 0) {
                pause();
            }
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivityResult.launch(intent);
        });
    }

    private String time_string(long time_all) {
        long hour, min, sec;
        long temp;
        hour = time_all / 36000;
        temp = time_all % 36000;
        min = temp / 600;
        temp = temp % 600;
        if (temp % 10 == 0) {
            sec = temp / 10;
        } else if (temp / 10 == 59) {
            sec = 0;
            if (min != 59) {
                min = (time_all % 36000) / 600 + 1;
            } else {
                min = 0;
                hour = hour + 1;
            }
        } else {
            if (time_all >= 0) sec = temp / 10 + 1;
            else sec = 0;
        }

        if (min >= 10 && sec >= 10) {
            return hour + ":" + min + ":" + sec;
        } else if (min < 10 && sec >= 10) {
            return hour + ":0" + min + ":" + sec;
        } else if (min < 10) {
            return hour + ":0" + min + ":0" + sec;
        } else {
            return hour + ":" + min + ":0" + sec;
        }
    }

    private void time1_show() {
        player1_time.setText(time_string(time_all_1));
    }

    private void time2_show() {
        player2_time.setText(time_string(time_all_2));
    }

    private String ctdw_string(int ctdw_time, int ctdw_sec) {
        return ctdw_time + "회, " + ctdw_sec + "초";
    }

    private String fischer_string(int fischer_sec) {
        return "+" + fischer_sec + "초";
    }

    private void mode1_show() {
        if (mode == BASIC) {
            player1_mode.setText("");
        } else if (mode == COUNTDOWN) {
            player1_mode.setText(ctdw_string(ctdw_time1, ctdw_sec1));
        } else if (mode == FISCHER) {
            player1_mode.setText(fischer_string(fischer_sec1));
        }
    }

    private void mode2_show() {
        if (mode == BASIC) {
            player2_mode.setText("");
        } else if (mode == COUNTDOWN) {
            player2_mode.setText(ctdw_string(ctdw_time2, ctdw_sec2));
        } else if (mode == FISCHER) {
            player2_mode.setText(fischer_string(fischer_sec2));
        }
    }

    private void saveData() {
        editor.putInt("time_all_1", time_all_1);
        editor.putInt("time_all_2", time_all_2);
        editor.putInt("ctdw_time1", ctdw_time1);
        editor.putInt("ctdw_time2", ctdw_time2);
        editor.putInt("ctdw_sec1", ctdw_sec1);
        editor.putInt("ctdw_sec2", ctdw_sec2);
        editor.putInt("fischer_sec1", fischer_sec1);
        editor.putInt("fischer_sec2", fischer_sec2);
        editor.putInt("mode", mode);
        editor.apply();
    }

    private void setData() {
        time_all_1 = sf.getInt("time_all_1", 18000);
        time_all_2 = sf.getInt("time_all_2", 18000);
        ctdw_time1 = sf.getInt("ctdw_time1", 0);
        ctdw_time2 = sf.getInt("ctdw_time2", 0);
        ctdw_sec1 = sf.getInt("ctdw_sec1", 0);
        ctdw_sec2 = sf.getInt("ctdw_sec2", 0);
        fischer_sec1 = sf.getInt("fischer_sec1", 0);
        fischer_sec2 = sf.getInt("fischer_sec2", 0);
        mode = sf.getInt("mode", 0);
        time1_show();
        time2_show();
        mode1_show();
        mode2_show();
    }

    private void reload() {
        ImageButton reload = findViewById(R.id.reloadButton);
        reload.setOnClickListener(v -> {
            pause();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("초기화");
            builder.setMessage("정말로 초기화 하시겠습니까?");

            builder.setPositiveButton("확인", (dialogInterface, i) -> {
                ctdw_mode1 = false;
                ctdw_mode2 = false;
                setData();
                fullscreen();
            });

            builder.setNegativeButton("취소", (dialogInterface, i) -> fullscreen());

            builder.show();
        });
    }

    Handler handler1 = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            time1_show();
            mode1_show();
        }
    };

    public class timeThread1 implements Runnable {
        @Override
        public void run() {
            while (true) {
                while (isRunning1) {
                    if (time_all_1 / 10 <= 10 && time_all_1 % 10 == 0 && time_all_1 != 0) {
                        soundPool.play(warningSound, 1f, 1f, 0, 0, 1f);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {

                        });
                        return;
                    }
                    Message msg = new Message();
                    msg.arg1 = time_all_1--;
                    handler1.sendMessage(msg);
                    if (time_all_1 <= 0 && mode != COUNTDOWN) {
                        time1.setEnabled(false);
                        time2.setEnabled(false);
                        firstClick1 = true;
                        firstClick2 = true;
                        pause.setVisibility(View.INVISIBLE);
                        time1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_timeout));
                        thread1.interrupt();
                        if (thread2 != null) thread2.interrupt();
                        soundPool.play(timeoverSound, 1f, 1f, 0, 0, 1f);
                        break;
                    } else if (time_all_1 <= 0 && ctdw_time1 != 0) {
                        ctdw_mode1 = true;
                        time_all_1 = ctdw_sec1 * 10;
                        ctdw_time1--;
                        voice(ctdw_time1);
                    } else if (time_all_1 <= 0) {
                        time1.setEnabled(false);
                        time2.setEnabled(false);
                        firstClick1 = true;
                        firstClick2 = true;
                        pause.setVisibility(View.INVISIBLE);
                        time1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_timeout));
                        thread1.interrupt();
                        if (thread2 != null) thread2.interrupt();
                        soundPool.play(timeoverSound, 1f, 1f, 0, 0, 1f);
                        break;
                    }
                }
            }
        }
    }

    Handler handler2 = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            time2_show();
            mode2_show();
        }
    };

    public class timeThread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                while (isRunning2) {
                    if (time_all_2 / 10 <= 10 && time_all_2 % 10 == 0 && time_all_2 != 0) {
                        soundPool.play(warningSound, 1f, 1f, 0, 0, 1f);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {

                        });
                        return;
                    }
                    Message msg = new Message();
                    msg.arg1 = time_all_2--;
                    handler2.sendMessage(msg);
                    if (time_all_2 <= 0 && mode != COUNTDOWN) {
                        time1.setEnabled(false);
                        time2.setEnabled(false);
                        firstClick1 = true;
                        firstClick2 = true;
                        pause.setVisibility(View.INVISIBLE);
                        time2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_timeout));
                        if (thread1 != null) thread1.interrupt();
                        thread2.interrupt();
                        soundPool.play(timeoverSound, 1f, 1f, 0, 0, 1f);
                        break;
                    } else if (time_all_2 <= 0 && ctdw_time2 != 0) {
                        ctdw_mode2 = true;
                        time_all_2 = ctdw_sec2 * 10;
                        ctdw_time2--;
                        voice(ctdw_time2);
                    } else if (time_all_2 <= 0) {
                        time1.setEnabled(false);
                        time2.setEnabled(false);
                        firstClick1 = true;
                        firstClick2 = true;
                        pause.setVisibility(View.INVISIBLE);
                        time2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_timeout));
                        if (thread1 != null) thread1.interrupt();
                        thread2.interrupt();
                        soundPool.play(timeoverSound, 1f, 1f, 0, 0, 1f);
                        break;
                    }
                }
            }
        }
    }

    public void time2Clicked(View v) {
        soundPool.play(clickSound, 1f, 1f, 0, 0, 1f);
        if (ctdw_mode2) {
            time_all_2 = ctdw_sec2 * 10;
        }
        if (mode == FISCHER && bg2 == 1) {
            time_all_2 = time_all_2 + fischer_sec2 * 10;
        }
        pause.setVisibility(View.VISIBLE);
        time2.setBackground(ContextCompat.getDrawable(this, R.drawable.view_disabled));
        time1.setBackground(ContextCompat.getDrawable(this, R.drawable.view_enabled));
        bg1 = 1;
        bg2 = 0;
        time2.setEnabled(false);
        time1.setEnabled(true);
        isRunning1 = true;
        isRunning2 = false;
        if (firstClick2) {
            thread1 = new Thread(new timeThread1());
            thread1.start();
            firstClick2 = false;
        }
    }

    public void time1Clicked(View v) {
        soundPool.play(clickSound, 1f, 1f, 0, 0, 1f);
        if (ctdw_mode1) {
            time_all_1 = ctdw_sec1 * 10;
        }
        if (mode == FISCHER && bg1 == 1) {
            time_all_1 = time_all_1 + fischer_sec1 * 10;
        }
        pause.setVisibility(View.VISIBLE);
        time1.setBackground(ContextCompat.getDrawable(this, R.drawable.view_disabled));
        time2.setBackground(ContextCompat.getDrawable(this, R.drawable.view_enabled));
        bg1 = 0;
        bg2 = 1;
        time1.setEnabled(false);
        time2.setEnabled(true);
        isRunning2 = true;
        isRunning1 = false;
        if (firstClick1) {
            thread2 = new Thread(new timeThread2());
            thread2.start();
            firstClick1 = false;
        }
    }

    public void pauseButtonClicked(View v) {
        pause();
    }

    private void pause() {
        time1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_disabled));
        time2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_disabled));
        pause.setVisibility(View.INVISIBLE);
        isRunning1 = false;
        isRunning2 = false;
        time1.setEnabled(true);
        time2.setEnabled(true);
        bg1 = 0;
        bg2 = 0;
    }

    private void voice(int ctdw_time) {
        if (ctdw_time == 0) soundPool.play(ctdw0Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 1) soundPool.play(ctdw1Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 2) soundPool.play(ctdw2Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 3) soundPool.play(ctdw3Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 4) soundPool.play(ctdw4Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 5) soundPool.play(ctdw5Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 6) soundPool.play(ctdw6Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 7) soundPool.play(ctdw7Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 8) soundPool.play(ctdw8Sound, 1f, 1f, 0, 0, 1f);
        else if (ctdw_time == 9) soundPool.play(ctdw9Sound, 1f, 1f, 0, 0, 1f);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        pause();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("종료");
        builder.setMessage("정말로 종료하시겠습니까?");

        builder.setPositiveButton("확인", (dialogInterface, i) -> finish());

        builder.setNegativeButton("취소", (dialogInterface, i) -> fullscreen());

        builder.show();
    }
}