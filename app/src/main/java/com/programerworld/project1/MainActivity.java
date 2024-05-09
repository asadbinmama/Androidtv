package com.programerworld.project1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText numCodeEditText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // เริ่มต้น EditText
        numCodeEditText = findViewById(R.id.numcode);

        // เพิ่ม TextChangedListener เพื่อตรวจสอบจำนวนตัวอักษรที่ถูกป้อนใน EditText
        numCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ก่อนที่ข้อความจะเปลี่ยนแปลง
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // เมื่อข้อความถูกเปลี่ยนแปลง
            }
            @Override
            public void afterTextChanged(Editable s) {
                // หลังจากที่ข้อความถูกเปลี่ยนแล้ว

                // ตรวจสอบว่าข้อความมีความยาวมากกว่า 5 ตัวหรือไม่
                if (s.length() > 5) {
                    // ถ้ามีมากกว่า 5 ตัว ให้ลบตัวอักษรที่เกินกว่า 5 ตัว
                    s.delete(5, s.length());
                }
            }
        });
    }

    // วิธีการที่เรียกว่าเมื่อคลิกปุ่ม "Next"
    public void openWebsite(View view) {
        // รับรหัสที่ป้อนจาก EditText
        String numCode = numCodeEditText.getText().toString();

        // ตรวจสอบว่ารหัสที่ป้อนถูกต้องหรือไม่ (ในกรณีนี้คือรหัส 5 หลัก)
        if (numCode.length() == 5) {
            // รหัสถูกต้อง สร้าง URL ด้วยรหัสที่ป้อน
            String formattedCode = numCode.substring(0, 2) + "." + numCode.substring(2);
            String url = "https://imacscenter.org/masjid-prayer-time/" + formattedCode;

            // เปิด URL ในเว็บเบราว์เซอร์
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            // รหัสไม่ถูกต้อง แสดงข้อความแสดงข้อผิดพลาด
            Toast.makeText(this, "กรุณาป้อนข้อมูลให้ครบ 5 ตัว", Toast.LENGTH_SHORT).show();
        }
    }

    // วิธีการเปิดนโยบายความเป็นส่วนตัว
    public void openPrivacyPolicy(View view) {
        // Open the privacy policy URL in a web browser
        String privacyPolicyUrl = "https://imacscenter.org/main/privacy-policy/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
        startActivity(intent);


    }
}