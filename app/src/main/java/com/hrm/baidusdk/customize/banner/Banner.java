package com.hrm.baidusdk.customize.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hrm.baidusdk.BuildConfig;
import com.hrm.baidusdk.R;
import com.hrm.baidusdk.customize.banner.constant.BannerConfig;
import com.hrm.baidusdk.customize.banner.loader.BannerEntry;
import com.hrm.baidusdk.customize.banner.loader.BannerLoader;
import com.hrm.baidusdk.customize.banner.transformer.complex.AlphaPageTransformer;
import com.hrm.baidusdk.customize.banner.transformer.complex.NonPageTransformer;
import com.hrm.baidusdk.customize.banner.transformer.complex.RotateDownPageTransformer;
import com.hrm.baidusdk.customize.banner.transformer.complex.RotateUpPageTransformer;
import com.hrm.baidusdk.customize.banner.transformer.complex.RotateYTransformer;
import com.hrm.baidusdk.customize.banner.transformer.complex.ScaleInTransformer;
import com.hrm.baidusdk.customize.banner.transformer.complex.ScaleYTransformer;
import com.hrm.baidusdk.customize.banner.transformer.simple.AccordionTransformer;
import com.hrm.baidusdk.customize.banner.transformer.simple.DefaultTransformer;
import com.hrm.baidusdk.customize.banner.widget.BannerScroller;
import com.hrm.baidusdk.customize.banner.widget.BannerViewPager;
import com.hrm.baidusdk.customize.banner.widget.WeakHandler;
import com.hrm.baidusdk.util.ResolutionConversion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public class Banner extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private static final String TAG = "banner";
    private static final int NORMAL_EXTRA_NUM = 2;
    private static final int MULTI_PAGE_EXTRA_NUM = 4;
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private BannerViewPager mViewPager;
    private LinearLayout mIndicatorLl;
    private ImageView mDefaultImg;

    private WeakHandler mWeakHandler = new WeakHandler();
    private ArrayList<View> mBannerItems = new ArrayList<>();
    private List<BannerEntry> mBannerEntry = new ArrayList<>();

    private BannerPagerAdapter mBannerPagerAdapter;

    //能否自动轮播
    private boolean mCanAutoPlay;
    //是否一屏多页
    private int mShowModel;
    //是否显示指示器
    private boolean mShowIndicator;
    //轮播间隔时长
    private int mIntervalTime;
    //VP滚动时长
    private int mPagerScrollTime;
    //左右页可见的宽度
    private int mExposeWidth;
    //页面间距
    private int mPageSpacing;
    //魅族模式重叠尺寸
    private int mOverlapSize;
    //单页轮播动画
    private int mSingleTransform;
    //多页轮播动画
    private int mMultiTransform;
    //仿魅族Banner动画
    private int mMzTransform;
    //Banner无数据默认显示
    private int mDefaultBanner;
    //指示器选中宽度
    private int mIndicatorSelectedW;
    //指示器默认宽度
    private int mIndicatorDefaultW;
    //指示器选中高度
    private int mIndicatorSelectedH;
    //指示器默认高度
    private int mIndicatorDefaultH;
    //指示器间距
    private int mIndicatorSpacing;
    //指示器距离底部高度
    private int mIndicatorMarginBottom;
    //指示器选中样式
    private int mIndicatorSelectStyle;
    //指示器默认样式
    private int mIndicatorDefaultStyle;

    //加载ViewPager页面实例
    private BannerLoader mBannerLoader;
    //banner摘要
    private TextView mBannerText;
    //当前Banner下标
    private int mCurrentIndex = 1;
    //当前指示器位置
    private int mCurIndicatorIndex = 0;
    //需要的页数
    private int mNeedPagers;
    //实际页数
    private int mRealPagers;

    public Banner(Context context) {
        super(context);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.merge_banner, this);
        initAttributes(context, attrs);
        initView();
        initViewPagerScroll();
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.merge_banner, this);
        initAttributes(context, attrs, defStyleAttr);
        initView();
        initViewPagerScroll();
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        mCanAutoPlay = typedArray.getBoolean(R.styleable.Banner_banner_auto_play, BannerConfig.CAN_AUTO_PLAY);
        mShowModel = typedArray.getInteger(R.styleable.Banner_banner_show_model, BannerConfig.SINGLE);
        mShowIndicator = typedArray.getBoolean(R.styleable.Banner_banner_show_indicator, BannerConfig.SHOW_INDICATOR);
        mDefaultBanner = typedArray.getResourceId(R.styleable.Banner_banner_default_image, R.drawable.no_banner);
        mIntervalTime = typedArray.getInteger(R.styleable.Banner_banner_interval_time, BannerConfig.INTERVAL_TIME);
        mPagerScrollTime = typedArray.getInteger(R.styleable.Banner_banner_scroll_time, BannerConfig.SCROLL_TIME);
        mExposeWidth = (int) typedArray.getDimension(R.styleable.Banner_banner_expose_width, BannerConfig.EXPOSE_WIDTH);
        mOverlapSize = (int) typedArray.getDimension(R.styleable.Banner_banner_mz_overlap, BannerConfig.PAGE_SPACING);
        mPageSpacing = (int) typedArray.getDimension(R.styleable.Banner_banner_page_spacing, BannerConfig.PAGE_SPACING);
        mIndicatorSelectedW = (int) typedArray.getDimension(R.styleable.Banner_indicator_select_width, ResolutionConversion.dip2px(context, 8));
        mIndicatorDefaultW = (int) typedArray.getDimension(R.styleable.Banner_indicator_default_width, ResolutionConversion.dip2px(context, 8));
        mIndicatorSelectedH = (int) typedArray.getDimension(R.styleable.Banner_indicator_select_height, ResolutionConversion.dip2px(context, 8));
        mIndicatorDefaultH = (int) typedArray.getDimension(R.styleable.Banner_indicator_default_height, ResolutionConversion.dip2px(context, 8));
        mIndicatorSpacing = (int) typedArray.getDimension(R.styleable.Banner_indicator_space, ResolutionConversion.dip2px(context, 6));
        mIndicatorSelectStyle = typedArray.getResourceId(R.styleable.Banner_indicator_select_drawable, R.drawable.shape_banner_select_indicator);
        mIndicatorDefaultStyle = typedArray.getResourceId(R.styleable.Banner_indicator_default_drawable, R.drawable.shape_banner_default_indicator);
        mSingleTransform = typedArray.getInteger(R.styleable.Banner_banner_single_anim, BannerConfig.PAGER_TRANSFORM);
        mMultiTransform = typedArray.getInteger(R.styleable.Banner_banner_multi_anim, BannerConfig.PAGER_TRANSFORM);
        mMzTransform = typedArray.getInteger(R.styleable.Banner_banner_mz_anim, BannerConfig.PAGER_TRANSFORM);
        typedArray.recycle();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        initAttributes(context, attrs, 0);
    }

    private void initView() {
        mViewPager = (BannerViewPager) findViewById(R.id.banner_vp);
        mIndicatorLl = (LinearLayout) findViewById(R.id.indicator_ll);
        mDefaultImg = (ImageView) findViewById(R.id.default_iv);
        //Banner数据为空时默认显示图片
        mDefaultImg.setImageResource(mDefaultBanner);
        initIndicator();
        initMultiPage();
        initBannerTransformer();
    }

    /**
     * 切换多页
     */
    private void initMultiPage() {
        //非一屏三页，直接 return
        if (mShowModel == BannerConfig.SINGLE) return;
        //避免复数
        if (mExposeWidth < 0) mExposeWidth = 0;
        if (mPageSpacing < 0) mPageSpacing = 0;
        if (mOverlapSize < 0) mOverlapSize = 0;
        //关键之处：
        //1.当clipChildren为 false 时，超出 View 的子页面，不会被切掉，仍然可以显示
        //2.设置为true，你们不管你的子View设置为多大子View左右的View都不会显示，会用空白代替
        //3.设置setPagerMargin()
        setClipChildren(false);
        LayoutParams params = (LayoutParams) mViewPager.getLayoutParams();
        //仿魅族时，暴露的宽度即为 margin
        int margin = mShowModel == BannerConfig.MULTI ? (mExposeWidth + mPageSpacing) : mExposeWidth;
        params.leftMargin = margin;
        params.rightMargin = margin;
        mViewPager.setLayoutParams(params);
        //仿魅族Banner重要设置
        if (mShowModel == BannerConfig.MZ_EFFECT) {
            mViewPager.setEnableMzEffects(true);
        }
        //mShowModel == 2 正常一屏三页，显示正整数间隔
        //mShowModel == 3 仿魅族 Banner
        setPagerMargin(mShowModel == BannerConfig.MULTI ? mPageSpacing : -mOverlapSize);
        //左右预加载两页即可，太多会加大内存消耗
        setOffscreenPageLimit(2);
    }

    /**
     * 初始化指示器
     */
    private void initIndicator() {
        if (mShowIndicator) {
            mIndicatorLl.setVisibility(VISIBLE);
        }
        int defaultMargin = ResolutionConversion.dip2px(getContext(), 16);
        LayoutParams params = (LayoutParams) mIndicatorLl.getLayoutParams();
        if (mShowModel == BannerConfig.MULTI) {
            params.leftMargin = mExposeWidth + mPageSpacing + defaultMargin;
            params.rightMargin = mExposeWidth + mPageSpacing + defaultMargin;
            params.bottomMargin = mIndicatorMarginBottom;
        } else if (mShowModel == BannerConfig.MZ_EFFECT) {
            params.leftMargin = mExposeWidth + defaultMargin;
            params.rightMargin = mExposeWidth + defaultMargin;
            params.bottomMargin = mIndicatorMarginBottom;
        } else {
            params.leftMargin = defaultMargin;
            params.rightMargin = defaultMargin;
            params.bottomMargin = mIndicatorMarginBottom;
        }
        mIndicatorLl.setLayoutParams(params);
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            BannerScroller bannerScroller = new BannerScroller(mViewPager.getContext());
            bannerScroller.setDuration(mPagerScrollTime);
            mField.set(mViewPager, bannerScroller);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCanAutoPlay) {
            //按下时，结束轮播
            //其他情况，正常轮播
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay();
                if (DEBUG) {
                    Log.d(TAG, "dispatchTouchEvent: 开始轮播");
                }
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoPlay();
                if (DEBUG) {
                    Log.d(TAG, "dispatchTouchEvent: 取消轮播");
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mBannerPagerChangedListener != null) {
            mBannerPagerChangedListener.onPageScrolled(findRealPosition(position),
                    positionOffset,
                    positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentIndex = position;
        setSelectedIndicator(findRealPosition(position));
        //        //防止此处下标时，重复回调
//        if (mShowModel == BannerConfig.SINGLE) {
//            if (position == 1) return;
//        } else {
//            if (position == 2) return;
//        }
        if (mBannerPagerChangedListener != null) {
            mBannerPagerChangedListener.onPageSelected(findRealPosition(position));
        }
        if (mBannerText != null) {
            mBannerText.setText(mBannerEntry.get(position).getIndicatorText());
        }
        Log.d(TAG, "onPageSelected: 当前位置：" + mCurrentIndex
                + "\n"
                + "实际位置：" + findRealPosition(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mBannerPagerChangedListener != null) {
            mBannerPagerChangedListener.onPageScrollStateChanged(state);
        }
        // 从名字可以看出这个方法主要用来监测viewpager的滑动状态：
        // 当viewpager处于空闲状态时 state=0，
        // 当我们手指按下时 state=1，
        // 当我们手指抬起时 state=2，
        // 所以我们完全可以在state=0时 去加载或者处理我们的事情，因为这时候滑动已经结束。

        //在我们快速滑动过程中，state=0只有在空闲时才会调用
        //这样当我们滑到第一页或者最后一页时，没有及时的过渡，导致我们滑不动，
        //但是state=1，滑动过程中总有按下或者抬起，这样他会在每次翻页时触发
        //用来及时的处理我们的逻辑再好不过，这样就完美避免了滑不动的尴尬

        //多点触碰滑动，不做处理，你非要这样，我也没办法
        switch (state) {
            case 0:
                if (DEBUG) {
                    Log.d(TAG, "onPageScrollStateChanged: 空闲");
                }
                break;
            case 1:
                if (mShowModel == BannerConfig.MULTI || mShowModel == BannerConfig.MZ_EFFECT) {
                    if (mCurrentIndex == 1) {
                        //过渡到真实的最后一页(即：倒数第三页)
                        mViewPager.setCurrentItem(mNeedPagers - 3, false);
                    } else if (mCurrentIndex == mNeedPagers - 2) {
                        //处于倒数第二张，为Banner第一张图
                        //过渡到真实第一张(即：下标为2)
                        mViewPager.setCurrentItem(2, false);
                    }
                } else {
                    if (mCurrentIndex == 0) {
                        //处于第一页，为Banner最后一张图
                        //过渡到真实的最后一页(即：倒数第二页)
                        mViewPager.setCurrentItem(mNeedPagers - 2, false);
                    } else if (mCurrentIndex == mNeedPagers - 1) {
                        //处于最后一页，为banner第一张图
                        //过渡到真实的第一页(即：下标为1)
                        mViewPager.setCurrentItem(1, false);
                    }
                }
                if (DEBUG) {
                    Log.d(TAG, "onPageScrollStateChanged: 按下拖拽：" + mCurrentIndex);
                }
                break;
            case 2:
                // TODO: 2018/12/3
                if (DEBUG) {
                    Log.d(TAG, "onPageScrollStateChanged: 抬起");
                }
                break;
            default:
                break;
        }
    }

    public void loadImagePaths(List<? extends BannerEntry> imagePaths) {
        //说明loadImagePaths()方法不是第一次调用
        //需要进行初始化
        if (!mBannerEntry.isEmpty()) {
            mWeakHandler.removeCallbacks(mBannerPlayRunnable);
            mBannerEntry.clear();
            mBannerItems.clear();
            mRealPagers = 0;
            mNeedPagers = 0;
            mCurIndicatorIndex = 0;
            //轮播速度过快时，防止数据清空，VP正好再执行任务，出现异常
            if (mIntervalTime > 3000 && mBannerPagerAdapter != null) {
                mViewPager.setAdapter(mBannerPagerAdapter);
            }
        }
        //清除第一页时，添加的View
        View lastOnePager = findViewById(R.id.only_one_pager);
        if (lastOnePager != null) removeView(lastOnePager);
        if (imagePaths == null || imagePaths.isEmpty()) {
            mDefaultImg.setVisibility(View.VISIBLE);
            return;
        }
        mDefaultImg.setVisibility(View.GONE);
        mRealPagers = imagePaths.size();
        //处理仅有一页时的情况
        if (imagePaths.size() == 1) {
            createOnlyOnePager(imagePaths.get(0));
            if (mBannerPagerAdapter != null) {
                //有多张转为一张时，数据源发生变化，此处因为return
                //没有及时notifyDataSetChanged(),出现如下异常
                // The application's PagerAdapter changed the adapter's contents without
                // calling PagerAdapter#notifyDataSetChanged!
                //此处避免异常，应该notifyDataSetChanged
                mBannerPagerAdapter.notifyDataSetChanged();
            }
            return;
        }

        //正常流程

        //创建指示器
        createDefaultIndicator(mRealPagers);
        //数据源处理
        if (mShowModel == BannerConfig.MULTI || mShowModel == BannerConfig.MZ_EFFECT) {
            //多添加 4 页
            mNeedPagers = mRealPagers + MULTI_PAGE_EXTRA_NUM;
            //mImagePaths第一个元素，为Banner倒数第二张图片
            mBannerEntry.add(imagePaths.get(mRealPagers - 2));
            //mImagePaths第二个元素，为Banner倒数第一张图片
            mBannerEntry.add(imagePaths.get(mRealPagers - 1));
            mBannerEntry.addAll(imagePaths);
            //mImagePaths倒数第二个元素，为Banner第一张图片
            mBannerEntry.add(imagePaths.get(0));
            //mImagePaths倒数第一个元素，为Banner第二张图片
            mBannerEntry.add(imagePaths.get(1));
        } else {
            //多添加 2 页
            mNeedPagers = mRealPagers + NORMAL_EXTRA_NUM;
            //mImagePaths第一个元素，为 Banner 最后一张图
            mBannerEntry.add(imagePaths.get(mRealPagers - 1));
            mBannerEntry.addAll(imagePaths);
            //mImagePaths最后一个元素，为 Banner 第一张图
            mBannerEntry.add(imagePaths.get(0));
        }
        //通知更新数据
        notifyBannerData();
        Log.d(TAG, "loadImagePaths: Banner所需元素： " + mBannerEntry);
    }

    /**
     * 更新数据
     */
    private void notifyBannerData() {
        //Banner 的起始位置
        if (mShowModel == BannerConfig.MULTI || mShowModel == BannerConfig.MZ_EFFECT) {
            mCurrentIndex = 2;
        } else {
            mCurrentIndex = 1;
        }
        //适配器创建
        if (mBannerPagerAdapter == null) {
            mBannerPagerAdapter = new BannerPagerAdapter();
            mViewPager.addOnPageChangeListener(this);
        }
        //此处做一个简单说明
        //mBannerPagerAdapter.notifyDataSetChanged()无法重置Vp的初始状态，他只是销毁当前页面
        //以及左右缓存的页面，然后再重新创建销毁的页面实例，但仍然显示当前页，因为 VP中mCurItem = 0;scrollTo(0, 0);
        //没法重置，所以直接设置适配器，重置VP状态

        //如果notifyDataSetChanged()，然后再setCurrentItem(mCurrentIndex, false)，
        //这样处理，会先实例当前页以及需要缓存的页面，然后再回到mCurrentIndex，再实例mCurrentIndex页以及其左右
        //缓存页，感觉有些得不偿失

        //所以不如直接设置一次适配器
        mViewPager.setAdapter(mBannerPagerAdapter);
        mViewPager.setFocusable(true);
        mViewPager.setCurrentItem(mCurrentIndex, false);
        //是否自行滚动
        if (mCanAutoPlay) startAutoPlay();
    }

    private void createOnlyOnePager(BannerEntry entry) {
        View pagerOne = mBannerLoader.createView(mViewPager.getContext(), 0);
        mBannerLoader.loadView(mViewPager.getContext(), entry, 0, pagerOne);
        pagerOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBannerClickListener != null) {
                    mOnBannerClickListener.onBannerClick(0);
                }
            }
        });
        pagerOne.setId(R.id.only_one_pager);
        LayoutParams onePagerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(pagerOne, onePagerParams);
    }

    /**
     * 创建指示器
     *
     * @param size
     */
    private void createDefaultIndicator(int size) {
        if (!mShowIndicator) return;
        mIndicatorLl.removeAllViews();
        while (size > 0) {
            View view = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorDefaultW, mIndicatorDefaultH);
            params.setMargins(mIndicatorSpacing, 0, 0, 0);
            view.setLayoutParams(params);
            view.setBackgroundResource(mIndicatorDefaultStyle);
            mIndicatorLl.addView(view);
            size--;
        }
    }

    /**
     * 设置选中指示器
     *
     * @param aimsPosition
     */
    private void setSelectedIndicator(int aimsPosition) {
        if (!mShowIndicator) return;
        int childCount = mIndicatorLl.getChildCount();
        if (aimsPosition > childCount - 1 || mCurIndicatorIndex > childCount - 1) return;
        View beforeChild = mIndicatorLl.getChildAt(mCurIndicatorIndex);
        ViewGroup.LayoutParams lastChildLayoutParams = beforeChild.getLayoutParams();
        lastChildLayoutParams.width = mIndicatorDefaultW;
        lastChildLayoutParams.height = mIndicatorDefaultH;
        beforeChild.setLayoutParams(lastChildLayoutParams);
        beforeChild.setBackgroundResource(mIndicatorDefaultStyle);
        View selectChild = mIndicatorLl.getChildAt(aimsPosition);
        ViewGroup.LayoutParams selectChildLayoutParams = selectChild.getLayoutParams();
        selectChildLayoutParams.width = mIndicatorSelectedW;
        selectChildLayoutParams.height = mIndicatorSelectedH;
        selectChild.setLayoutParams(selectChildLayoutParams);
        selectChild.setBackgroundResource(mIndicatorSelectStyle);
        mCurIndicatorIndex = aimsPosition;
    }

    /**
     * 设置页间边距
     * 仅限于一屏三页
     *
     * @param marginPixels
     */
    public void setPagerMargin(int marginPixels) {
        if (mViewPager == null) return;
        if (mShowModel == BannerConfig.MULTI || mShowModel == BannerConfig.MZ_EFFECT) {
            mViewPager.setPageMargin(marginPixels);
        }
    }

    /**
     * 设置Banner 预加载页数
     *
     * @param limit
     * @return
     */
    public Banner setOffscreenPageLimit(int limit) {
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    /**
     * 设置自动滚动
     *
     * @param mCanAutoPlay
     */
    public void setAutoPlay(boolean mCanAutoPlay) {
        this.mCanAutoPlay = mCanAutoPlay;
    }

    /**
     * 设置轮播时长
     *
     * @param mIntervalTime
     */
    public void setIntervalTime(int mIntervalTime) {
        this.mIntervalTime = mIntervalTime;
    }

    /**
     * 设置 Banner 加载器
     *
     * @param mBannerLoader
     */
    public void setBannerLoader(BannerLoader mBannerLoader) {
        this.mBannerLoader = mBannerLoader;
    }

    /**
     * 设置 Banner 摘要的 TextView
     *
     * @param mBannerText
     */
    public void setBannerText(TextView mBannerText) {
        this.mBannerText = mBannerText;
    }

    public Banner setBannerTransformer(ViewPager.PageTransformer transformer) {
        try {
            if (mViewPager != null) {
                mViewPager.setPageTransformer(true, transformer);
            }
        } catch (Exception e) {
            Log.e(TAG, "Please set the PageTransformer class");
        }
        return this;
    }

    private void initBannerTransformer() {
        if (mShowModel == BannerConfig.SINGLE) {
            setSingleTransformer(mSingleTransform);
        } else if (mShowModel == BannerConfig.MULTI) {
            setMultiTransformer(mMultiTransform);
        } else {
            setMzTransformer(mMzTransform);
        }
    }

    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mBannerEntry.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (mBannerLoader == null) {
                throw new RuntimeException("[Banner] --> The mBannerLoader is not null");
            }
            while (mBannerItems.size() <= position) {
                mBannerItems.add(null);
            }
            View bannerPager = mBannerItems.get(position);
            if (bannerPager == null) {
                final int realPosition = findRealPosition(position);
                bannerPager = mBannerLoader.createView(mViewPager.getContext(), realPosition);
                mBannerLoader.loadView(mViewPager.getContext(), mBannerEntry.get(position), realPosition, bannerPager);
                mBannerItems.set(position, bannerPager);
                bannerPager.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnBannerClickListener != null) {
                            mOnBannerClickListener.onBannerClick(realPosition);
                        }
                    }
                });
            }
            container.addView(bannerPager);
            return bannerPager;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private int findRealPosition(int position) {
        if (mShowModel == BannerConfig.MULTI || mShowModel == BannerConfig.MZ_EFFECT) {
            if (position == 1) {
                //Last Pager
                return mRealPagers - 1;
            } else if (position == 2 || mNeedPagers - 2 == position) {
                //First Pager
                return 0;
            } else {
                return position - 2;
            }
        } else {
            if (position == 0) {
                //Last Pager
                return mRealPagers - 1;
            } else if (position == mNeedPagers - 1) {
                //First Pager
                return 0;
            } else {
                return position - 1;
            }
        }
    }

    /**
     * 开始自动轮播
     * 用于 onStart/onResume
     * 便于更好的用于体验
     */
    public void startAutoPlay() {
        if (mCanAutoPlay && mRealPagers > 1) {
            mWeakHandler.removeCallbacks(mBannerPlayRunnable);
            mWeakHandler.postDelayed(mBannerPlayRunnable, mIntervalTime);
        }
    }

    /**
     * 结束自动轮播
     * 用于onPause/onStop
     * 便于更好的用于体验
     */
    public void stopAutoPlay() {
        if (mCanAutoPlay && mRealPagers > 1) {
            mWeakHandler.removeCallbacks(mBannerPlayRunnable);
        }
    }

    private final Runnable mBannerPlayRunnable = new Runnable() {
        @Override
        public void run() {
            mCurrentIndex++;
            if (mShowModel == BannerConfig.MULTI || mShowModel == BannerConfig.MZ_EFFECT) {
                //一屏三页
                if (mCurrentIndex == mNeedPagers - 1) {
                    //当前处于倒数第二页(因为mCurrentIndex++)，为Banner第一张图
                    //此时过渡到真实的第一页（即下标为2）
                    mViewPager.setCurrentItem(2, false);
                    mWeakHandler.post(mBannerPlayRunnable);
                } else {
                    mViewPager.setCurrentItem(mCurrentIndex);
                    mWeakHandler.postDelayed(mBannerPlayRunnable, mIntervalTime);
                }
            } else {
                //一屏一页
                if (mCurrentIndex == mNeedPagers) {
                    //当下标=页数,说明此时正在显示"第一页"
                    //即：处于最后一页,那么需要立即过渡到真实的第一页（即下标为1）
                    //过渡时间基本可以忽略，过渡完成，马上进入else，播放下一页
                    //禁用翻页动画,过渡不易察觉
                    mViewPager.setCurrentItem(1, false);
                    mWeakHandler.post(mBannerPlayRunnable);
                } else {
                    mViewPager.setCurrentItem(mCurrentIndex);
                    mWeakHandler.postDelayed(mBannerPlayRunnable, mIntervalTime);
                }
            }
            Log.d(TAG, "run: 下标： " + mCurrentIndex);
        }
    };

    /**
     * 处理Banner点击事件
     */
    public interface OnBannerClickListener {
        void onBannerClick(int position);
    }

    private OnBannerClickListener mOnBannerClickListener;

    public void setOnBannerClickListener(OnBannerClickListener mOnBannerClickListener) {
        this.mOnBannerClickListener = mOnBannerClickListener;
    }


    /**
     * banner翻页
     */
    public interface OnBannerPagerChangedListener {
        void onPageScrollStateChanged(int state);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);
    }

    public static class OnBannerSimplePagerListener implements OnBannerPagerChangedListener {

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }
    }

    private OnBannerPagerChangedListener mBannerPagerChangedListener;

    public void setBannerPagerChangedListener(OnBannerPagerChangedListener bannerPagerChangedListener) {
        this.mBannerPagerChangedListener = bannerPagerChangedListener;
    }

    /**
     * 设置一屏一页 的页面切换动画
     *
     * @param singleTransformer
     */
    private void setSingleTransformer(int singleTransformer) {
        switch (singleTransformer) {
            case 0:
                setBannerTransformer(new DefaultTransformer());
                break;
            case 1:
                setBannerTransformer(new AccordionTransformer());
                break;
            default:
                break;
        }
    }

    private void setMultiTransformer(int multiTransformer) {
        switch (multiTransformer) {
            case 0:
                setBannerTransformer(new AlphaPageTransformer());
                break;
            case 1:
                setBannerTransformer(new NonPageTransformer());
                break;
            case 2:
                setBannerTransformer(new RotateDownPageTransformer());
                break;
            case 3:
                setBannerTransformer(new RotateUpPageTransformer());
                break;
            case 4:
                setBannerTransformer(new RotateYTransformer());
            default:
                setBannerTransformer(new ScaleInTransformer());
                break;
        }
    }

    private void setMzTransformer(int mzTransformer) {
        switch (mzTransformer) {
            case 0:
                setBannerTransformer(new RotateYTransformer());
                break;
            case 1:
                setBannerTransformer(new ScaleInTransformer());
                break;
            default:
                setBannerTransformer(new ScaleYTransformer());
        }
    }
}
