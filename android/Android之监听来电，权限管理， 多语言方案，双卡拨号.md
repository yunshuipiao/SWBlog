

### 有关权限管理
Anroid6.0以下， 权限申明后即可获取（国产定制系统除外）
在manifest文件里声明权限：
```
//电话相关权限
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
<uses-permission android:name="android.permission.CALL_PHONE"/>
```

6.0以上不仅要声明，还需运行时获取。
步骤大概如下：
![](https://github.com/yunshuipiao/SWBlog/blob/master/media/picture/%E6%9D%83%E9%99%90%E5%A4%84%E7%90%86%E6%B5%81%E7%A8%8B.png)

已打电话权限为例说明（一般情况）：
```
    private void checkAndRquestCallPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkCallPermission: " + "没有打电话权限");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);// 1:code
        }
    }
```

```
    //重载activity的onRequestPermissionsResult方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (1 == grantResults.length && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                Log.d(TAG, "onRequestPermissionsResult: " + "已获取：" + permissions[0] + " 权限");
            }     
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
```

对shouldShowRequestPermissionRationale方法的补充说明：
在请求权限时，这个函数用来可以用来给用户提示为什么需要权限（用户没有勾选不再提示按钮，第一次请求或是拒绝后返回ture）：
```
    private void checkAndRquestCallPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkCallPermission: " + "没有电话权限");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                Log.d(TAG, "checkCallPermission: " + "need phone permission");
                //弹出对话框，提示用户允许或者拒绝，允许则请求权限
            }
            //此处默认允许
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);// 1:code
        }
    }
```

```
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (1 == grantResults.length && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                Log.d(TAG, "onRequestPermissionsResult: " + "以获取：" + permissions[0] + " 权限");
            }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
//                  //用户没有勾选不再提示，并拒绝
                    Log.d(TAG, "onRequestPermissionsResult: " + "refuse one time");
                } else {
//                    用户勾选提示并拒绝
                    Log.d(TAG, "onRequestPermissionsResult: " + "refuse forever");
                    //跳转权限设置页
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
```


根据测试，小米机型貌似没有可勾选功能，一次拒绝默认永远拒绝。下次申请到权限设置页。
（国产机型区分对待）
具体代码参考
[https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/MainActivity.java](https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/MainActivity.java)


### 来电监听和拨打监听
简单介绍此功能的实现
**在有电话权限的前提下**
**在有电话权限的前提下**

功能比较简单，看代码就行：
继承BroadcastReceiver类处理即可：
```
public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneReceiver";
    private boolean mInComingFlag = false;
    private static String ACTION_NEW_INCOMMING_CALL = "android.intent.action.PHONE_STATE";
    private PhoneStateListener listen = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话打进响铃中，电话打出没有此状态
                    mInComingFlag = true;
                    Log.d(TAG, "onCallStateChanged: " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //挂断电话
                    if (mInComingFlag) {
                        Log.d(TAG, "onCallStateChanged: " + "call hang up");
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //电话接听
                    if (mInComingFlag) {
                        Log.d(TAG, "onCallStateChanged: " + "接听电话：" + incomingNumber);
                    }
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //电话打出
            mInComingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "onReceive: " + "call phoneNumber：" + phoneNumber);
        }
        if (intent.getAction().equals(ACTION_NEW_INCOMMING_CALL)) {
            //电话打进
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(listen, PhoneStateListener.LISTEN_CALL_STATE);

        }
    }
}
```
接着在manifest声明该receiver和需要过滤的广播
```
        <receiver android:name=".phone.PhoneReceiver"
                  android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.NEW_OUTGOING_CALL"/>
                <action android:name="android.intent.action.PHONE_STATE"/>
                <action android:name="android.intent.action.MY_SELF_RECEIVER"/>
            </intent-filter>
        </receiver>
```

自定义广播发送并接收（同上）
```
                //发送自定义广播，PhoneReceiver接收
                Intent intent = new Intent(ACTION);
                intent.putExtra("Msg", "helloReceiver");
                sendBroadcast(intent);
```
完整代码参考
[https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/phone/PhoneReceiver.java](https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/phone/PhoneReceiver.java)

### 多语言方案实现
```
    //选择语言并保存状态
    private void changeLanguage() {
        //弹出对话框或者其他方式选择语言，并持久化保存到本地
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"auto", "English", "简体中文"},
                getSharedPreferences("language", Context.MODE_PRIVATE).getInt("language", 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        Log.d(TAG, "onClick: " + which);
                        //保存设置
                        editor.putInt("language", which);
                        editor.apply();
                        dialog.dismiss();

                        Intent intent = new Intent(MultiLanguageActivity.this, MultiLanguageActivity.class);
                        //重新打开一个返回栈并清除前者
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
```

```
    //加载所保存的语言
    private void setLanguage() {
        SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        int language = preferences.getInt("language", 0);

        Resources resources  = getResources();
        DisplayMetrics dispalyMetRics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        switch (language) {
            case 0:
                configuration.setLocale(Locale.getDefault());
                break;
            case 1:
                configuration.setLocale(Locale.ENGLISH);
                break;
            case 2:
                configuration.setLocale(Locale.CHINESE);
                break;
        }
        // FIXME: 2017/6/5
        resources.updateConfiguration(configuration, dispalyMetRics);
    }
```

添加语言文件strings：
![](https://github.com/yunshuipiao/SWBlog/blob/master/media/picture/%E5%A4%9A%E8%AF%AD%E8%A8%80%E6%96%B9%E6%A1%88.png)

因为对ActionBar不起作用，因此需要调用以下方法设置title显示。
```
getSupportActionBar().setTitle(R.string.app_name);
```
完整代码参考
[https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/Language/MultiLanguageActivity.java](https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/Language/MultiLanguageActivity.java)

### 双卡双待手机拨打电话（暂无监听双卡双待电话接听情况）

判断当前sim使用情况：
```
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private int checkDualSim() {
        int simNumber = 0;
        SubscriptionManager sm = SubscriptionManager.from(this);
        List<SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
        if (subs == null) {
            d(TAG, "checkDualSim: " + "no sim");
            return simNumber;
        }
        if (subs.size() > 1) {
            simNumber = 2;
            d(TAG, "checkDualSim: " + "two sims");
        } else {
            d(TAG, "checkDualSim: " + "one sim");
            simNumber = 1;
        }
        for (SubscriptionInfo s: subs) {
            d(TAG, "checkDualSim: " + "simInfo:" + subs.toString());
        }
        return simNumber;
    }
```

```
//根据上述情况，初始化UI和拨打电话，尤其注意sim2的打电话情况
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callPhone(boolean isDualSim) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "callPhone: " + "no call phone permission");
            return;
        }
        String phoneNumber = mBinding.etPhoneNumber.getText().toString().trim();
        phoneNumber = TextUtils.isEmpty(phoneNumber) ? "13422284669" : phoneNumber;
        if (!isDualSim) {
            //单卡
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
            return;
        }
        TelecomManager telecomManager = (TelecomManager)getSystemService(Context.TELECOM_SERVICE);
        if(telecomManager != null) {
            List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            d(TAG, "callPhone: " + phoneAccountHandleList);
            d(TAG, "callPhone: " + phoneAccountHandleList.get(1).toString());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandleList.get(1));
            startActivity(intent);
        }
    }
```

完成代码参考
[https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/phone/DualSimCallActivity.java](https://github.com/yunshuipiao/SWBase/blob/Sbranch/app/src/main/java/com/macmini/swensun/swbase/phone/DualSimCallActivity.java)

以上情况实现比较粗略，涉及工业代码和机型适配还需细细斟酌。



