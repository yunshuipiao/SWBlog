## 悬浮窗

对于是否有开悬浮窗，程序是可以检测到的。

权限声明：
`<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>`


### 权限检查：

对于android6.0 以上的机型来说，google将悬浮窗权限和其他危险权限单独列出，因此可以检测， 方法：
```
fun checkFloatWindowPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val result = Settings.canDrawOverlays(BaseContext.application)
        return result
    } else {
        //6.0 以下
        val result = checkRomFloatWindowPermission()
        return result
    }
    return true
}
```

android 4.4以下大部分机型声明即获取（包括大部分国产机）， 因此只需判断4.4.4 -- 5.1之间的系统即可。
```
//如下，经过我的测试，只有小米和魅族需要在`api<=19`时单独处理，其他正常。
fun checkRomFloatWindowPermission(): Boolean {

    val brand = Build.MANUFACTURER
    if (brand.contains("Xiaomi")) {
        //check miui permission
        return checkMeizuAndMIUIFloatWindowPermission()

    }
    if (Build.VERSION.SDK_INT <= 19) {
        return true
    }
    when (brand) {
        "Huawei", "HUAWEI" -> return true
        "Meizu" -> return checkMeizuAndMIUIFloatWindowPermission()
        "OPPO" -> return true
        "vivo" -> return true
        "Sony" -> return true
        "Letv" -> return true
        "LG" -> return true
    }
    return true
}
```
```
//-------------- Meizu MIUI start---------------------

fun checkMeizuAndMIUIFloatWindowPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= 19) {
        return checkMeizuAndMIUIOp(24)
    }
    return true
}

fun checkMeizuAndMIUIOp(op: Int): Boolean {
    if (Build.VERSION.SDK_INT >= 19) {
        val opsManager = BaseContext.application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val clazz = AppOpsManager::class.java
            val method = clazz.getDeclaredMethod("checkOp",
                    Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java)
            return AppOpsManager.MODE_ALLOWED == method.invoke(opsManager, op, Binder.getCallingUid(), BaseContext.application.packageName)
        } catch (e: Exception) {
            Logger.d(TAG, "checkOp", e)
        }
    } else {
        Logger.d(TAG, "Below API 19 cannot invoke!")
    }
    return false
}

//-------------- Meizu MIUI end ---------------------
```

至此，权限检查完毕。
不能动态获取，只能跳转到这只页去手动开启。
```
fun applyFloatWindowPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= 23) {
        applySystemPermission(activity)
        return
    }
    val brand = Build.MANUFACTURER
    when (brand) {
        "Huawei", "HUAWEI" -> openHuaweiPermSetting(activity)
        "Xiaomi" -> applyMIUIPermSetting(activity)
        "Meizu" -> applyMeizuPermission(activity)
        "OPPO" -> openOppoPermSetting(activity)
        "vivo" -> openVivoPermSetting(activity)
        "Sony" -> openSonyPermSetting(activity)
        "Letv" -> openLetvPermSetting(activity)
        "LG" -> openLGPermSetting(activity)
        else -> applySystemPermission(activity)
    }
}
```

```

//-------------- Sysytem start ---------------------

fun  applySystemPermission(activity: Activity) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.data = Uri.parse("package:" + activity.packageName)
    activity.startActivityForResult(intent, 1)

}

//-------------- Sysytem end ---------------------
```

```
//-------------- Meizu start---------------------

fun applyMeizuPermission(activity: Activity) {
    val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
    intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
    intent.putExtra("packageName", activity.packageName)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    activity.startActivity(intent)
}

//-------------- Meizu end ---------------------
```

```
fun applyMIUIPermSetting(activity: Activity) {
    openMIUIPermSetting(activity)

}
```


