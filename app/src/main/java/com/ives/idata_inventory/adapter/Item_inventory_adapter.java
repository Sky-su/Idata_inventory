package com.ives.idata_inventory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ives.idata_inventory.R;
import com.ives.idata_inventory.entity.Stock;
import com.ives.idata_inventory.util.SoundPoolHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author rujmobil
 */
public class Item_inventory_adapter extends BaseAdapter {

    private List<Stock> mData;
    private Context context;
    private SoundPoolHelper soundPoolHelper = null;

    private Map<Integer,String> title;

    public Item_inventory_adapter(List<Stock> mData, Context context,Map<Integer,String> data) {
        this.mData = mData;
        this.context = context;
        title = data;
        if (soundPoolHelper == null){
            soundPoolHelper = new SoundPoolHelper(4, SoundPoolHelper.TYPE_MUSIC)
                    .setRingtoneType(SoundPoolHelper.RING_TYPE_MUSIC)
                    //加载默认音频，因为上面指定了，所以其默认是：RING_TYPE_MUSIC
                    .load(context, "read", R.raw.duka3);
        }

    }


    public  void newDatabase(List<Stock> data, Context context,boolean sound){
        if (mData.size()>0){
            mData.clear();
            mData = new ArrayList<Stock>(data);
            if (sound){
                soundPool();
            }
            notifyDataSetChanged();
        }
    }
    /**
     *返回适配器数据
     * @return
     */
    public List<Stock> getDataS() {
        if (mData !=null){
            return mData;
        }
        return null;
    }
    public void clear() {
        if (mData != null){
            mData.clear();
            notifyDataSetChanged();
        }

    }


    /**播放声音**/

    public void soundPool(){
        soundPoolHelper.play("read",false);
    }
    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list,null);
            holder = new ViewHolder();
            holder.stockNameHit = convertView.findViewById(R.id.stockNameHit);
            holder.stockIdHit = convertView.findViewById(R.id.stockIdHit);
            holder.preserverHit = convertView.findViewById(R.id.preserverHit);
            holder.erpHit = convertView.findViewById(R.id.ERPHit);
            holder.stockNameHit.setText(String.format("%s%s",title.get(2),":"));
            holder.stockIdHit.setText(String.format("%s%s",title.get(0),":"));
            holder.erpHit.setText(String.format("%s%s",title.get(1),":"));
            holder.preserverHit.setText(String.format("%s%s",title.get(6),":"));
            holder.stockName = convertView.findViewById(R.id.stockName);
            holder.stockId = convertView.findViewById(R.id.stockId);
            holder.preserver = convertView.findViewById(R.id.preserver);
            holder.erp = convertView.findViewById(R.id.ERP);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        //赋值
        holder.stockName.setText(mData.get(position).getStockName());
        holder.stockId.setText(mData.get(position).getStockId());
        holder.erp.setText(mData.get(position).getErpId());
        holder.preserver.setText(mData.get(position).getPreserver());
        notifyDataSetChanged();
        return convertView;
    }



    /**
     * 清除一条数据
     */
    public void removeData(int i) {
        if (mData != null) {
            mData.remove(i);
            notifyDataSetChanged();
        }
    }

    /**
     * 清除所有数据
     */
    public void removeAllData() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }
    public class ViewHolder{
        //UI
        public TextView stockNameHit;
        public TextView erpHit;
        public TextView stockIdHit;
        public TextView preserverHit;
        //data
        public TextView stockName;
        public TextView erp;
        public TextView stockId;
        public TextView preserver;
    }
}
