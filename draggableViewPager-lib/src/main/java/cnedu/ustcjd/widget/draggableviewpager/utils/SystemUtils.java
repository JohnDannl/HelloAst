package cnedu.ustcjd.widget.draggableviewpager.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class SystemUtils {
    private final static String TAG = "SystemUtils";
    private final static String CPU_INFO_PATH = "/sys/devices/system/cpu/";
    private static boolean isP2pLibsLoaded = false;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;
    private static Object synObj = new Object();
    private static long lastClickTime;
    public static void loadLibrary(String libName) {
        System.loadLibrary(libName);
    }

    public static int getVersionNO() {
        return Build.VERSION.SDK_INT;
    }

    private static String getWifiMacAddress() {
        try {
            final NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");
            if (networkInterface != null) {
                final byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    final StringBuilder builder = new StringBuilder();
                    for (byte b : mac) {
                        builder.append(String.format("%02X:", b));
                    }

                    if (builder.length() > 0) {
                        builder.deleteCharAt(builder.length() - 1);
                    }

                    return builder.toString();
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    public static void showShortToast(final Context act, final int msgID) {
        if (act != null)
            showShortToast(act.getApplicationContext(), act.getResources().getString(msgID));
    }

    public static void showShortToast(final Context act, final String msg) {
        if (act != null)
            showMessage(act.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(final Context act, final String msg) {
        if (act != null)
            showMessage(act.getApplicationContext(), msg, Toast.LENGTH_LONG);
    }

    public static void showMessage(final Context act, final String msg, final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(act, msg, len);
                            }
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    public static void showSoftKeyboard(final Context context, final View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(final Context context, final View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSoftKeyboardByView(final View view) {
        try {
            view.onTouchEvent(MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(),
                    MotionEvent.ACTION_DOWN, view.getLeft() + 1, view.getTop() + 1, 0));
            view.onTouchEvent(MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(),
                    MotionEvent.ACTION_UP, view.getLeft() + 1, view.getTop() + 1, 0));
        } catch (Exception e) {
        }
    }

    public static void requestFocusOnEditText(final EditText view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final long down = SystemClock.uptimeMillis();
                final long up = down + 20;
                view.requestFocus();
                view.onTouchEvent(MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, view.getLeft() + 5, view.getTop() + 5, 0));
                view.onTouchEvent(MotionEvent.obtain(up, up, MotionEvent.ACTION_UP, view.getLeft() + 5, view.getTop() + 5, 0));
            }
        });
    }

    public static float getDeviceDensity(final Context context) {
        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    public static float getDeviceFontScaleDensity(final Context context) {
        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.scaledDensity;
    }

    public static int getDeviceWidth(final Context context) {
        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT > 17) {
            wm.getDefaultDisplay().getRealMetrics(dm);
        } else {
            wm.getDefaultDisplay().getMetrics(dm);
        }

        return dm.widthPixels;
    }

    public static int getDeviceHeight(final Context context) {
        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getSurfacePixelFormat() {
        return PixelFormat.RGBA_8888;
    }

    public static String getLocale() {
        try {
            final String country = Locale.getDefault().getLanguage().toLowerCase();
            return country;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "en_US";
    }

    public static void writeStringToLocal(final String filePath, final String content, final boolean append) {
        BufferedWriter out = null;
        try {
            final File file = new File(filePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, append)));
            out.write(content + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static DateFormat getSystemDefaultDateFormat(final Context context) {
        //return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            return new SimpleDateFormat("HH:mm", Locale.US);
        } else {
            return new SimpleDateFormat("hh:mm a", Locale.US);
        }
    }

    public static String getPreviewThumbDirectory(final Context context) {
        final File previewDir = new File(context.getCacheDir(), "Preview");
        return previewDir.getAbsolutePath();
    }

    public static String getPreviewThumbPath(final Context context, final String srcId) {
        final File thumbF = new File(getPreviewThumbDirectory(context), srcId + ".jpg");
        return thumbF.getAbsolutePath();
    }

    /*
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     * @return The number of cores, or 1 if failed to get result
     */
    public static int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File(CPU_INFO_PATH);
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    public static boolean isPoorPerformance() {
        return false;
    }

    public static DateFormat getDateFormat(final Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

    public static DateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    public static boolean is24HourFormat(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    public static String simpleDisturbToken(String url) {
        if (TextUtils.isEmpty(url)) return "";
        String token = "token=";
        int tokenPos = url.indexOf(token);
        if (tokenPos >= 0) {
            StringBuffer str = new StringBuffer(url);
            str.insert(token.length() + tokenPos, getRandomString(5));
            return str.toString();
        }
        return url;

    }

    public static String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return "";
    }

    public static boolean isPhoneNumber(final String account) {
        return !account.contains("@");
    }

    public static int getSystemStatusBarHeight(final Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static int parseInt(String value) {
        int result = 0;
        try {
            if (!TextUtils.isEmpty(value)) {
                result = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            result = 0;
            e.printStackTrace();
        }
        return result;
    }

    public static int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public static void getRealScreen(Context context, int[] realSize) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point p = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            d.getRealSize(p);
        } else { //it's hidden, maybe it is in lower versions
            try {
                Method m = Display.class.getMethod("getRealSize", Point.class);
                m.invoke(d, p);
            } catch (Throwable t) {
                DisplayMetrics mDisplayMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
                if (mDisplayMetrics.heightPixels > mDisplayMetrics.widthPixels) {
                    realSize[0] = mDisplayMetrics.heightPixels;
                    realSize[1] = mDisplayMetrics.widthPixels;
                } else {
                    realSize[0] = mDisplayMetrics.widthPixels;
                    realSize[1] = mDisplayMetrics.heightPixels;
                }
            }
        }
        if (p.y > p.x) {
            realSize[0] = p.y;
            realSize[1] = p.x;
        } else {
            realSize[0] = p.x;
            realSize[1] = p.y;
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static String getCurrentMonthAndDay() {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return formatter.format(new Date());
    }

    public static String getCurrentTime() {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS", Locale.getDefault());
        return formatter.format(new Date());
    }

    public static String getFormatTime(long milliseconds) {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS", Locale.getDefault());
        return formatter.format(new Date(milliseconds));
    }

    public static String getFormatEventStartTime(long milliseconds) {
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date(milliseconds));
    }

    public static String getCloseServiceExpiredTipDay() {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 判断是否存在虚拟导航栏
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        if (realWidth > realHeight) {
            return (realWidth - displayWidth);
        }

        return (realHeight - displayHeight);
    }

    public static void hideSystemNavBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    public static long getStartTimeOfDay(long now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndTimeOfDay(long now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static String formatEventTime(long timeMs) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        long totalSeconds = (long) Math.ceil((double) timeMs / 1000.0f);
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        stringBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    public static String formatEventCurrentTime(long timeMs) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(new Date(timeMs));
    }

    //

    /**
     * 竖屏时判断点击位置是否在target内
     *
     * @param target View
     * @param x      坐标x
     * @param y      坐标y
     * @return
     */
    public static boolean isTouchTargetViewPortrait(View target, float x, float y) {
        int[] fullScreenLocation = new int[2];
        int width = target.getWidth();
        int height = target.getHeight();
        target.getLocationOnScreen(fullScreenLocation);
        return x > fullScreenLocation[0] && x < fullScreenLocation[0] + width && y > fullScreenLocation[1] && y < fullScreenLocation[1] + height;
    }

    /**
     * 横屏时判断点击位置是否在target内
     *
     * @param target View
     * @param x      坐标x
     * @param y      坐标y
     * @return
     */
    public static boolean isTouchTargetViewLand(View target, float x, float y) {
        int[] fullScreenLocation = new int[2];
        int width = target.getWidth();
        int height = target.getHeight();
        target.getLocationOnScreen(fullScreenLocation);
        return x > fullScreenLocation[0] && x < fullScreenLocation[0] + height && y > fullScreenLocation[1] && y < fullScreenLocation[1] + width;
    }

    /**
     * 获取虚拟导航栏高度
     *
     * @param context
     * @return
     */
    public static int getVirtualBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static boolean hasNotchInVivo(Context context) {
        boolean hasNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method[] methods = ftFeature.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    if (method != null) {
                        if (method.getName().equalsIgnoreCase("isFeatureSupport")) {
                            hasNotch = (boolean) method.invoke(ftFeature, 0x00000020);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasNotch = false;
        }
        return hasNotch;
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
