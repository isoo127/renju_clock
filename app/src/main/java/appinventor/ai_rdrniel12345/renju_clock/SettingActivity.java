package appinventor.ai_rdrniel12345.renju_clock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    Spinner mode;
    String[] modes = {"기본","초읽기","피셔"};
    private int mode_int;

    NumberPicker hour1;
    NumberPicker min1;
    NumberPicker sec1;
    NumberPicker hour2;
    NumberPicker min2;
    NumberPicker sec2;
    NumberPicker ctdw_time;
    NumberPicker ctdw_sec;
    NumberPicker fischer_sec;

    TextView ctdw_text;
    TextView ctdw_time_text;
    TextView ctdw_sec_text;

    TextView fis_text;
    TextView fis_text_sec;

    SharedPreferences sf;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        sf = getSharedPreferences("Data", Activity.MODE_PRIVATE);
        editor = sf.edit();

        hour1 = findViewById(R.id.hour_picker1);
        min1 = findViewById(R.id.min_picker1);
        sec1 = findViewById(R.id.sec_picker1);
        hour2 = findViewById(R.id.hour_picker2);
        min2 = findViewById(R.id.min_picker2);
        sec2 = findViewById(R.id.sec_picker2);
        ctdw_time = findViewById(R.id.countdown_time);
        ctdw_sec = findViewById(R.id.countdown_sec);
        fischer_sec = findViewById(R.id.fischer_sec);
        mode = findViewById(R.id.selectMode);

        ctdw_text = findViewById(R.id.countdown_text);
        ctdw_time_text = findViewById(R.id.countdown_text_time);
        ctdw_sec_text = findViewById(R.id.countdown_text_sec);

        fis_text = findViewById(R.id.fischer_text);
        fis_text_sec = findViewById(R.id.fischer_text_sec);

        fullscreen();
        setMode();
        cancel();
        setNumberPickers();
        setData();
        apply();
    }

    private void fullscreen(){
        // for fullscreen
        View decorView = getWindow().getDecorView();
        // for fullscreen
        int uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOption);
    }

    private void cancel() {
        Button cancel = findViewById(R.id.cancelButton);
        cancel.setOnClickListener(v -> finish());
    }

    private void setMode() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode.setAdapter(adapter);
        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //@RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
                if(position == 0){ // 기본
                    mode0();
                }
                else if(position == 1) { // 초읽기
                    mode1();
                }
                else if(position == 2) { // 피셔
                    mode2();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setNumberPickers() {
        hour1.setMaxValue(9);
        hour1.setMinValue(0);
        min1.setMaxValue(59);
        min1.setMinValue(0);
        sec1.setMaxValue(59);
        sec1.setMinValue(0);
        hour2.setMaxValue(9);
        hour2.setMinValue(0);
        min2.setMaxValue(59);
        min2.setMinValue(0);
        sec2.setMaxValue(59);
        sec2.setMinValue(0);
        ctdw_time.setMinValue(1);
        ctdw_time.setMaxValue(10);
        ctdw_sec.setMaxValue(60);
        ctdw_sec.setMinValue(1);
        fischer_sec.setMinValue(1);
        fischer_sec.setMaxValue(60);
    }

    private void apply() {
        Button apply = findViewById(R.id.applyButton);

        apply.setOnClickListener(v -> {
            saveData();
            sendData();
        });
    }

    private void sendData() {
        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        intent.putExtra("hour1",hour1.getValue());
        intent.putExtra("hour2",hour2.getValue());
        intent.putExtra("min1",min1.getValue());
        intent.putExtra("min2",min2.getValue());
        intent.putExtra("sec1",sec1.getValue());
        intent.putExtra("sec2",sec2.getValue());
        if(mode_int == 1) {
            intent.putExtra("ctdw_time",ctdw_time.getValue());
            intent.putExtra("ctdw_sec",ctdw_sec.getValue());
        }
        else if(mode_int == 2) intent.putExtra("fischer_sec",fischer_sec.getValue());
        intent.putExtra("mode",mode_int);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void saveData() {
        editor.putInt("hour1", hour1.getValue());
        editor.putInt("hour2", hour2.getValue());
        editor.putInt("min1", min1.getValue());
        editor.putInt("min2", min2.getValue());
        editor.putInt("sec1", sec1.getValue());
        editor.putInt("sec2", sec2.getValue());
        editor.putInt("ctdw_time", ctdw_time.getValue());
        editor.putInt("ctdw_sec", ctdw_sec.getValue());
        editor.putInt("fischer_sec", fischer_sec.getValue());
        editor.putInt("mode",mode_int);
        editor.apply();
    }

    private void setData() {
        hour1.setValue(sf.getInt("hour1",0));
        hour2.setValue(sf.getInt("hour2",0));
        min1.setValue(sf.getInt("min1",30));
        min2.setValue(sf.getInt("min2",30));
        sec1.setValue(sf.getInt("sec1",0));
        sec2.setValue(sf.getInt("sec2",0));

        if(sf.getInt("mode",0) == 0){
            mode.setSelection(0);
        }
        else if(sf.getInt("mode",1) == 1){
            mode.setSelection(1);
        }
        else if(sf.getInt("mode",2) == 2){
            mode.setSelection(2);
        }
        ctdw_time.setValue(sf.getInt("ctdw_time",1));
        ctdw_sec.setValue(sf.getInt("ctdw_sec",1));
        fischer_sec.setValue(sf.getInt("fischer_sec",1));
    }

    //@RequiresApi(api = Build.VERSION_CODES.Q)
    private void mode0() {
        ctdw_text.setTextColor(Color.parseColor("#929090"));
        ctdw_time_text.setTextColor(Color.parseColor("#929090"));
        ctdw_sec_text.setTextColor(Color.parseColor("#929090"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ctdw_time.setTextColor(Color.parseColor("#929090"));
            ctdw_sec.setTextColor(Color.parseColor("#929090"));
            fischer_sec.setTextColor(Color.parseColor("#929090"));
        }

        ctdw_time.setEnabled(false);
        ctdw_sec.setEnabled(false);

        fis_text.setTextColor(Color.parseColor("#929090"));
        fis_text_sec.setTextColor(Color.parseColor("#929090"));

        fischer_sec.setEnabled(false);
        mode_int = 0;
    }

    private void mode1() {
        ctdw_text.setTextColor(Color.parseColor("#000000"));
        ctdw_time_text.setTextColor(Color.parseColor("#000000"));
        ctdw_sec_text.setTextColor(Color.parseColor("#000000"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ctdw_time.setTextColor(Color.parseColor("#000000"));
            ctdw_sec.setTextColor(Color.parseColor("#000000"));
            fischer_sec.setTextColor(Color.parseColor("#929090"));
        }

        ctdw_time.setEnabled(true);
        ctdw_sec.setEnabled(true);

        fis_text.setTextColor(Color.parseColor("#929090"));
        fis_text_sec.setTextColor(Color.parseColor("#929090"));

        fischer_sec.setEnabled(false);
        mode_int = 1;
    }

    private void mode2() {
        ctdw_text.setTextColor(Color.parseColor("#929090"));
        ctdw_time_text.setTextColor(Color.parseColor("#929090"));
        ctdw_sec_text.setTextColor(Color.parseColor("#929090"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ctdw_time.setTextColor(Color.parseColor("#929090"));
            ctdw_sec.setTextColor(Color.parseColor("#929090"));
            fischer_sec.setTextColor(Color.parseColor("#000000"));
        }

        ctdw_time.setEnabled(false);
        ctdw_sec.setEnabled(false);

        fis_text.setTextColor(Color.parseColor("#000000"));
        fis_text_sec.setTextColor(Color.parseColor("#000000"));

        fischer_sec.setEnabled(true);
        mode_int = 2;
    }

    public void markClicked(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

        builder.setTitle("제작자");
        builder.setMessage("한국오목협회 소속 강상민 기사 개발\n\nEmail : isooksm1207@gmail.com");
        builder.show();
    }
}