package com.songm.dormrepair.adapter.orderinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.songm.dormrepair.R;
import com.songm.dormrepair.model.orderinfo.Eval;
import com.songm.dormrepair.utils.UrlUtils;
import com.wenhuaijun.easyimageloader.imageLoader.EasyImageLoader;

import java.util.List;

/**
 * Created by SongM on 2017/9/17.
 * 评价列适配器
 */

public class EvalAdapter extends BaseAdapter {

    private Context context;
    private List<Eval.EvalListBean> list;

    public EvalAdapter(Context context, List<Eval.EvalListBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView != null) {
            view = convertView;
        } else {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_order_info_eval, null);
        }

        ImageView item_order_info_eval_evalIcon = (ImageView) view.findViewById(R.id.item_order_info_eval_evalIcon);
        TextView item_order_info_eval_evalName = (TextView) view.findViewById(R.id.item_order_info_eval_evalName);
        TextView item_order_info_eval_evalContent = (TextView) view.findViewById(R.id.item_order_info_eval_evalContent);

        Eval.EvalListBean evalListBean = list.get(position);
        EasyImageLoader.getInstance(context).bindBitmap(UrlUtils.HOST + evalListBean.getEvalIconUrl(), item_order_info_eval_evalIcon);
        item_order_info_eval_evalName.setText(evalListBean.getEvalName());
        item_order_info_eval_evalContent.setText(evalListBean.getEvalContent());

        return view;
    }
}
