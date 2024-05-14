package com.programerworld.project1;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class timepray extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepray);

        //time
        final TextView textView = findViewById(R.id.time);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String currentTime = sdf.format(new Date());
                textView.setText(currentTime);
                handler.postDelayed(this, 0); // อัปเดตทุกๆ 1 วินาที
            }
        });

        //day month year
        final TextView textViewDate = findViewById(R.id.Date);
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US);
        String currentDate = sdfDate.format(new Date());
        textViewDate.setText(currentDate);

        //animation text
        TextView infoTextView = findViewById(R.id.infoTextView);

        Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);

        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(slideLeft);

        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                infoTextView.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        infoTextView.startAnimation(animationSet);



    }
}
