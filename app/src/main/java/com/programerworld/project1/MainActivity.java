
package com.programerworld.project1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            }
    public void actimepray(View view) {
        // สร้าง Intent เพื่อเปิด activity timepray
        EditText numCodeEditText = findViewById(R.id.numcode);
        String numCode = numCodeEditText.getText().toString();
        // ตรวจสอบว่า numCode มีความยาวเท่ากับ 5 หรือไม่
        if (numCode.length() != 5) {
            // แสดงข้อความแจ้งเตือนให้กรอกรหัสมัสยิดให้ครบ 5 ตัว
            Toast.makeText(this, "กรุณากรอกรหัสมัสยิดให้ครบ 5 ตัว", Toast.LENGTH_SHORT).show();
            return; // ออกจากเมธอด
        }
        // ถ้า numCode มีความยาวเท่ากับ 5 ให้ดำเนินการต่อไปโดยปกติ
        // สร้าง Intent เพื่อเปิด activity timepray
        Intent intent = new Intent(MainActivity.this, timepray.class);
        startActivity(intent); // เริ่ม activity timepray
    }
    public void openPrivacyPolicy(View view) {
        // Open the privacy policy URL in a web browser
        String privacyPolicyUrl = "https://imacscenter.org/main/privacy-policy/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
        startActivity(intent);
    }
}
