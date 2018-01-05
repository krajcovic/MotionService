package cz.krajcovic.motionservice;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;

public class ShellCommandService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        private final String TAG = ServiceHandler.class.getName();

        public ServiceHandler(Looper looper) {
            super(looper);
        }


         @Override
        public void handleMessage(Message msg) {
             //executeteTouch01();
            try {
                int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;


                MotionCommands command = MotionCommands.valueOf(msg.arg2);
                double factor = 0.2;
                switch (command) {
                    case UP:
                        executeteSwipe(width / 2, height - (int)(height * factor), width / 2, (int)(height * factor), 250);
                        break;
                    case DOWN:
                        executeteSwipe(width / 2, (int)(height * factor), width / 2, height - (int)(height * factor), 250);
                        break;
                    case LEFT:
                        executeteSwipe(width - (int)(width * factor), height / 2, (int)(width * factor), height / 2, 250);
                        break;
                    case RIGHT:
                        executeteSwipe((int)(width * factor), height / 2, width - (int)(width * factor), height / 2, 250);
                        break;
                    case LONG_CENTER:
                        executeteLontTouch(width/2, height /2, 250);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }


            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }

        private void executeteTouch01(View view) {
            // get the coordinates of the view
            int[] coordinates = new int[2];
            view.getLocationOnScreen(coordinates);

// MotionEvent parameters
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();
            int action = MotionEvent.ACTION_DOWN;
            int x = coordinates[0];
            int y = coordinates[1];
            int metaState = 0;

// dispatch the event
            MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
            view.dispatchTouchEvent(event);
        }

        private void executeteSwipe(int x1, int y1, int x2, int y2, int duration) throws IOException {
                String command = String.format("input swipe %d %d %d %d %d", x1, y1, x2, y2, duration);
                java.lang.Process process = Runtime.getRuntime().exec(command);
        }

        private void executeteLontTouch(int x, int y, int duration) throws IOException {
            String command = String.format("input swipe %d %d %d %d %d", x, y, x, y, duration);
            java.lang.Process process = Runtime.getRuntime().exec(command);
        }
    }


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.arg2 = intent.getIntExtra("motionCommand", MotionCommands.UP.getId());
//        msg.obj = intent.getBundleExtra("currentView");
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

//    protected void showToast(final String msg){
//        //gets the main thread
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                // run this code in the main thread
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
