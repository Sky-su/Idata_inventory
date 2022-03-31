package com.ives.idata_inventory.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.ives.idata_inventory.R;
import com.ives.idata_inventory.entity.Stock;

import java.util.Map;

public class CommonDialog extends Dialog {

    private Context mContext;
    private ClickCallBack mCallBack;
    public Stock stock;
    private Map<Integer,String> title;

    public CommonDialog(Context context, Stock data,Map<Integer,String> hit, ClickCallBack callBack) {
        super(context, R.style.DialogStyle);
        mContext = context;
        stock = data;
        title = hit;
        mCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private  View view;
    TextView erpHit;
    TextView erp;
    TextView trayIdHit;
    TextView trayId;
    TextView stockNameHit;
    TextView stockName;
    TextView mfHit;
    TextView mf;
    TextView preserverHit;
    TextView preserver;

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.diaog_remind, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        lp.width = (int) (dm.widthPixels * 0.9); // 把对话框宽度设置为屏幕宽度的0.8
        dialogWindow.setAttributes(lp);
         erpHit = findViewById(R.id.erpHit);
         erp = findViewById(R.id.erp);
         trayIdHit = findViewById(R.id.trayIdHit);
         trayId = findViewById(R.id.trayId);
         stockNameHit = findViewById(R.id.stockNameHit);
         stockName = findViewById(R.id.stockName);
        mfHit = findViewById(R.id.mFHit);
         mf = findViewById(R.id.mf);
        preserver = findViewById(R.id.preserver);
        preserverHit = findViewById(R.id.preserverHit);
        /**
         *
         */
        erpHit.setText(String.format("%s%s",title.get(1),":"));
        trayIdHit.setText(String.format("%s%s",title.get(0),":"));
        stockNameHit.setText(String.format("%s%s",title.get(2),":"));
        mfHit.setText(String.format("%s%s",title.get(5),":"));
        preserverHit.setText(String.format("%s%s",title.get(6),":"));

        //
        erp.setText(stock.getErpId());
        trayId.setText(stock.getStockId());
        stockName.setText(stock.getStockName());
        mf.setText(stock.getMF());
        preserver.setText(stock.getPreserver());


//        TextView text = (TextView) view.findViewById(R.id.text2);
//        text.setText(String.format("%s%s",title.get(1),": ")+stock.getErpId()+"\n"+
//                String.format("%s%s",title.get(0),": ")+stock.getStockId()+"\n"
//                +String.format("%s%s",title.get(2),": ")+stock.getStockName()+"\n"+
//                String.format("%s%s",title.get(5),": ")+stock.getMF());

        // 设置按钮监听
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // dismiss();
                mCallBack.onConfirm( );
            }
        });

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // dismiss();
                mCallBack.onCancel();
            }
        });

    }

    public void showDialog(Stock s){
        if (s.getStockId().equals(stock.getStockId())){
            dismiss();
        }else{
            stock = s;
            erp.setText(stock.getErpId());
            trayId.setText(stock.getStockId());
            stockName.setText(stock.getStockName());
            mf.setText(stock.getMF());
        }
       // isShowing();
    }
    // 回调接口，执行具体的处理逻辑
    public interface ClickCallBack {
        void onConfirm();
        void onCancel();
    }
    public class Text_view{
        TextView erpHit;
        TextView erp;
        TextView trayIdHit;
        TextView trayId;
        TextView stockNameHit;
        TextView stockName;
        TextView mfHit;
        TextView mf;

    }

}
