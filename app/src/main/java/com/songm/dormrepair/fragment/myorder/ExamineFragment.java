package com.songm.dormrepair.fragment.myorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songm.dormrepair.fragment.myorder.utils.ShowOrderListUtils;

/**
 * Created by neokree on 16/12/14.
 * 正在审核
 */
public class ExamineFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ShowOrderListUtils(inflater, 1).view();
    }

}
