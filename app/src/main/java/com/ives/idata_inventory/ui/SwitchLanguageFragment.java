package com.ives.idata_inventory.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ives.idata_inventory.MainActivity;
import com.ives.idata_inventory.R;
import com.ives.idata_inventory.adapter.LanguageAdapter;
import com.ives.idata_inventory.entity.LanguageBean;
import com.ives.idata_inventory.rf_mode.BackResult;
import com.ives.idata_inventory.rf_mode.OnKeyListener;
import com.ives.idata_inventory.util.ButtonOnCilk;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.List;


public class SwitchLanguageFragment extends Fragment implements OnKeyListener {



    public SwitchLanguageFragment() {
        // Required empty public constructor
    }

    private ListView mLanguageList;
    private LanguageAdapter adapter;
    private List<LanguageBean> languageList;
    private QMUITopBar toolbar;
    private View mlvView;
    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mlvView = inflater.inflate(R.layout.fragment_switch_language, container, false);;

        initView(mlvView);
        initData();
        setEvent();
        return mlvView;
    }
    private void initView(View view) {
        mLanguageList = (ListView) view.findViewById(R.id.language_list);

    }
    /**
     * 语言ListView
     */
    private void initData() {
        languageList = new ArrayList<>();
        languageList.add(new LanguageBean(0, getString(R.string.chinese), "zh", false));
        languageList.add(new LanguageBean(1, getString(R.string.english), "en", false));
        adapter = new LanguageAdapter(getContext(), languageList);
        mLanguageList.setAdapter(adapter);
    }

    private void setEvent() {
        mLanguageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                languageList.get(position).setSelected(true);
                adapter.notifyDataSetChanged();
                SharedPreferences preferences = getActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("language", languageList.get(position).getShortName());
                editor.commit();
                editor.apply();
                //setResult(RESULT_OK);
                final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                //杀掉以前进程
                android.os.Process.killProcess(android.os.Process.myPid());                // finish();
            }
        });
    }

    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK :
                if (ButtonOnCilk.isFastClick()){
                    mainActivity.setCurrentPage(1);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {

    }
}