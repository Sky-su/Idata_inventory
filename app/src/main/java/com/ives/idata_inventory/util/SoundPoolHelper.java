package com.ives.idata_inventory.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;


import com.ives.idata_inventory.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/22 17:24
 *     desc  :  封装了SoundPool
 *     thanks To:   http://flycatdeng.iteye.com/blog/2120043
 *                  http://www.2cto.com/kf/201408/325318.html
 *                  https://developer.android.com/reference/android/media/SoundPool.html
 *     dependent on:
 *     update log:
 *              1.  2017年6月28日10:39:46
 *                  1）修复了当play指定的RingtoneName为空的时候，触发的一个bug
 *                  2）增加了一个默认的铃声，当找不到系统的默认铃声时候，会默认加载一个我们提供的一个默认铃声
 * </pre>
 */
public class SoundPoolHelper {

    /*常量*/
    public final static int TYPE_MUSIC = AudioManager.STREAM_MUSIC;
    public final static int TYPE_ALARM = AudioManager.STREAM_ALARM;
    public final static int TYPE_RING = AudioManager.STREAM_RING;
    private static final int MIN_CLICK_DELAY_TIME = 30;
    private static long lastClickTime;

    @IntDef({TYPE_MUSIC, TYPE_ALARM, TYPE_RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {}

    public final static int RING_TYPE_MUSIC = RingtoneManager.TYPE_ALARM;
    public final static int RING_TYPE_ALARM = RingtoneManager.TYPE_NOTIFICATION;
    public final static int RING_TYPE_RING = RingtoneManager.TYPE_RINGTONE;
    @IntDef({RING_TYPE_MUSIC, RING_TYPE_ALARM, RING_TYPE_RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RING_TYPE {}

    /*变量*/
    private SoundPool soundPool;
    private int NOW_RINGTONE_TYPE = RingtoneManager.TYPE_NOTIFICATION;
    private int maxStream;
    private Map<String, Integer> ringtoneIds;

    /*方法*/

    public SoundPoolHelper() {
        this(1,TYPE_MUSIC);
    }

    public SoundPoolHelper(int maxStream) {
        this(maxStream,TYPE_ALARM);
    }

    public SoundPoolHelper(int maxStream, @TYPE int streamType) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(maxStream);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(streamType);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
            this.maxStream = maxStream;
            ringtoneIds = new HashMap<>();
    }

    /**
     * 设置RingtoneType，这只是关系到加载哪一个默认音频
     *  需要在load之前调用
     * @param ringtoneType  ringtoneType
     * @return  this
     */
    public SoundPoolHelper setRingtoneType(@RING_TYPE int ringtoneType) {
        NOW_RINGTONE_TYPE = ringtoneType;
        return this;
    }

    /**
     * 加载音频资源
     * @param context   上下文
     * @param resId     资源ID
     * @return  this
     */
    public SoundPoolHelper load(Context context, @NonNull String ringtoneName, @RawRes int resId) {
        if (maxStream==0) {
            return this;
        }
        maxStream--;
        ringtoneIds.put(ringtoneName,soundPool.load(context,resId,1));
        return this;
    }

    /**
     * 加载默认的铃声
     * @param context 上下文
     * @return  this
     */
    public SoundPoolHelper loadDefault(Context context) {
        Uri uri = getSystemDefaultRingtoneUri(context);
        if (uri == null) {
            load(context,"default", R.raw.duka3);
        } else{
            load(context,"default",uri2Path(context,uri));
        }
        return this;
    }

    /**
     *      把 Uri 转变 为 真实的 String 路径
     * @param context 上下文
     * @param uri  URI
     * @return 转换结果
     */
    public static String uri2Path(Context context, Uri uri) {
        if ( null == uri ) return null;
        String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    /**
     * 加载铃声
     * @param context   上下文
     * @param ringtoneName 自定义铃声名称
     * @param ringtonePath 铃声路径
     * @return  this
     */
    public SoundPoolHelper load(Context context, @NonNull String ringtoneName, @NonNull String ringtonePath) {
        if (maxStream==0) {
            return this;
        }
        maxStream--;
        ringtoneIds.put(ringtoneName,soundPool.load(ringtonePath,1));
        return this;
    }

    /**
     *  int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) ：
     *  1)该方法的第一个参数指定播放哪个声音；
     *  2) leftVolume 、
     *  3) rightVolume 指定左、右的音量：
     *  4) priority 指定播放声音的优先级，数值越大，优先级越高；
     *  5) loop 指定是否循环， 0 为不循环， -1 为循环；
     *  6) rate 指定播放的比率，数值可从 0.5 到 2 ， 1 为正常比率。
     */
    public void play( String ringtoneName, boolean isLoop) {
        long curClickTime = System.currentTimeMillis();
        if (ringtoneIds.containsKey(ringtoneName) && (curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            soundPool.play(ringtoneIds.get(ringtoneName), 0.1f, 0.1f, 1, isLoop ? -1 : 0, 1);
        }
    }

    public void playLooking(@NonNull String ringtoneName, boolean isLoop, float nu) {
        long curClickTime = System.currentTimeMillis();
        if (ringtoneIds.containsKey(ringtoneName) && (curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            soundPool.play(ringtoneIds.get(ringtoneName), nu, nu, 1, isLoop ? -1 : 0, 1);
        }
    }

    public void playDefault() {
        play("default", false);
    }
    public void close(String ring) {
        soundPool.stop(ringtoneIds.get(ring));
    }

    /**
     * 释放资源
     */
    public void release() {
        if (soundPool != null) {
            soundPool.release();
        }
    }

    /**
     * 获取系统默认铃声的Uri
     * @param context  上下文
     * @return  uri
     */
    private Uri getSystemDefaultRingtoneUri(Context context) {
        try {
            return RingtoneManager.getActualDefaultRingtoneUri(context, NOW_RINGTONE_TYPE);
        } catch (Exception e) {
            return null;
        }
    }
}

