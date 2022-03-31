package com.ives.idata_inventory.ui;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ives.idata_inventory.MainActivity;
import com.ives.idata_inventory.R;
import com.ives.idata_inventory.rf_mode.OnKeyListener;
import com.ives.idata_inventory.util.ButtonOnCilk;
import com.ives.idata_inventory.util.FileUtil;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;


public class PdfViewFragment extends Fragment implements OnKeyListener {


    public PdfViewFragment() {
        // Required empty public constructor
    }


    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    private PDFView pdfView;
    private String pdfName = "error";//文件名称   默认一个错误名称
    private boolean mLoadComplete = false;//加载完成
    private TextView mPageTv;//页数
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pdf_view, container, false);

        mPageTv = view.findViewById(R.id.textView);

        FileUtil.checkFile(getContext());
        SeePdf(new File(FileUtil.appFolderImportName,"help.pdf"));
        return view;
    }

    /**
     * 查看PDF
     */
    private void SeePdf(File dest) {
        try {
            PDFView webPdf = view.findViewById(R.id.pdfView);
            webPdf.setVisibility(View.VISIBLE);
            webPdf.fromFile(dest)
                    .defaultPage(1)  //设置默认显示第1页
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            if (mPageTv != null && page > 0 && pageCount >= page) {
                                mPageTv.setText(page + "/" + pageCount);
                            }
                        }
                    }) //设置翻页监听
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                            refreshPageView();
                        }
                    })
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            mLoadComplete = true;
                        }
                    })
                    .showMinimap(false) //pdf放大的时候，是否在屏幕的右上角生成小地图
                    .swipeVertical(true) //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                    .enableSwipe(true) //是否允许翻页，默认是允许翻页
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏页数
     */
    Runnable hidePage = new Runnable() {
        @Override
        public void run() {
            if (mPageTv != null) {
                mPageTv.setVisibility(View.GONE);
            }
        }
    };

    private Handler handler = null;
    private void refreshPageView() {
        if (mPageTv != null && !mPageTv.isShown()) {
            mPageTv.setVisibility(View.VISIBLE);
        }
        if (handler == null) {
            handler = new Handler();
        }
        handler.removeCallbacks(hidePage);
        handler.postDelayed(hidePage, 3000);
    }

    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {

    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK :
                if (ButtonOnCilk.isFastClick()){
                    mainActivity.setCurrentPage(3);
                }
                break;
            default:
                break;
        }
    }
}