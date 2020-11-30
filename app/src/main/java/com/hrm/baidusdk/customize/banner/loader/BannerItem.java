package com.hrm.baidusdk.customize.banner.loader;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public class BannerItem implements BannerEntry {
    private Object path;
    private String indicatorText;

    public BannerItem() {
    }

    public BannerItem(Object path, String indicatorText) {
        this.path = path;
        this.indicatorText = indicatorText;
    }

    @Override
    public Object getBannerPath() {
        return path;
    }

    @Override
    public String getIndicatorText() {
        return indicatorText;
    }

    public Object getPath() {
        return path;
    }

    public void setPath(Object path) {
        this.path = path;
    }

    public void setIndicatorText(String indicatorText) {
        this.indicatorText = indicatorText;
    }
}
