package com.ives.idata_inventory;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.ives.idata_inventory.util.LanguageUtil;


/**
 * @author rujmobil
 * 2021-7-21
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("language", Context.MODE_PRIVATE);
        String selectedLanguage = preferences.getString("language", "");
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, selectedLanguage));
    }
}
