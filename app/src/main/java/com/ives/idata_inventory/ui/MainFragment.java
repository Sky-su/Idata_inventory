package com.ives.idata_inventory.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.ives.idata_inventory.MainActivity;
import com.ives.idata_inventory.R;
import com.ives.idata_inventory.dao.GlobalUtil;
import com.ives.idata_inventory.rf_mode.GetRFIDThread;
import com.ives.idata_inventory.util.ButtonOnCilk;


public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }



    private View mainView;

    private Button inventoryButton;
    private Button findButton;
    private Button settingButton;
    private Button exitButton;
    private ImageButton languageButton;

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
        mainView = inflater.inflate(R.layout.fragment_main, container, false);
        initialize(mainView);
        return mainView;
    }

    void initialize(View view){
        inventoryButton = (Button) view.findViewById(R.id.inventoryButton);
        findButton = (Button) view.findViewById(R.id.findButton);
        settingButton = (Button) view.findViewById(R.id.settingButton);
        exitButton = (Button) view.findViewById(R.id.exitButton);
        languageButton = view.findViewById(R.id.languageButton);
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ButtonOnCilk.isFastViewClick(inventoryButton)){
                    mainActivity.setCurrentPage(2);
                }
            }
        });
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ButtonOnCilk.isFastViewClick(findButton)){
                    GlobalUtil.workId= 2;
                    mainActivity.setCurrentPage(4);
                }
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ButtonOnCilk.isFastViewClick(settingButton)){
                    mainActivity.setCurrentPage(3);
                }
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ButtonOnCilk.isFastViewClick(exitButton)){
                    mainActivity.finish();
                }
            }
        });
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ButtonOnCilk.isFastViewClick(languageButton)){
                    mainActivity.setCurrentPage(5);
                }
            }
        });

    }


}