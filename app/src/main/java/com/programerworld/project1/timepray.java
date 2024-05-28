package com.programerworld.project1;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepray);

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
    }

    private void changeIslamicDateBackgroundColor() {
        final TextView textViewIslamicDate = findViewById(R.id.date_islam);
        final LinearLayout linearLayout1 = findViewById(R.id.row1); // الLinearLayout الأول
        final LinearLayout linearLayout2 = findViewById(R.id.row7); // الLinearLayout الثاني

        final Handler bgColorHandler1 = new Handler();

        bgColorHandler1.postDelayed(new Runnable() {
            boolean isColorChanged1 = false;
            boolean isColorChanged2 = false;

            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String currentTime = sdf.format(new Date());

                // شرط للLinearLayout الأول
                if ((currentTime.compareTo("20:10:00") >= 0 || currentTime.compareTo("05:10:00") <= 0)) {
                    // إذا كان الوقت بين 20:10 و 05:10
                    linearLayout1.setBackgroundResource(android.R.color.darker_gray); // تغيير لون الخلفية
                    isColorChanged1 = true;
                } else if (isColorChanged1) {
                    // إذا كان الشرط الأول قد تحقق، اجعل الخلفية شفافة
                    linearLayout1.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged1 = false; // إعادة تعيين العلامة
                }

                // شرط للLinearLayout الثاني
                if ((currentTime.compareTo("05:10:01") >= 0 && currentTime.compareTo("06:34:00") <= 0)) {
                    // إذا كان الوقت بين 12:00 و 14:00
                    linearLayout2.setBackgroundResource(android.R.color.darker_gray); // تغيير لون الخلفية إلى لون آخر
                    isColorChanged2 = true;
                } else if (isColorChanged2) {
                    // إذا كان الشرط الثاني قد تحقق، اجعل الخلفية شفافة
                    linearLayout2.setBackgroundColor(Color.TRANSPARENT); // استخدام اللون الشفاف
                    isColorChanged2 = false; // إعادة تعيين العلامة
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
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
