package cnedu.ustcjd.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.POWER_SERVICE;

public class IntentWrapper {
    private static final String TAG = "IntentWrapper";
    //Android 6.0+ Doze 模式
    protected static final int DOZE = 98;
    //华为 自启管理
    protected static final int HUAWEI = 99;
    //华为 锁屏清理
    protected static final int HUAWEI_GOD = 100;
    //小米 自启动管理
    protected static final int XIAOMI = 101;
    //小米 神隐模式
    protected static final int XIAOMI_GOD = 102;
    //三星 5.0/5.1 自启动应用程序管理
    protected static final int SAMSUNG_L = 103;
    //魅族 自启动管理
    protected static final int MEIZU = 104;
    //魅族 待机耗电管理
    protected static final int MEIZU_GOD = 105;
    //Oppo 自启动管理
    protected static final int OPPO = 106;
    //三星 6.0+ 未监视的应用程序管理
    protected static final int SAMSUNG_M = 107;
    //Oppo 自启动管理(旧版本系统)
    protected static final int OPPO_OLD = 108;
    //Vivo 后台高耗电
    protected static final int VIVO_GOD = 109;
    //金立 应用自启
    protected static final int GIONEE = 110;
    //乐视 自启动管理
    protected static final int LETV = 111;
    //乐视 应用保护
    protected static final int LETV_GOD = 112;
    //酷派 自启动管理
    protected static final int COOLPAD = 113;
    //联想 后台管理
    protected static final int LENOVO = 114;
    //联想 后台耗电优化
    protected static final int LENOVO_GOD = 115;
    //中兴 自启管理
    protected static final int ZTE = 116;
    //中兴 锁屏加速受保护应用
    protected static final int ZTE_GOD = 117;
    //锤子手机 自启动权限管理
    protected static final int SMART = 118;

    public static List<IntentWrapper> getIntentWrapperList(Context context) {
        List<IntentWrapper> sIntentWrapperList = new ArrayList<>();
        if (context != null) {
            String packageName = context.getApplicationContext().getPackageName();

            //Android 6.0+ Doze 模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                boolean ignoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName);
                if (!ignoringBatteryOptimizations) {
                    Intent dozeIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    dozeIntent.setData(Uri.parse("package:" + packageName));
                    sIntentWrapperList.add(new IntentWrapper(dozeIntent, DOZE));
                }
            }

            //华为 自启管理
            Intent huaweiIntent = new Intent();
            huaweiIntent.setAction("huawei.intent.action.HSM_BOOTAPP_MANAGER");
            sIntentWrapperList.add(new IntentWrapper(huaweiIntent, HUAWEI));

            //华为 锁屏清理
            Intent huaweiGodIntent = new Intent();
            huaweiGodIntent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            sIntentWrapperList.add(new IntentWrapper(huaweiGodIntent, HUAWEI_GOD));

            //小米 自启动管理
            Intent xiaomiIntent = new Intent();
            xiaomiIntent.setAction("miui.intent.action.OP_AUTO_START");
            xiaomiIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sIntentWrapperList.add(new IntentWrapper(xiaomiIntent, XIAOMI));

            //小米 神隐模式
            Intent xiaomiGodIntent = new Intent();
            xiaomiGodIntent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
            xiaomiGodIntent.putExtra("package_name", packageName);
            xiaomiGodIntent.putExtra("package_label", getApplicationName(context));
            sIntentWrapperList.add(new IntentWrapper(xiaomiGodIntent, XIAOMI_GOD));

            //三星 5.0/5.1 自启动应用程序管理
            Intent samsungLIntent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
            if (samsungLIntent != null)
                sIntentWrapperList.add(new IntentWrapper(samsungLIntent, SAMSUNG_L));

            //三星 6.0+ 未监视的应用程序管理
            Intent samsungMIntent = new Intent();
            samsungMIntent.setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity"));
            sIntentWrapperList.add(new IntentWrapper(samsungMIntent, SAMSUNG_M));

            //魅族 自启动管理
            Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
            meizuIntent.putExtra("packageName", packageName);
            sIntentWrapperList.add(new IntentWrapper(meizuIntent, MEIZU));

            //魅族 待机耗电管理
            Intent meizuGodIntent = new Intent();
            meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.powerui.PowerAppPermissionActivity"));
            sIntentWrapperList.add(new IntentWrapper(meizuGodIntent, MEIZU_GOD));

            //Oppo 自启动管理
            Intent oppoIntent = new Intent();
            oppoIntent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            sIntentWrapperList.add(new IntentWrapper(oppoIntent, OPPO));

            //Oppo 自启动管理(旧版本系统)
            Intent oppoOldIntent = new Intent();
            oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.permission.startup.StartupAppListActivity"));
            sIntentWrapperList.add(new IntentWrapper(oppoOldIntent, OPPO_OLD));

            //Vivo 后台高耗电
            Intent vivoGodIntent = new Intent();
            vivoGodIntent.setComponent(new ComponentName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"));
            sIntentWrapperList.add(new IntentWrapper(vivoGodIntent, VIVO_GOD));

            //金立 应用自启
            Intent gioneeIntent = new Intent();
            gioneeIntent.setComponent(new ComponentName("com.gionee.softmanager", "com.gionee.softmanager.MainActivity"));
            sIntentWrapperList.add(new IntentWrapper(gioneeIntent, GIONEE));

            //乐视 自启动管理
            Intent letvIntent = new Intent();
            letvIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(letvIntent, LETV));

            //乐视 应用保护
            Intent letvGodIntent = new Intent();
            letvGodIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.BackgroundAppManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(letvGodIntent, LETV_GOD));

