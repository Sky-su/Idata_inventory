package com.ives.idata_inventory.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.ives.idata_inventory.MainActivity;
import com.ives.idata_inventory.R;
import com.ives.idata_inventory.dao.GlobalUtil;
import com.ives.idata_inventory.rf_mode.OnKeyListener;
import com.ives.idata_inventory.util.ButtonOnCilk;
import com.ives.idata_inventory.util.CustomToast;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.regex.Pattern;


public class SettingFragment extends Fragment implements OnKeyListener {


    //Ui
    /**数据样式**/
    private QMUIGroupListView listView;
    /**设备编号**/
    private QMUICommonListItemView deviceId;
    /**服务器地址**/
    private QMUICommonListItemView networkAddress;
    /**系统版本**/
    private QMUICommonListItemView SystemInfo;
    //设置进入密码
    private QMUICommonListItemView pwd;

    /**语言设置**/
    private QMUICommonListItemView languageId;

    /**帮助**/
    private QMUICommonListItemView help;

    private QMUITopBar topbar;
    public SettingFragment() {
        // Required empty public constructor
    }

    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topbar = view.findViewById(R.id.settingTopBar);
        topbar.setTitle(getString(R.string.setting));
        topbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
        initializeView(view);
    }

    private void initializeView(View view) {
        listView = view.findViewById(R.id.SetListView);

        languageId = listView.createItemView(getString(R.string.language));
        if (QMUIDisplayHelper.getCurCountryLan(getContext()).contains("en")){
            languageId.setDetailText(getString(R.string.english));
        }else if (QMUIDisplayHelper.getCurCountryLan(getContext()).contains("zh")){
            languageId.setDetailText(getString(R.string.chinese));
        }
        help = listView.createItemView(getString(R.string.help));
        SystemInfo = listView.createItemView(getString(R.string.version));
        SystemInfo.setDetailText(QMUIPackageHelper.getAppVersion(getContext()));

        // url = listView.createItemView("网址");
        //url.setBackground(getResources().getDrawable(R.mipmap.logo));
        //url.setDetailText("www.aidcsys.com");
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ButtonOnCilk.isFastViewClick(v)){
                    if (v instanceof QMUICommonListItemView) {
                        CharSequence text = ((QMUICommonListItemView) v).getText();
                        //upDateText(v,text.toString().trim(),((QMUICommonListItemView) v).getDetailText().toString());
                        if (getString(R.string.language).equals(text.toString())){

                            mainActivity.setCurrentPage(5);
                            //startActivityForResult(new Intent(Setting.this, SwitchLanguageActivity.class), 2);
                        }
                        if (getString(R.string.help).equals(text.toString())){
                            mainActivity.setCurrentPage(6);

                        }
                    }
                }
            }
        };


        QMUIGroupListView.newSection(getContext())
                .addItemView(languageId,onClickListener)
                .addItemView(help,onClickListener)
                .addItemView(SystemInfo, null)
                .addTo(listView);
    }


    //实例化
//    private void initView(View view){
//        listView = view.findViewById(R.id.SetListView);
//        deviceId = listView.createItemView(getString(R.string.deviceid));
//        deviceId.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);
//        networkAddress = listView.createItemView(getString(R.string.deviceurl));
//        networkAddress.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);
//        SystemInfo = listView.createItemView(getString(R.string.version));
//        pwd = listView.createItemView(getString(R.string.pwd));
//        SystemInfo.setDetailText(QMUIPackageHelper.getAppVersion(getContext()));
//        SystemInfo.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ButtonOnCilk.isFastViewClick(v)){
//                    if (v instanceof QMUICommonListItemView) {
//                        CharSequence text = ((QMUICommonListItemView) v).getText();
//                        if (getString(R.string.language).equals(text.toString())){
//                            // startActivityForResult(new Intent(this, SwitchLanguageActivity.class), 2);
//                        }else{
//                            upDateText(v,text.toString().trim(),((QMUICommonListItemView) v).getDetailText().toString());
//                        }
//                    }
//                }else{
//                    CustomToast.showLoad(getContext(),getString(R.string.RepeatClick),1500);
//                }
//            }
//        };
//
//        QMUIGroupListView.newSection(getContext())
//                .addItemView(deviceId, onClickListener)
//                .addItemView(networkAddress, onClickListener)//ip地址
//                .addItemView(pwd,onClickListener)
//                .addItemView(SystemInfo, null)
//                .addTo(listView);
//    }


    @Override
    public void onStart() {
        super.onStart();
//        networkAddress.setDetailText(GlobalUtil.getURL(getContext()));
//        deviceId.setDetailText(GlobalUtil.getDevice(getContext()));
//        pwd.setDetailText(GlobalUtil.getPWD(getContext()));
    }

    //弹窗修改值
//    private void upDateText(final View v, final String title, String data){
//        final QMUIDialog.EditTextDialogBuilder edit = new QMUIDialog.EditTextDialogBuilder(getContext());
//        edit.setTitle(title)
//                .setDefaultText(data)
//                .addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        dialog.dismiss();
//                    }
//                })
//                .addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        CharSequence text = edit.getEditText().getText();
//                        if (!"".equals(text.toString())){
//                            if (title.equals(getString(R.string.deviceid))){
//                                if (text.toString().length() == 3 && isInteger(text.toString())){
//                                    GlobalUtil.SetDevice(getContext(), text.toString());
//                                    ((QMUICommonListItemView) v).setDetailText(text.toString());
//                                    dialog.dismiss();
//                                }else {
//                                    CustomToast.showLoad(getContext(),getString(R.string.deviceurlHit_two),3000);
//                                }
//                            }
//                            if (title.equals(getString(R.string.deviceurl))){
//                                GlobalUtil.SetURL(getContext(), text.toString());
//                                ((QMUICommonListItemView) v).setDetailText(text.toString());
//                                dialog.dismiss();
//                            }
//                            if (title.equals(getString(R.string.update_time))){
//                                GlobalUtil.setUpdateTime(getContext(), text.toString());
//                                ((QMUICommonListItemView) v).setDetailText(text.toString());
//                                dialog.dismiss();
//                            }
//                            if (title.equals(getString(R.string.pwd))){
//                                GlobalUtil.setUpdateTime(getContext(), text.toString());
//                                ((QMUICommonListItemView) v).setDetailText(text.toString());
//                                GlobalUtil.setPWD(getContext(), text.toString());
//                                dialog.dismiss();
//                            }
//                        }else{
//                            CustomToast.showLoad(getContext(),getString(R.string.settingInfoHit),2000);
//                        }
//                    }
//                }).show();
//    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {

    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK :
                if (ButtonOnCilk.isFastClick()){
//                    if (!TextUtils.isEmpty(networkAddress.getDetailText())&& !TextUtils.isEmpty(deviceId.getDetailText())){
//                        GlobalUtil.setPWD(getContext(),pwd.getDetailText().toString());
//                        GlobalUtil.SetDevice(getContext(),deviceId.getDetailText().toString());
//                        GlobalUtil.SetURL(getContext(),networkAddress.getDetailText().toString());
//                        mainActivity.setCurrentPage(0);
//                    }else{
//                        CustomToast.showLoad(getContext(),getString(R.string.settingErrorHit),3000);
//                    }
                    mainActivity.setCurrentPage(1);
                }
                break;
            default:
                break;
        }
    }
}