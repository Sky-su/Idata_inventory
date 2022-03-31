package com.ives.idata_inventory.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

public class TextUtil {



    /**
     * 设置字体颜色大小
     * @param data
     * @param start
     * @param end
     * @param colorInt
     * @return
     */
    public static CharSequence showTextStyle(String data,int start,int end,int colorInt){
        SpannableStringBuilder showText  = new SpannableStringBuilder(data);
        //单独设置字体大小
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(35);
        // 相对于默认字体大小的倍数,这里是1.2倍
       //  RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan((float) 1.2);
        showText.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //单独设置字体颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorInt);
        showText.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return showText;
    }
}
