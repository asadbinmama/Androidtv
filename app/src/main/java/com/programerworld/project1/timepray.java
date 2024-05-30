package com.programerworld.project1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class timepray extends AppCompatActivity {

    private static final int IMAGE_CHANGE_INTERVAL = 5000; // Image change interval in milliseconds (5 seconds)
    private String[] texts = {
            "             มัสยิดหน้าควน นูรุดดีน เปิดรับสมัครนักเรียนใหม่ ปีการศึกษา 2567 เรียนอัลกุรอานภาคค่ำ หลักสูตรกีรออาตี เวลา 18.30 - 20.00 น. รับสมัครตั้งแต่อายุ 6 ปีขึ้นไป สอบถาม ครูซอลีฮะห์ หมัดอะหิน 095-0214255",
            " Masjid Na Khwan Nuruddin is open for new student admissions for the year 2567. Evening Quran classes, time from 18:30 - 20:00. Enrollment starts from age 6. Contact Teacher Solihah Madahin at 095-0214255 "
    };
    private int currentTextIndex = 0;
    private int currentTimerIndex = 0;
    private CountDownTimer countDownTimer;
    private List<Long> countdownTimes;
    private TextView countdownTimerTextView;

    private static final String VISITOR_COUNT_KEY = "visitorCountKey";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepray);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("VisitorCounterPrefs", Context.MODE_PRIVATE);
        int visitorCount = sharedPreferences.getInt(VISITOR_COUNT_KEY, 0);
        visitorCount++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VISITOR_COUNT_KEY, visitorCount);
        editor.apply();

        // Display visitor count
        TextView visitorCountTextView = findViewById(R.id.visitor_count);
        visitorCountTextView.setText("               จำนวนผู้เข้าชม : " + visitorCount);

        // Time
        final TextView textView = findViewById(R.id.time);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String currentTime = sdf.format(new Date());
                textView.setText(currentTime);
                handler.postDelayed(this, 1000); // Update every second
            }
        });

        // Day, Month, Year
        final TextView textViewDate = findViewById(R.id.Date);
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US);
        String currentDate = sdfDate.format(new Date());
        textViewDate.setText(currentDate);

        // TextView for displaying Islamic date
        final TextView textViewIslamicDate = findViewById(R.id.date_islam);

        // Fetch data from API
        fetchDateFromAPI(textViewIslamicDate);

        // Moving text animation
        final TextView infoTextView = findViewById(R.id.infoTextView);
        infoTextView.setText(texts[currentTextIndex]);

        // Prepare ViewPager
        int[] images = {R.drawable.m2, R.drawable.m1, R.drawable.m3}; // Your images
        final ViewPager viewPager = findViewById(R.id.viewPager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(this, images);
        viewPager.setAdapter(adapter);

        // Change image every 5 seconds
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

        // Slide text from right to left
        final Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                currentTextIndex = (currentTextIndex + 1) % texts.length;
                infoTextView.setText(texts[currentTextIndex]);
                infoTextView.startAnimation(slideLeft);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        infoTextView.startAnimation(slideLeft);

        // Change background color of Islamic date TextView
        changeIslamicDateBackgroundColor();

        // Initialize countdown times (in milliseconds)
        countdownTimes = new ArrayList<>();
        countdownTimes.add(3600000L); // 1 hour in milliseconds
        countdownTimes.add(1800000L); // 30 minutes in milliseconds
        countdownTimes.add(600000L); // 10 minutes in milliseconds

        // Initialize countdown timer TextView
        countdownTimerTextView = findViewById(R.id.countdown_timer);
        startNextTimer();
    }

    private void changeIslamicDateBackgroundColor() {
        final TextView textViewIslamicDate = findViewById(R.id.date_islam);
        final LinearLayout linearLayout1 = findViewById(R.id.row1); // first LinearLayout
        final LinearLayout linearLayout2 = findViewById(R.id.row6); // LinearLayout
        final LinearLayout linearLayout3 = findViewById(R.id.row2);
        final LinearLayout linearLayout4 = findViewById(R.id.row3);
        final LinearLayout linearLayout5 = findViewById(R.id.row4);
        final LinearLayout linearLayout6 = findViewById(R.id.row5);
        final Handler bgColorHandler1 = new Handler();

        bgColorHandler1.postDelayed(new Runnable() {
            boolean isColorChanged1 = false;
            boolean isColorChanged2 = false;
            boolean isColorChanged3 = false;
            boolean isColorChanged4 = false;
            boolean isColorChanged5 = false;
            boolean isColorChanged6 = false;

            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String currentTime = sdf.format(new Date());

                // Condition for the first LinearLayout
                if ((currentTime.compareTo("20:10:01") >= 0 || currentTime.compareTo("05:10:00") <= 0)) {
                    // If the time is between 20:10 and 05:10
                    linearLayout1.setBackgroundResource(android.R.color.holo_blue_light); // Change background color
                    isColorChanged1 = true;
                } else if (isColorChanged1) {
                    // If the first condition is met, make the background transparent
                    linearLayout1.setBackgroundColor(Color.TRANSPARENT); // Use transparent color
                    isColorChanged1 = false; // Reset the tag
                }

                // Condition for the second LinearLayout
                if ((currentTime.compareTo("05:10:01") >= 0 && currentTime.compareTo("06:34:00") <= 0)) {
                    // If the time is between 12:00 and 14:00
                    linearLayout2.setBackgroundResource(android.R.color.holo_blue_light); // Change the background color to another color
                    isColorChanged2 = true;
                } else if (isColorChanged2) {
                    // إذا كان الشرط الثاني قد تحقق، اجعل الخلفية شفافة
                    linearLayout2.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged2 = false; // إعادة تعيين العلامة
                }

                // شرط للLinearLayout الثالث
                if ((currentTime.compareTo("06:34:01") >= 0 && currentTime.compareTo("12:45:00") <= 0)) {
                    // إذا كان الوقت بين 12:00 و 14:00
                    linearLayout3.setBackgroundResource(android.R.color.holo_blue_light); // تغيير لون الخلفية إلى لون آخر
                    isColorChanged3 = true;
                } else if (isColorChanged3) {
                    // إذا كان الشرط الثاني قد تحقق، اجعل الخلفية شفافة
                    linearLayout3.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged3 = false; // إعادة تعيين العلامة
                }

                // شرط للLinearLayout االرابع
                if ((currentTime.compareTo("12:45:01") >= 0 && currentTime.compareTo("16:00:00") <= 0)) {
                    // إذا كان الوقت بين 12:00 و 14:00
                    linearLayout4.setBackgroundResource(android.R.color.holo_blue_light); // تغيير لون الخلفية إلى لون آخر
                    isColorChanged4 = true;
                } else if (isColorChanged4) {
                    // إذا كان الشرط الثاني قد تحقق، اجعل الخلفية شفافة
                    linearLayout4.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged4 = false; // إعادة تعيين العلامة
                }

                // شرط للLinearLayout االخامس
                if ((currentTime.compareTo("16:00:01") >= 0 && currentTime.compareTo("18:38:00") <= 0)) {
                    // إذا كان الوقت بين 12:00 و 14:00
                    linearLayout5.setBackgroundResource(android.R.color.holo_blue_light); // تغيير لون الخلفية إلى لون آخر
                    isColorChanged5 = true;
                } else if (isColorChanged5) {
                    // إذا كان الشرط الثاني قد تحقق، اجعل الخلفية شفافة
                    linearLayout5.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged5 = false; // إعادة تعيين العلامة
                }

                // شرط للLinearLayout االسادس
                if ((currentTime.compareTo("18:38:01") >= 0 && currentTime.compareTo("20:10:00") <= 0)) {
                    // إذا كان الوقت بين 12:00 و 14:00
                    linearLayout6.setBackgroundResource(android.R.color.holo_blue_light); // تغيير لون الخلفية إلى لون آخر
                    isColorChanged6 = true;
                } else if (isColorChanged6) {
                    // إذا كان الشرط الثاني قد تحقق، اجعل الخلفية شفافة
                    linearLayout6.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged6 = false; // إعادة تعيين العلامة
                }

                bgColorHandler1.postDelayed(this, 1000); // التحقق مرة أخرى كل 2 ثانية
            }
        }, 1000); // بدء تغيير لون الخلفية فور تحميل النشاط
    }

    private void fetchDateFromAPI(TextView textViewIslamicDate) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String currentDate = sdf.format(new Date());
        String url = "https://api.aladhan.com/v1/gToH/" + currentDate;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONObject hijri = data.getJSONObject("hijri");

                        String hijriDay = hijri.getString("day");
                        String hijriMonthEn = hijri.getJSONObject("month").getString("en");
                        String hijriYear = hijri.getString("year");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewIslamicDate.setText(String.format(Locale.US,
                                        "%s %s %s", hijriDay, hijriMonthEn, hijriYear));
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startNextTimer() {
        if (currentTimerIndex < countdownTimes.size()) {
            long countdownTime = countdownTimes.get(currentTimerIndex);
            countDownTimer = new CountDownTimer(countdownTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = millisUntilFinished / (1000 * 60 * 60);
                    long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                    long seconds = (millisUntilFinished / 1000) % 60;
                    String timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
                    countdownTimerTextView.setText(timeFormatted);
                }

                @Override
                public void onFinish() {
                    currentTimerIndex++;
                    startNextTimer();
                }
            }.start();
        } else {
            currentTimerIndex = 0; // Reset index if needed
            startNextTimer();
        }
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
