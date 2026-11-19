package com.sandy.care;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RemoteControlService extends AccessibilityService {

    private DatabaseReference databaseReference;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        
        // الاتصال بنفس القاعدة والاستماع للتغييرات بشكل فوري
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://sandycare-f96f2-default-rtdb.firebaseio.com/");
        databaseReference = db.getReference("touch_command");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long xLong = snapshot.child("x").getValue(Long.class);
                    Long yLong = snapshot.child("y").getValue(Long.class);

                    if (xLong != null && yLong != null) {
                        float x = xLong.floatValue();
                        float y = yLong.floatValue();
                        
                        // تنفيذ النقرة على الشاشة إلكترونياً
                        simulateClick(x, y);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // التعامل مع الأخطاء إن وجدت
            }
        });
    }

    private void simulateClick(float x, float y) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(x, y);
            
            GestureDescription.Builder builder = new GestureDescription.Builder();
            // نقرة سريعة لمدة 50 ميللي ثانية
            builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
            
            dispatchGesture(builder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                }
            }, null);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