附： 各手机厂商跳转到设置页的方法：
```
// open app setting according to rom ----------------

fun applyBrandStrategy(activity: Activity) {
    val brand = Build.MANUFACTURER
    when (brand) {
        "Huawei", "HUAWEI" -> openHuaweiPermSetting(activity)
        "Xiaomi" -> openMIUIPermSetting(activity)
        "Meizu" -> openMeizuPermSetting(activity)
        "OPPO" -> openOppoPermSetting(activity)
        "vivo" -> openVivoPermSetting(activity)
        "Sony" -> openSonyPermSetting(activity)
        "Letv" -> openLetvPermSetting(activity)
        "LG" -> openLGPermSetting(activity)
        else -> openSystemSetting(activity)
    }
}

fun openMIUIPermSetting(activity: Activity): Boolean {
    val i = Intent("miui.intent.action.APP_PERM_EDITOR")
    val componentName = ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
    i.component = componentName
    i.putExtra("extra_pkgname", activity.packageName)
    try {
        activity.startActivity(i)
    } catch (e: Exception) {
        Logger.e(TAG, "openMIUIPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openMeizuPermSetting(activity: Activity): Boolean {
    val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.putExtra("packageName", activity.packageName)
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openMeizuPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openHuaweiPermSetting(activity: Activity): Boolean {
    val intent = Intent()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("packageName", activity.packageName)
    var comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
    intent.component = comp
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openHuaweiPermSetting jump e:" + e.message)
        comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SingleAppActivity")
        intent.component = comp
        try {
            activity.startActivity(intent)
        } catch (e1: Exception) {
            Logger.e(TAG, "openHuaweiPermSetting jump e1:" + e.message)
            openSystemSetting(activity)
            return false
        }
    }

    return true
}

fun openOppoPermSetting(activity: Activity): Boolean {
    val intent = Intent()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("packageName", activity.packageName)
    val comp = ComponentName("com.oppo.safe", "com.oppo.safe.permission.PermissionSettingsActivity")
    intent.component = comp
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openOppoPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openVivoPermSetting(activity: Activity): Boolean {
    val intent = Intent()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("packageName", activity.packageName)
    val comp = ComponentName("com.iqoo.secure", "com.iqoo.secure.MainActivity")
    intent.component = comp
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openVivoPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openSonyPermSetting(activity: Activity): Boolean {
    val intent = Intent()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("packageName", activity.packageName)
    val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
    intent.component = comp
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openSonyPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openLGPermSetting(activity: Activity): Boolean {
    val intent = Intent("android.intent.action.MAIN")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("packageName", activity.packageName)
    val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity")
    intent.component = comp
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openLGPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openLetvPermSetting(activity: Activity): Boolean {
    val intent = Intent()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("packageName", activity.packageName)
    val comp = ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps")
    intent.component = comp
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(TAG, "openLetvPermSetting jump e:" + e.message)
        openSystemSetting(activity)
        return false
    }

    return true
}

fun openSystemSetting(activity: Activity): Boolean {
    val localIntent = Intent()
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
    localIntent.data = Uri.fromParts("package", activity.packageName, null)
    try {
        activity.startActivity(localIntent)
    } catch (e: Exception) {
        Logger.e(TAG, "openSystemSetting jump e:" + e.message)
        return false
    }

    return true
}
```

## 开启自启动
自启动比较简单，android系统无法检测到是否开启，一般做法是首次安装提示用户跳转设置：
```
    private fun applySelfStartSetting() {

        var intent = Intent()
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            var componentName: ComponentName? = null
            val brand = Build.MANUFACTURER
            when (brand) {
                "Xiaomi" -> {
                    componentName = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
                }
                "samsung", "Samsung" -> {
                    componentName = ComponentName("com.samsung.android.sm" ,"com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity")
                }
                "Huawei", "HUAWEI" -> {
                    componentName = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
                }
                "Meizu" -> {
                    componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.SmartBGActivity")
                }
                "OPPO" -> {
                    componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity")
                }
                "vivo" -> {
                    componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity")
                }
                "Letv" -> {
                    intent.action = "com.letv.android.permissionautoboot"
                }
                else -> {
                    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    intent.data = Uri.fromParts("package", packageName, null)
                }
            }
            intent.component = componentName
            startActivity(intent)
        } catch (e: Exception) {
            Logger.d(PhoneDialogActivity.TAG, "applySelfStartSetting:"  + e)
            intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
```



