package com.maple.recordwav.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;
import com.maple.msdialog.AlertDialog;
import com.maple.popups.utils.DensityUtils;
import com.maple.recordwav.R;
import com.maple.recordwav.databinding.ActivityMainBinding;
import com.maple.recordwav.utils.BottomTabView;
import com.maple.recordwav.utils.FragmentChangeManager;
import com.maple.recordwav.utils.ViewUtils;
import com.maple.recordwav.utils.permission.RxPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Record Wav 示例（Java版）
 */
public class MainActivityJava extends FragmentActivity {
    private ActivityMainBinding binding;
    private int tabIndex = 0;// 当前显示页面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewUtils.setPaddingTopWithStatusBar(binding.flTopBar);
        if (Build.VERSION.SDK_INT >= 30) {
            EdgeToEdge.enable(this,
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            );
            ViewCompat.setOnApplyWindowInsetsListener(binding.tlTab, new OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                    Insets ins = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    ViewGroup.LayoutParams params = v.getLayoutParams();
                    params.height = ins.bottom + DensityUtils.dp2px(MainActivityJava.this, 50);
                    v.setLayoutParams(params);
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), ins.bottom);
                    return insets;
                }
            });
        }

        initTitleBar();
        initView();
        requestPermission();
    }

    private void initTitleBar() {
        binding.tvTitle.setText("Wav 助手(Java)");
        binding.tvSwitch.setText("转Kotlin\n版本");
        binding.tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityJava.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        Fragment tab0 = getSupportFragmentManager().findFragmentByTag(FragmentChangeManager.Companion.getFragmentTag(0));
        fragmentList.add(tab0 != null ? tab0 : new RecordPageJava());
        Fragment tab1 = getSupportFragmentManager().findFragmentByTag(FragmentChangeManager.Companion.getFragmentTag(1));
        fragmentList.add(tab1 != null ? tab1 : new PlayParsePageJava());
        Fragment tab2 = getSupportFragmentManager().findFragmentByTag(FragmentChangeManager.Companion.getFragmentTag(2));
        fragmentList.add(tab2 != null ? tab2 : new AboutPage());

        int[] iconArr = {R.drawable.sel_tab_record_icon, R.drawable.sel_tab_play_icon, R.drawable.sel_tab_parse_icon};
        String[] titleArr = {getString(R.string.record), getString(R.string.play), getString(R.string.info)};

        for (int i = 0; i < fragmentList.size(); i++) {
            TabLayout.Tab tab = binding.tlTab.newTab();
            BottomTabView view = new BottomTabView(this);
            view.setIcon(iconArr[i], null);
            view.setTitle(titleArr[i]);
            tab.setCustomView(view);
            binding.tlTab.addTab(tab);
        }

        FragmentChangeManager fgManager = new FragmentChangeManager(getSupportFragmentManager(), R.id.fl_content, fragmentList, tabIndex);
        binding.tlTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabIndex = tab.getPosition();
                fgManager.setCurrentFragment(tabIndex);
                View customView = tab.getCustomView();
                if (customView != null && customView instanceof BottomTabView) {
                    ((BottomTabView) customView).setSelectStatus(true);
                }
                if (tabIndex == 1) {
                    binding.tlTab.setBackgroundResource(R.color.navigation_bar_bg2);
                } else if (tabIndex == 2) {
                    binding.tlTab.setBackgroundResource(R.color.navigation_bar_bg3);
                } else {
                    binding.tlTab.setBackgroundResource(R.color.navigation_bar_bg);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null && customView instanceof BottomTabView) {
                    ((BottomTabView) customView).setSelectStatus(false);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        TabLayout.Tab tab = binding.tlTab.getTabAt(tabIndex);
        if (tab != null) {
            tab.select();
        }
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        new RxPermissions(this).request(
                Manifest.permission.RECORD_AUDIO
        ).subscribe(granted -> {
            if (!granted) {
                new AlertDialog(MainActivityJava.this)
                        .setDialogTitle("权限不足！")
                        .setMessage("录音必须要有“RECORD_AUDIO”权限哦，否则无法录音和存储。")
                        .setLeftButton("退出", v -> MainActivityJava.this.finish())
                        .setRightButton("再选一次", v -> requestPermission())
                        .setDialogCancelable(false)
                        .show();
            }
        });
    }

}