            //酷派 自启动管理
            Intent coolpadIntent = new Intent();
            coolpadIntent.setComponent(new ComponentName("com.yulong.android.security", "com.yulong.android.seccenter.tabbarmain"));
            sIntentWrapperList.add(new IntentWrapper(coolpadIntent, COOLPAD));

            //联想 后台管理
            Intent lenovoIntent = new Intent();
            lenovoIntent.setComponent(new ComponentName("com.lenovo.security", "com.lenovo.security.purebackground.PureBackgroundActivity"));
            sIntentWrapperList.add(new IntentWrapper(lenovoIntent, LENOVO));

            //联想 后台耗电优化
            Intent lenovoGodIntent = new Intent();
            lenovoGodIntent.setComponent(new ComponentName("com.lenovo.powersetting", "com.lenovo.powersetting.ui.Settings$HighPowerApplicationsActivity"));
            sIntentWrapperList.add(new IntentWrapper(lenovoGodIntent, LENOVO_GOD));

            //中兴 自启管理
            Intent zteIntent = new Intent();
            zteIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.autorun.AppAutoRunManager"));
            sIntentWrapperList.add(new IntentWrapper(zteIntent, ZTE));

            //中兴 锁屏加速受保护应用
            Intent zteGodIntent = new Intent();
            zteGodIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.setting.ClearAppSettingsActivity"));
            sIntentWrapperList.add(new IntentWrapper(zteGodIntent, ZTE_GOD));

