package com.hrm.baidusdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
//        bannerEntries.add(new BannerItem("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565154160894&di=a177dc95e640d1c9564000ba8682725d&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fback_pic%2Fqk%2Fback_origin_pic%2F00%2F02%2F72%2Fs_1198_e160f1f6a2a449109f20536ad2100ae0.jpg",null));
//        bannerEntries.add(new BannerItem("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565154160894&di=527d9758d2e2518ad7e0b52236871665&imgtype=0&src=http%3A%2F%2Fku.90sjimg.com%2Fback_pic%2F00%2F03%2F20%2F58561dc0b9c8bd7.jpg",null));
//        bannerEntries.add(new BannerItem("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606671670924&di=de0d442dfdc7a320608d3176c87da235&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn20200226ac%2F350%2Fw690h460%2F20200226%2F5bfd-ipzreiw1736185.jpg",null));
//        bannerEntries.add(new BannerItem("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565154160895&di=791b5647e6f58fc49ba134a09a4fc77a&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Fback_pic%2F04%2F95%2F44%2F605925c4ae73de4.jpg",null));
        ThreadTask.getInstance().executorNetThread(new Runnable() {
            @Override
            public void run() {
                HttpClientUtils httpClientUtils = new HttpClientUtils();
                List<String> urls = httpClientUtils.getImageUrl();
                for (int i = 0; i < Math.min(urls.size(), 5); i++) {
                    bannerEntries.add(new BannerItem(urls.get(i), null));
                }
                MainHandler.get(new Runnable() {
                    @Override
                    public void run() {
                        initBanner();
                    }
                });
            }
        });


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
            Intent intent = new Intent(MainActivity.this, ViewZhiHuAnswerPicture.class);
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