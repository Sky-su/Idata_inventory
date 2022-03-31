package com.ives.idata_inventory.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class DrawableVerticalButton extends androidx.appcompat.widget.AppCompatButton {

    public DrawableVerticalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas = getTopCanvas(canvas);
        super.onDraw(canvas);
    }

    private Canvas getTopCanvas(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables == null) {
            return canvas;
        }
        Drawable drawable = drawables[1];// 上面的drawable
        if(drawable == null){
            drawable = drawables[3];// 下面的drawable
        }

        float textSize = getPaint().getTextSize();
        int drawHeight = drawable.getIntrinsicHeight();
        int drawPadding = getCompoundDrawablePadding();
        float contentHeight = textSize + drawHeight + drawPadding;
        int topPadding = (int) (getHeight() - contentHeight);
        setPadding(0, topPadding , 0, 0);
        float dy = (contentHeight - getHeight())/2;
        canvas.translate(0, dy);
//        Log.i("DrawableTopButton", "setPadding(0,"+topPadding+",0,0");
//        Log.i("DrawableTopButton", "translate(0,"+dy+")");
        return canvas;
    }

}
