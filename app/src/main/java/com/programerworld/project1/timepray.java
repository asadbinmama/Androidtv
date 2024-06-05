package com.programerworld.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class timepray extends AppCompatActivity {

    // Count down
    private TextView tvCountdown;
    private final Handler handler = new Handler();


    // Language
    private boolean isThai = true;

    private TextView timeHeader;
    private TextView azanHeader;
    private TextView iqomahHeader;
    private TextView subuh;
    private TextView suhri;
    private TextView asri;
    private TextView maqrib;
    private TextView Isa;
    private TextView israk;
    private TextView Time;
    private TextView time_to_pray;
    private final String[] thaiTexts = {"เวลา", "อาซาน", "อิกอมะห์", "ซุบฮี", "ซุฮรี", "อัสรี", "มัฆริบ", "อีซา", "อิชร็อก", "เวลา", "เวลาละหมาดจะถึงในอีก"};
    private final String[] englishTexts = {"Time:", "Azan", "Iqomah", "subuh", "suhri", "asri", "maqrib", "Isa", "israk", "Time", "Prayer time will come soon."};

    //-------------
    private static final int IMAGE_CHANGE_INTERVAL = 5000; // Image change interval in milliseconds (5 seconds)
    private final String[] texts = {
            " มัสยิดหน้าควนนูรุดดีนเปิดรับสมัครนักเรียนใหม่ปีการศึกษา 2567 เรียนอัลกุรอานภาคค่ำหลักสูตรกีรออาตี เวลา 18.30 - 20.00 น. รับสมัครตั้งแต่อายุ 6 ปีขึ้นไป สอบถาม ครูซอลีฮะห์ หมัดอะหิน 095-0214255",
            " Masjid Na Khwan Nuruddin is open for new student admissions for the year 2567. Evening Quran classes, time from 18:30 - 20:00. Enrollment starts from age 6. Contact Teacher Solihah Madahin at 095-0214255 "
    };
    //----------------
    private int currentTextIndex = 0;
    private static final String VISITOR_COUNT_KEY = "visitorCountKey";

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepray);

        //--------
        timeHeader = findViewById(R.id.time_header);
        azanHeader = findViewById(R.id.azan_header);
        iqomahHeader = findViewById(R.id.iqomah_header);
        subuh = findViewById(R.id.subuh);
        suhri = findViewById(R.id.suhri);
        asri = findViewById(R.id.asri);
        maqrib = findViewById(R.id.maqrib);
        Isa = findViewById(R.id.Isa);
        israk = findViewById(R.id.israk);
        Time = findViewById(R.id.Time);
        time_to_pray = findViewById(R.id.time_to_pray);

        startTextSwitcher();

        //------------------------
        tvCountdown = findViewById(R.id.tvCountdown);
        // ตัวอย่างการตั้งค่าช่วงเวลาละหมาด
        long fajrTime = getTimeInMillis("04:42");
        long iqomahfajr = getTimeInMillis("05:10");
        long dhuhrTime = getTimeInMillis("12:30");
        long iqomahdhur = getTimeInMillis("12:45");
        long asrTime = getTimeInMillis("15:45");
        long iqomahasr = getTimeInMillis("16:00");
        long maghribTime = getTimeInMillis("18:28");
        long iqomahmaghrib = getTimeInMillis("18:38");
        long ishaTime = getTimeInMillis("20:00");
        long iqomahIsha = getTimeInMillis("20:10");
        long ishrak = getTimeInMillis("06:15");

        // อัปเดตเวลาเหลือในการละหมาด
        Runnable runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long nextPrayerTime = getNextPrayerTime(currentTime, fajrTime, iqomahfajr, ishrak, dhuhrTime, iqomahdhur, asrTime, iqomahasr, maghribTime, iqomahmaghrib, ishaTime, iqomahIsha);

                long diff = nextPrayerTime - currentTime;
                if (diff > 0) {
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;

                    tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    tvCountdown.setText("00:00:00");
                }

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("VisitorCounterPrefs", Context.MODE_PRIVATE);
        int visitorCount = sharedPreferences.getInt(VISITOR_COUNT_KEY, 0);
        visitorCount++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VISITOR_COUNT_KEY, visitorCount);
        editor.apply();

        // current time
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
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE d MMMM yyyy",new Locale("th", "TH"));
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
    //------------
    private void startTextSwitcher() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchText();
                handler.postDelayed(this, 10000); // 10 วินาที
            }
        }, 10000);
    }

    private void switchText() {
        if (isThai) {
            timeHeader.setText(englishTexts[0]);
            azanHeader.setText(englishTexts[1]);
            iqomahHeader.setText(englishTexts[2]);
            subuh.setText(englishTexts[3]);
            suhri.setText(englishTexts[4]);
            asri.setText(englishTexts[5]);
            maqrib.setText(englishTexts[6]);
            Isa.setText(englishTexts[7]);
            israk.setText(englishTexts[8]);
            Time.setText(englishTexts[9]);
            time_to_pray.setText(englishTexts[10]);
        } else {
            timeHeader.setText(thaiTexts[0]);
            azanHeader.setText(thaiTexts[1]);
            iqomahHeader.setText(thaiTexts[2]);
            subuh.setText(thaiTexts[3]);
            suhri.setText(thaiTexts[4]);
            asri.setText(thaiTexts[5]);
            maqrib.setText(thaiTexts[6]);
            Isa.setText(thaiTexts[7]);
            israk.setText(thaiTexts[8]);
            Time.setText(thaiTexts[9]);
            time_to_pray.setText(thaiTexts[10]);
        }
        isThai = !isThai; // สลับสถานะภาษา
    }

    //---------------
    private long getTimeInMillis(String time) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            calendar.setTime(Objects.requireNonNull(dateFormat.parse(time)));
            Calendar current = Calendar.getInstance();
            calendar.set(Calendar.YEAR, current.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, current.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long getNextPrayerTime(long currentTime, long... prayerTimes) {
        for (long prayerTime : prayerTimes) {
            if (prayerTime > currentTime) {
                return prayerTime;
            }
        }
        return prayerTimes[0] + TimeUnit.DAYS.toMillis(1); // Return the next day's first prayer time
    }
    //-------------------------

    private void changeIslamicDateBackgroundColor() {
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
                    // If the second condition is met, make the background transparent
                    linearLayout2.setBackgroundColor(Color.TRANSPARENT); // Use transparent color
                    isColorChanged2 = false; // Reset the flag
                }

                // Condition for the third LinearLayout
                if ((currentTime.compareTo("06:34:01") >= 0 && currentTime.compareTo("12:45:00") <= 0)) {
                    // If the time is between 12:00 and 14:00
                    linearLayout3.setBackgroundResource(android.R.color.holo_blue_light); // Change the background color to another color
                    isColorChanged3 = true;
                } else if (isColorChanged3) {
                    // If the second condition is met, make the background transparent
                    linearLayout3.setBackgroundColor(Color.TRANSPARENT); // Use transparent color
                    isColorChanged3 = false; // Reset the flag
                }

                // Condition for the fourth LinearLayout
                if ((currentTime.compareTo("12:45:01") >= 0 && currentTime.compareTo("16:00:00") <= 0)) {
                    // If the time is between 12:00 and 14:00
                    linearLayout4.setBackgroundResource(android.R.color.holo_blue_light); // Change the background color to another color
                    isColorChanged4 = true;
                } else if (isColorChanged4) {
                    // If the second condition is met, make the background transparent
                    linearLayout4.setBackgroundColor(Color.TRANSPARENT); // Use transparent color
                    isColorChanged4 = false; // Reset the flag
                }

                // Condition for the fifth LinearLayout
                if ((currentTime.compareTo("16:00:01") >= 0 && currentTime.compareTo("18:38:00") <= 0)) {
                    // If the time is between 12:00 and 14:00
                    linearLayout5.setBackgroundResource(android.R.color.holo_blue_light); // Change the background color to another color
                    isColorChanged5 = true;
                } else if (isColorChanged5) {
                    // If the second condition is met, make the background transparent
                    linearLayout5.setBackgroundColor(Color.TRANSPARENT); // Use transparent color
                    isColorChanged5 = false; // Reset the flag
                }

                // Condition for the sixth LinearLayout
                if ((currentTime.compareTo("18:38:01") >= 0 && currentTime.compareTo("20:10:00") <= 0)) {
                    // If the time is between 12:00 and 14:00
                    linearLayout6.setBackgroundResource(android.R.color.holo_blue_light); // Change the background color to another color
                    isColorChanged6 = true;
                } else if (isColorChanged6) {
                    // If the second condition is met, make the background transparent
                    linearLayout6.setBackgroundColor(Color.TRANSPARENT); // Use transparent color
                    isColorChanged6 = false; // Reset the flag
                }

                bgColorHandler1.postDelayed(this, 1000); //Check back every 2 seconds
            }
        }, 1000); // Start changing the background color as soon as the activity loads
    }

    private void fetchDateFromAPI(TextView textViewIslamicDate) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String currentDate = sdf.format(new Date());
        String url = "https://api.aladhan.com/v1/gToH/" + currentDate;

        new Thread(() -> {
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

                    runOnUiThread(() -> textViewIslamicDate.setText(String.format(Locale.US,
                            "%s %s %s", hijriDay, hijriMonthEn, hijriYear)));
                }
            } catch (Exception e) {
                e.printStackTrace();
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