## android 相关信息的获取方法：
 
### 1.获取手机型号(Build)
所属包： android.os.Build 
作用(含义)： 从系统属性中提取设备硬件和版本信息 
静态属性：
```
1. BOARD 主板：The name of the underlying board, like goldfish. 
2.  BOOTLOADER 系统启动程序版本号：The system bootloader version number. 
3. BRAND 系统定制商：The consumer-visible brand with which the product/hardware will be associated, if any. 
4. CPU_ABI cpu指令集：The name of the instruction set (CPU type + ABI convention) of native code. 
5. CPU_ABI2 cpu指令集2：The name of the second instruction set (CPU type + ABI convention) of native code. 
6. DEVICE 设备参数：The name of the industrial design. 
7. DISPLAY 显示屏参数：A build ID string meant for displaying to the user 
8. FINGERPRINT 唯一识别码：A string that uniquely identifies this build. Do not attempt to parse this value. 
9. HARDWARE 硬件名称：The name of the hardware (from the kernel command line or /proc). 
10. HOST 
11. ID 修订版本列表：Either a changelist number, or a label like M4-rc20. 
12. MANUFACTURER 硬件制造商：The manufacturer of the product/hardware.（我们目前只需要关注这个静态属性即可）
13. MODEL 版本即最终用户可见的名称：The end-user-visible name for the end product. 
14. PRODUCT 整个产品的名称：The name of the overall product. 
15. RADIO 无线电固件版本：The radio firmware version number. 在API14后已过时。使用 getRadioVersion()代替。 
16. SERIAL 硬件序列号：A hardware serial number, if available. Alphanumeric only, case-insensitive. 
17. TAGS 描述build的标签,如未签名，debug等等。：Comma-separated tags describing the build, like unsigned,debug. 
18. TIME 
19. TYPE build的类型：The type of build, like user or eng. 
20. USER
```

### 打开其他应用程序中的Activity或服务(ComponentName) 
所属包： android.content.ComponentName 
构造方法使用方式如下： 
1.  传递当前上下文和将要跳转的类名； 
2.  传递一个String包名和String类名； 
3.  传递一个Parcel数据容器。 
需要关注的方法：unflattenFromString(“传递将要跳转的地址，格式为包名/跳转Activity Name”)


## 通过adb获取跳转包名路径
adb为我们提供了一个可以打印出当前系统所有service信息，在后面可加上具体的服务名的命令

`adb shell dumpsys`

```
获取设备电池信息：adb shell dumpsys battery
获取cpu信息：adb shell dumpsys cpuinfo
获取内存信息：adb shell dumpsys meminfo 
要获取具体应用的内存信息，可加上包名 
adb shell dumpsys meminfo PACKAGE_NAME
获取Activity信息：adb shell dumpsys activity
获取package信息：adb shell dumpsys package 
加上-h可以获取帮助信息 
获取某个包的信息：adb shell dumpsys package PACKAGE_NAME
获取通知信息：adb shell dumpsys notification
获取wifi信息：adb shell dumpsys wifi 
可以获取到当前连接的wifi名、搜索到的wifi列表、wifi强度等
获取电源管理信息：adb shell dumpsys power 
可以获取到是否处于锁屏状态：mWakefulness=Asleep或者mScreenOn=false
获取电话信息：adb shell dumpsys telephony.registry 
可以获取到电话状态，例如mCallState值为0，表示待机状态、1表示来电未接听状态、2表示电话占线状态 
mCallForwarding=false #是否启用呼叫转移 
mDataConnectionState=2 #0：无数据连接 1：正在创建数据连接 2：已连接mDataConnectionPossible=true #是否有数据连接mDataConnectionApn= #APN名称等
```

```
adb shell dumpsys activity top
//获取当前显示activity的详细信息，包括所属包名和activity名，activity的布局信息等等。
```

--------------------
参考资料  ： http://www.voidcn.com/article/p-dpiicqfm-zw.html

--------------------------
