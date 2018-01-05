package cz.krajcovic.motionservice;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InputManagerCommandService extends Service {
    private static final String TAG = InputManagerCommandService.class.getName();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        private final String TAG = ServiceHandler.class.getName();

        Method injectInputEventMethod;
        InputManager im;

        public ServiceHandler(Looper looper) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            super(looper);

            setEventInput();
        }


        @Override
        public void handleMessage(Message msg) {
            handleShell(msg);


            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }

        private void handleShell(Message msg) {
            try {
                int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;


                MotionCommands command = MotionCommands.valueOf(msg.arg2);
                double factor = 0.2;
                int duration = 100;
                switch (command) {
                    case UP:
                        executeteSwipe(width / 2, height - (int) (height * factor), width / 2, (int) (height * factor), duration);
                        break;
                    case DOWN:
                        executeteSwipe(width / 2, (int) (height * factor), width / 2, height - (int) (height * factor), duration);
                        break;
                    case LEFT:
                        executeteSwipe(width - (int) (width * factor), height / 2, (int) (width * factor), height / 2, duration);
                        break;
                    case RIGHT:
                        executeteSwipe((int) (width * factor), height / 2, width - (int) (width * factor), height / 2, duration);
                        break;
                    case LONG_CENTER:
                        executeteLontTouch(width / 2, height / 2, 250);
                        break;
                    case TEST_L_R:
                        for(int i = 10; i > 0; i--) {
                            executeteSwipe(width - (int) (width * factor), height / 2, (int) (width * factor), height / 2, duration);
                            sleep(1000);
                            executeteSwipe((int) (width * factor), height / 2, width - (int) (width * factor), height / 2, duration);
                            sleep(1000);
                        }
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }

        private void sleep(int i) {
            // Add your cpu-blocking activity here
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void setEventInput() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            String methodName = "getInstance";
            Object[] objArr = new Object[0];
            im = (InputManager) InputManager.class.getDeclaredMethod(methodName, new Class[0])
                    .invoke(null, objArr);

            //Make MotionEvent.obtain() method accessible
            methodName = "obtain";
            MotionEvent.class.getDeclaredMethod(methodName, new Class[0]).setAccessible(true);

            //Get the reference to injectInputEvent method
            methodName = "injectInputEvent";
            injectInputEventMethod = InputManager.class.getMethod(methodName, new Class[]{InputEvent.class, Integer.TYPE});
        }

        public void injectMotionEvent(int inputSource, int action, long when, float x, float y,
                                      float pressure) throws InvocationTargetException, IllegalAccessException {
            MotionEvent event = MotionEvent.obtain(when, when, action, x, y, pressure, 1.0f, 0, 1.0f, 1.0f, 0, 0);
            event.setSource(inputSource);
            injectInputEventMethod.invoke(im, new Object[]{event, Integer.valueOf(0)});
        }

        private void injectKeyEvent(KeyEvent event)
                throws InvocationTargetException, IllegalAccessException {
            injectInputEventMethod.invoke(im, new Object[]{event, Integer.valueOf(0)});
        }

        private void executeteSwipe(int x1, int y1, int x2, int y2, int duration) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_DOWN, SystemClock.uptimeMillis(), x1, y1, 1.0f);

            int loop = 10;
            int signx = x1 > x2 ? 1 : -1;
            int signy = y1 > y2 ? 1 : -1;
            int dx = x1 > x2 ? (x1 - x2) / loop : (x2- x1) / loop;
            int dy = y1 > y2 ? (y1 - y2) / loop : (y2- y1) / loop;
            for(int i = 0; i < loop; i++) {

                injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_MOVE, SystemClock.uptimeMillis(), x1 - signx * dx * i, y1 - signy * dy * i, 1.0f);
                sleep(duration/loop);
            }

            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_UP, SystemClock.uptimeMillis(), x2, y2, 1.0f);
        }

        private void executeteLontTouch(int x, int y, int duration) throws IOException, InvocationTargetException, IllegalAccessException {
            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_DOWN, SystemClock.uptimeMillis(), x, y, 1.0f);
            sleep(duration);
            injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, MotionEvent.ACTION_UP, SystemClock.uptimeMillis(), x, y, 1.0f);
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
        try {
            mServiceHandler = new ServiceHandler(mServiceLooper);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

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
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
