package com.programerworld.project1;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class timepray extends AppCompatActivity {

    private static final int IMAGE_CHANGE_INTERVAL = 4000; // فاصل زمني لتغيير الصور بالميلي ثانية (4 ثوانٍ)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepray);

        // الوقت
        final TextView textView = findViewById(R.id.time);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String currentTime = sdf.format(new Date());
                textView.setText(currentTime);
                handler.postDelayed(this, 1000); // تحديث كل ثانية
            }
        });

        // اليوم، الشهر، السنة
        final TextView textViewDate = findViewById(R.id.Date);
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US);
        String currentDate = sdfDate.format(new Date());
        textViewDate.setText(currentDate);

        // نص الرسوم المتحركة
        final TextView infoTextView = findViewById(R.id.infoTextView);

        // إعداد ViewPager
        int[] images = {R.drawable.b, R.drawable.b9, R.drawable.b5}; // الصور الخاصة بك
        final ViewPager viewPager = findViewById(R.id.viewPager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(this, images);
        viewPager.setAdapter(adapter);

        // تغيير الصور كل 4 ثوانٍ
        final Handler imageHandler = new Handler();
        imageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % adapter.getCount();
                viewPager.setCurrentItem(nextItem, true);
                imageHandler.postDelayed(this, IMAGE_CHANGE_INTERVAL);
            }
        }, IMAGE_CHANGE_INTERVAL);

        // تحريك النص من اليمين إلى اليسار
        Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        final AnimationSet animationSetLeft = new AnimationSet(true);
        animationSetLeft.addAnimation(slideLeft);

        // تحريك النص من اليسار إلى اليمين
        Animation slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        final AnimationSet animationSetRight = new AnimationSet(true);
        animationSetRight.addAnimation(slideRight);

        // AnimationListener للتبديل بين حركتي النص
        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                infoTextView.startAnimation(animationSetRight); // تبدأ الحركة الثانية بعد الانتهاء من الأولى
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        slideRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                infoTextView.startAnimation(animationSetLeft); // تبدأ الحركة الأولى بعد الانتهاء من الثانية
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // تشغيل الحركة الأولى
        infoTextView.startAnimation(animationSetLeft);
    }

    private static class ViewPagerAdapter extends PagerAdapter {

        private final Context context;
        private final int[] images;

        public ViewPagerAdapter(Context context, int[] images) {
            this.context = context;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.viewpager_item, container, false);

            ImageView imageView = itemView.findViewById(R.id.imageView);
            imageView.setImageResource(images[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}

