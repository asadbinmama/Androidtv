package com.programerworld.project1;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONObject;
import java.io.IOException;

public class timepray extends AppCompatActivity {

    private static final int IMAGE_CHANGE_INTERVAL = 5000; // ช่วงการเปลี่ยนภาพเป็นมิลลิวินาที (5 วินาที)
    private String[] texts = {
            "             มัสยิดหน้าควน นูรุดดีน เปิดรับสมัครนักเรียนใหม่ ปีการศึกษา 2567 เรียนอัลกุรอานภาคค่ำ หลักสูตรกีรออาตี เวลา 18.30 - 20.00 น. รับสมัครตั้งแต่อายุ 6 ปีขึ้นไป สอบถาม ครูซอลีฮะห์ หมัดอะหิน 095-0214255",
            " Masjid Na Khwan Nuruddin is open for new student admissions for the year 2567. Evening Quran classes, time from 18:30 - 20:00. Enrollment starts from age 6. Contact Teacher Solihah Madahin at 095-0214255 "
    };
    private int currentTextIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepray);

        // เวลา
        final TextView textView = findViewById(R.id.time);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                String currentTime = sdf.format(new Date());
                textView.setText(currentTime);
                handler.postDelayed(this, 1000); // อัปเดตทุกวินาที
            }
        });

        // วันเดือนปี
        final TextView textViewDate = findViewById(R.id.Date);
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US);
        String currentDate = sdfDate.format(new Date());
        textViewDate.setText(currentDate);

        // TextView สำหรับแสดงวันที่ Islamic
        final TextView textViewIslamicDate = findViewById(R.id.date_islam);

        // ดึงข้อมูลจาก API
        fetchDateFromAPI(textViewIslamicDate);

        // ข้อความภาพเคลื่อนไหว
        final TextView infoTextView = findViewById(R.id.infoTextView);
        infoTextView.setText(texts[currentTextIndex]);

        // การตระเตรียม ViewPager
        int[] images = {R.drawable.m2, R.drawable.m1, R.drawable.m3}; // รูปของคุณ
        final ViewPager viewPager = findViewById(R.id.viewPager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(this, images);
        viewPager.setAdapter(adapter);

        // เปลี่ยนภาพทุกๆ 5 วินาที
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

        // ย้ายข้อความจากขวาไปซ้าย
        final Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // หลังจากภาพเคลื่อนไหวสิ้นสุดลง ให้เปลี่ยนข้อความ
                currentTextIndex = (currentTextIndex + 1) % texts.length;
                infoTextView.setText(texts[currentTextIndex]);
                infoTextView.startAnimation(slideLeft); // เริ่มเคลื่อนไหวอีกครั้ง
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // เริ่มการเคลื่อนไหวครั้งแรก
        infoTextView.startAnimation(slideLeft);
    }

    private void fetchDateFromAPI(TextView textViewIslamicDate) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        // สร้าง URL สำหรับวันที่ปัจจุบัน
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

                        // ดึงข้อมูลที่ต้องการจาก JSON
                        String hijriDay = hijri.getString("day");
                        String hijriMonthEn = hijri.getJSONObject("month").getString("en");
                        String hijriYear = hijri.getString("year");

                        // อัพเดต UI
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
