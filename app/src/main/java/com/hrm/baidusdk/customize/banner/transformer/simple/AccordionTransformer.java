package com.hrm.baidusdk.customize.banner.transformer.simple;

import android.view.View;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public class AccordionTransformer extends ABaseTransformer {

    @Override
    protected void onTransform(View page, float position) {
        page.setPivotX(position < 0 ? 0 : page.getWidth());
        page.setScaleX(position < 0 ? 1f + position : 1f - position);
    }
}
