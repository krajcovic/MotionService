package cz.krajcovic.motionservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button btn = (Button) findViewById(R.id.btnStart);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////                    // Restore interrupt status.
////                    Thread.currentThread().interrupt();
////                }
//
//
//                serviceIntent = new Intent(getApplicationContext(), ShellCommandService.class);
//                serviceIntent.putExtra("motionCommand", MotionCommands.UP.getId());
////                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.clRootLayout);
////                serviceIntent.putExtra("currentView", (Parcelable) layout.getRootView());
//                startService(serviceIntent);
//            }
//        });
//
//        btn = (Button) findViewById(R.id.btnStop);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService(serviceIntent);
//            }
//        });

        Button btn = (Button) findViewById(R.id.btnUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(getApplicationContext(), InputManagerCommandService.class);
                serviceIntent.putExtra("motionCommand", MotionCommands.UP.getId());
                startService(serviceIntent);
            }
        });

        btn = (Button) findViewById(R.id.btnDown);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(getApplicationContext(), InputManagerCommandService.class);
                serviceIntent.putExtra("motionCommand", MotionCommands.DOWN.getId());
                startService(serviceIntent);
            }
        });

        btn = (Button) findViewById(R.id.btnLeft);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(getApplicationContext(), InputManagerCommandService.class);
                serviceIntent.putExtra("motionCommand", MotionCommands.LEFT.getId());
                startService(serviceIntent);
            }
        });

        btn = (Button) findViewById(R.id.btnRight);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(getApplicationContext(), InputManagerCommandService.class);
                serviceIntent.putExtra("motionCommand", MotionCommands.RIGHT.getId());
                startService(serviceIntent);
            }
        });

        btn = (Button) findViewById(R.id.btnCenter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(getApplicationContext(), InputManagerCommandService.class);
                serviceIntent.putExtra("motionCommand", MotionCommands.LONG_CENTER.getId());
                startService(serviceIntent);
            }
        });

        btn = (Button) findViewById(R.id.btnTestLR);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                // Add your cpu-blocking activity here
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }

                serviceIntent = new Intent(getApplicationContext(), InputManagerCommandService.class);
                serviceIntent.putExtra("motionCommand", MotionCommands.TEST_L_R.getId());
                startService(serviceIntent);
            }
        });

    }
}
