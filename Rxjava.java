package com.example.macmini.download;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class Rxjava extends AppCompatActivity {

    private static final String TAG = "Rxjava";
    private int i = 0;
    private View.OnClickListener listener;
    private List<Integer> list = new ArrayList<>(0);
    private Subject<List<Integer>> subject;

    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.button6)
    Button button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        ButterKnife.bind(this);
        initDate();
        button4.setOnClickListener(listener);
    }

    @OnClick({R.id.button5, R.id.button6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button5:
                break;
            case R.id.button6:
                addInteger(i++);
                break;
        }
    }

    private void addInteger(int i) {
        list.add(i++);
        subject.onNext(list);
    }

    private void initDate() {
        Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(final ObservableEmitter<View> e) throws Exception {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!e.isDisposed()) {
                            e.onNext(v);
                        }
                    }
                };
            }
        }).subscribe(new Consumer<View>() {
            @Override
            public void accept(@NonNull View view) throws Exception {
                ((Button)view).setText(i++ + "---");
            }
        });

        subject = PublishSubject.create();
        subject.subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(@NonNull List<Integer> integers) throws Exception {
                Log.d(TAG, "accept: " + integers);
            }
        });


    }

    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
