package com.hrm.baidusdk.customize.banner.loader;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public interface BannerEntry<T> {
    /**
     * 获取 Banner 数据源
     *
     * @return
     */
    T getBannerPath();

    /**
     * 获取指示器文本
     *
     * @return
     */
    String getIndicatorText();
}
