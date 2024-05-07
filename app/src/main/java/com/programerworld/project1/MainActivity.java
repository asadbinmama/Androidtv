package com.programerworld.project1;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.time);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                textView.setText(currentTime);
                handler.postDelayed(this, 0); // อัปเดตทุกๆ 1 วินาที
            }
        });

        // ใน onCreate() หรือที่ต้องการใช้งาน Animation
        TextView infoTextView = findViewById(R.id.infoTextView);

// สร้าง Animation โดยกำหนดตำแหน่งปัจจุบันและตำแหน่งปลายทางของข้อความ
        TranslateAnimation animationRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        animationRight.setDuration(5000); // ระยะเวลา 5 วินาที

        TranslateAnimation animationLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        animationLeft.setDuration(5000); // ระยะเวลา 5 วินาที

// สร้าง AnimationSet เพื่อรวม Animation และกำหนดคุณสมบัติให้เลื่อนต่อเนื่อง
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animationRight);
        animationSet.addAnimation(animationLeft);
        animationSet.setRepeatCount(Animation.INFINITE); // เล่น Animation อย่างต่อเนื่อง
// เริ่ม Animation
        infoTextView.startAnimation(animationSet);
    }
}