            //锤子 自启动权限管理 //{cmp=com.smartisanos.security/.invokeHistory.InvokeHistoryActivity (has extras)} from uid 10070 on display 0
            Intent smartIntent = new Intent();
            smartIntent.putExtra("packageName", context.getPackageName());
            smartIntent.setComponent(new ComponentName("com.smartisanos.security", "com.smartisanos.security.invokeHistory.InvokeHistoryActivity"));
            sIntentWrapperList.add(new IntentWrapper(zteGodIntent, SMART));
        }
        return sIntentWrapperList;
    }

    protected static String sApplicationName;

    public static String getApplicationName(Context context) {
        if (sApplicationName == null) {
            PackageManager pm;
            ApplicationInfo ai;
            try {
                pm = context.getApplicationContext().getPackageManager();
                ai = pm.getApplicationInfo(context.getPackageName(), 0);
                sApplicationName = pm.getApplicationLabel(ai).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                sApplicationName = context.getPackageName();
            }
        }
        return sApplicationName;
    }

    protected Intent intent;
    protected int type;

    protected IntentWrapper(Intent intent, int type) {
        this.intent = intent;
        this.type = type;
    }

    public int getType() {
        return type;
    }
    /**
     * 判断本机上是否有能处理当前Intent的Activity
     */
    protected boolean doesActivityExists(Context context) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    /**
     * 安全地启动一个Activity
     */
    protected void startActivity(Activity a) {
        try {
            a.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IntentWrapper getBatteryOptimizationIntent(Context context) {
        IntentWrapper myIntent = null;
        for (IntentWrapper iw : getIntentWrapperList(context)) {
            if (!iw.doesActivityExists(context)) continue;
            myIntent = iw;
            break;
        }
        return myIntent;
    }
    /**
     * 处理白名单.
     *
     * @return 弹过框的 IntentWrapper.
     */
    @NonNull
    public static List<IntentWrapper> whitheListMatters(final Activity context) {
        List<IntentWrapper> showed = new ArrayList<>();
        String reason = "核心服务的持续运行";
        List<IntentWrapper> intentWrapperList = getIntentWrapperList(context);
        for (final IntentWrapper iw : intentWrapperList) {
            //如果本机上没有能处理这个Intent的Activity，说明不是对应的机型，直接忽略进入下一次循环。
            if (!iw.doesActivityExists(context)) continue;
            switch (iw.type) {
                case DOZE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        if (pm.isIgnoringBatteryOptimizations(context.getPackageName())) break;
                        new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setTitle("需要忽略 " + getApplicationName(context) + " 的电池优化")
                                .setMessage(reason + "需要 " + getApplicationName(context) + " 加入到电池优化的忽略名单。\n\n" +
                                        "请点击『确定』，在弹出的『忽略电池优化』对话框中，选择『是』。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int w) {
                                        iw.startActivity(context);
                                    }
                                })
                                .show();
                        showed.add(iw);
                    }
                    break;
                case HUAWEI:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 自动启动")
                            .setMessage(reason + "需要允许 " + getApplicationName(context) + " 的自动启动。\n\n" +
                                    "请点击『确定』，在弹出的『自启管理』中，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case ZTE_GOD:
                case HUAWEI_GOD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle(getApplicationName(context) + " 需要加入锁屏清理白名单")
                            .setMessage(reason + "需要 " + getApplicationName(context) + " 加入到锁屏清理白名单。\n\n" +
                                    "请点击『确定』，在弹出的『锁屏清理』列表中，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case XIAOMI_GOD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要关闭 " + getApplicationName(context) + " 的神隐模式")
                            .setMessage(reason + "需要关闭 " + getApplicationName(context) + " 的神隐模式。\n\n" +
                                    "请点击『确定』，在弹出的 " + getApplicationName(context) + " 神隐模式设置中，选择『无限制』，然后选择『允许定位』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case SAMSUNG_L:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 的自启动")
                            .setMessage(reason + "需要 " + getApplicationName(context) + " 在屏幕关闭时继续运行。\n\n" +
                                    "请点击『确定』，在弹出的『智能管理器』中，点击『内存』，选择『自启动应用程序』选项卡，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case SAMSUNG_M:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 的自启动")
                            .setMessage(reason + "需要 " + getApplicationName(context) + " 在屏幕关闭时继续运行。\n\n" +
                                    "请点击『确定』，在弹出的『电池』页面中，点击『未监视的应用程序』->『添加应用程序』，勾选 " + getApplicationName(context) + "，然后点击『完成』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case MEIZU:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 保持后台运行")
                            .setMessage(reason + "需要允许 " + getApplicationName(context) + " 保持后台运行。\n\n" +
                                    "请点击『确定』，在弹出的应用信息界面中，将『后台管理』选项更改为『保持后台运行』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case MEIZU_GOD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle(getApplicationName(context) + " 需要在待机时保持运行")
                            .setMessage(reason + "需要 " + getApplicationName(context) + " 在待机时保持运行。\n\n" +
                                    "请点击『确定』，在弹出的『待机耗电管理』中，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case ZTE:
                case LETV:
                case XIAOMI:
                case OPPO:
                case OPPO_OLD:
                case SMART:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 的自启动")
                            .setMessage(reason + "需要 " + getApplicationName(context) + " 加入到自启动白名单。\n\n" +
                                    "请点击『确定』，在弹出的『自启动管理』中，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case COOLPAD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 的自启动")
                            .setMessage(reason + "需要允许 " + getApplicationName(context) + " 的自启动。\n\n" +
                                    "请点击『确定』，在弹出的『酷管家』中，找到『软件管理』->『自启动管理』，取消勾选 " + getApplicationName(context) + "，将 " + getApplicationName(context) + " 的状态改为『已允许』。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case VIVO_GOD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 的后台运行")
                            .setMessage(reason + "需要允许 " + getApplicationName(context) + " 在后台高耗电时运行。\n\n" +
                                    "请点击『确定』，在弹出的『后台高耗电』中，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case GIONEE:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle(getApplicationName(context) + " 需要加入应用自启和绿色后台白名单")
                            .setMessage(reason + "需要允许 " + getApplicationName(context) + " 的自启动和后台运行。\n\n" +
                                    "请点击『确定』，在弹出的『系统管家』中，分别找到『应用管理』->『应用自启』和『绿色后台』->『清理白名单』，将 " + getApplicationName(context) + " 添加到白名单。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case LETV_GOD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要禁止 " + getApplicationName(context) + " 被自动清理")
                            .setMessage(reason + "需要禁止 " + getApplicationName(context) + " 被自动清理。\n\n" +
                                    "请点击『确定』，在弹出的『应用保护』中，将 " + getApplicationName(context) + " 对应的开关关闭。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case LENOVO:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要允许 " + getApplicationName(context) + " 的后台运行")
                            .setMessage(reason + "需要允许 " + getApplicationName(context) + " 的后台自启、后台 GPS 和后台运行。\n\n" +
                                    "请点击『确定』，在弹出的『后台管理』中，分别找到『后台自启』、『后台 GPS』和『后台运行』，将 " + getApplicationName(context) + " 对应的开关打开。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case LENOVO_GOD:
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("需要关闭 " + getApplicationName(context) + " 的后台耗电优化")
                            .setMessage(reason + "需要关闭 " + getApplicationName(context) + " 的后台耗电优化。\n\n" +
                                    "请点击『确定』，在弹出的『后台耗电优化』中，将 " + getApplicationName(context) + " 对应的开关关闭。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivity(context);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
            }
        }
        return showed;
    }

    public static List<IntentWrapper> showBatteryOptimizationSettingDialog(final Activity context) {
        List<IntentWrapper> showed = new ArrayList<>();
        List<IntentWrapper> intentWrapperList = getIntentWrapperList(context);
        for (final IntentWrapper iw : intentWrapperList) {
            //如果本机上没有能处理这个Intent的Activity，说明不是对应的机型，直接忽略进入下一次循环。
            if (!iw.doesActivityExists(context)) continue;
            showed.add(iw);
        }
        int count = 0;
        for (final IntentWrapper iw : showed) {
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(showed.size() > 1 ? String.format("重要（%s）", showed.size() - count) : "重要")
                    .setMessage("为保证应用核心服务的持续运行，以便及时收到设备的消息推送。需要忽略 " + getApplicationName(context) + " 的电池优化并允许应用在后台长期运行。\n\n" +
                            "请点击『确定』，在弹出的相应页面进行设置。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int w) {
                            d.dismiss();
                            iw.startActivity(context);
                        }
                    })
                    .show();
            count ++;
        }
        Log.d(TAG, "intent wrapper size:" + showed.size());
        return showed;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkIfMyselfInPowerWhiteList(Context context) {
        boolean ret = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            ret = pm.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        Log.d(TAG, String.format("SDK_Ver:%s, in power white list myself:%s", Build.VERSION.SDK_INT, ret));
        return ret;
    }
}
