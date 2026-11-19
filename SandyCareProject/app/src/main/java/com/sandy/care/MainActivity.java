package com.sandy.care;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private LinearLayout usersContainer;
    private DatabaseReference databaseReference;
    private Button btnStartControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // تصميم واجهة بسيطة برمجياً لوحة تحكم الأدمن
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("لوحة تحكم الأدمن - SandyCare");
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);

        btnStartControl = new Button(this);
        btnStartControl.setText("تفعيل خدمة التحكم والوصول");
        mainLayout.addView(btnStartControl);

        TextView subTitle = new TextView(this);
        subTitle.setText("\nالأجهزة المتصلة المتاحة للتحكم:");
        subTitle.setTextSize(16);
        mainLayout.addView(subTitle);

        usersContainer = new LinearLayout(this);
        usersContainer.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(usersContainer);

        setContentView(mainLayout);

        // تفعيل خدمات إمكانية الوصول عند الضغط على الزر
        btnStartControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // الانتقال لإعدادات الوصول لتفعيل الخدمة يدوياً على الجهاز
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "يرجى تفعيل خدمة SandyCare من القائمة", Toast.LENGTH_LONG).show();
            }
        });

        // الاتصال بقاعدة البيانات لجلب الأجهزة أو المستخدمين المتصلين
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://sandycare-f96f2-default-rtdb.firebaseio.com/");
        databaseReference = db.getReference("connected_devices");

        // إضافة جهاز تجريبي للاختبار في القائمة
        databaseReference.child("device_son").setValue("متصل وجاهز للتحكم");

        // مراقبة الأجهزة المتصلة وعرضها في لوحة الأدمن
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                usersContainer.removeAllViews();
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String deviceId = child.getKey();
                        String status = child.getValue(String.class);

                        Button deviceBtn = new Button(MainActivity.this);
                        deviceBtn.setText("جهاز الابن (" + deviceId + ") - " + status);
                        
                        // عند الضغط على جهاز الابن تفتح شاشته أو ترسل أمر التحكم
                        deviceBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "تم اختيار الجهاز، يمكنك البدء بالتحكم الآن!", Toast.LENGTH_SHORT).show();
                                // هنا يمكنك إرسال إشارة فتح الشاشة أو بدء مزامنة اللمس
                                databaseReference.child(deviceId).child("command").setValue("open_screen");
                            }
                        });

                        usersContainer.addView(deviceBtn);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "فشل الاتصال بقاعدة البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
