package com.sandy.care;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.MotionEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BackgroundService extends AccessibilityService {

    private DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        // تهيئة الاتصال بقاعدة البيانات التي أنشأناها
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://sandycare-f96f2-default-rtdb.firebaseio.com/");
        databaseReference = db.getReference("touch_command");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // يمكنك مراقبة الأحداث هنا إذا لزم الأمر
    }

    // هذه الدالة ترسل إحداثيات اللمس مباشرة عند النقر على شاشتك
    public void sendTouchCoordinates(float x, float y) {
        if (databaseReference != null) {
            databaseReference.child("x").setValue(x);
            databaseReference.child("y").setValue(y);
            databaseReference.child("timestamp").setValue(System.currentTimeMillis());
        }
    }

    @Override
    public void onInterrupt() {
        // عند توقف الخدمة
    }
}
