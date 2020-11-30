package com.hrm.baidusdk.customize.banner.transformer.simple;

import android.view.View;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public class DefaultTransformer extends ABaseTransformer {
    @Override
    protected void onTransform(View page, float position) {

    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }
}
