package com.hrm.baidusdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hrm.baidusdk.activity.CrashActivity;
import com.hrm.baidusdk.customize.floatbutton.DragFloatActionButton;
import com.hrm.baidusdk.customize.banner.Banner;
import com.hrm.baidusdk.customize.banner.loader.BannerEntry;
import com.hrm.baidusdk.customize.banner.loader.BannerItem;
import com.hrm.baidusdk.customize.banner.loader.ImageLoader;
import com.hrm.baidusdk.customize.floatbutton.DragFloatButtonClickListener;
import com.hrm.baidusdk.setting.Permission;
import com.hrm.baidusdk.util.MainHandler;
import com.hrm.baidusdk.util.ThreadTask;
import com.hrm.baidusdk.util.ToastUtils;
import com.hrm.baidusdk.zhihu.HttpClientUtils;
import com.hrm.baidusdk.zhihu.ViewZhiHuAnswerPicture;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/20
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private PoiSearch mPoiSearch;

    private MapView mMapView = null;

    private List<PoiInfo> poiInfoList = new ArrayList<>();

    private DragFloatActionButton button;

    private Banner banner;
    private List<BannerItem> bannerEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();

        initData();

        initView();
    }

    private void initView() {
        button = (DragFloatActionButton) findViewById(R.id.floatButton);
        button.setOnClickListener(dragFloatButtonClickListener);

        banner = findViewById(R.id.banner);
    }

    private void initData() {
        Log.d(TAG, Environment.getExternalStorageDirectory().getPath());
    }

    private void initBanner() {
        banner.setBannerLoader(new ImageLoader() {
            @Override
            public void loadView(Context context, BannerEntry entry, int position, View imageView) {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.no_banner)
                        .error(R.drawable.no_banner)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                Glide.with(context).load(entry.getBannerPath()).apply(requestOptions).into((ImageView) imageView);
            }
        });
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void onBannerClick(int position) {
                ToastUtils.makeTextShort(getApplicationContext(), "点击了 " + position);
            }
        });

        banner.setBannerPagerChangedListener(new Banner.OnBannerPagerChangedListener() {
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: ==> " + position);
            }
        });
        banner.loadImagePaths(bannerEntries);
    }

    DragFloatButtonClickListener dragFloatButtonClickListener = new DragFloatButtonClickListener() {
        @Override
        public void onClick() {
            Intent intent = new Intent(MainActivity.this, CrashActivity.class);
            startActivity(intent);
        }

        @Override
        public void onLongClick() {
            Log.d(TAG, "被长按了");
        }
    };


    OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            poiInfoList = poiResult.getAllPoi();
            for (PoiInfo info : poiInfoList) {
                Log.d(TAG, info.name);
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        banner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
        banner.stopAutoPlay();
    }

    private void initPermission() {
        Permission permission = new Permission(this);
        permission.checkPermission();
    }
}