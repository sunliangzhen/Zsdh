package com.shuxiangbaima.task.view.a;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLayoutInfo;
import com.zhy.autolayout.utils.AutoLayoutHelper;


/**
 * @author Zero
 * @date 2016/4/27 17:41
 */
public class AutoTextInputLayout extends TextInputLayout {
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoTextInputLayout(Context context) {
        super(context);
    }

    public AutoTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public AutoTextInputLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AutoTextInputLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInEditMode()) {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static class LayoutParams extends TextInputLayout.LayoutParams implements AutoLayoutHelper.AutoLayoutParams {
        private AutoLayoutInfo mAutoLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mAutoLayoutInfo = AutoLayoutHelper.getAutoLayoutInfo(c, attrs);
        }

        public AutoLayoutInfo getAutoLayoutInfo() {
            return this.mAutoLayoutInfo;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }
}
