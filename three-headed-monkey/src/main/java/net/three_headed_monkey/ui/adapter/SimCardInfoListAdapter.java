package net.three_headed_monkey.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.googlecode.androidannotations.annotations.*;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.SimCardInfo;
import net.three_headed_monkey.ui.custom_views.SimCardInfoItemView;
import net.three_headed_monkey.ui.custom_views.SimCardInfoItemView_;

import java.util.List;

@EBean
public class SimCardInfoListAdapter extends BaseAdapter {

    List<SimCardInfo> simCardInfos;

    @App
    ThreeHeadedMonkeyApplication application;

    @RootContext
    Context context;

    @AfterInject
    void initAdapter(){
        simCardInfos = application.simCardSettings.getAll();
    }

    @Override
    public int getCount() {
        return simCardInfos.size();
    }

    @Override
    public SimCardInfo getItem(int i) {
        return simCardInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(this.getClass().toString(), "getView " + position);
        SimCardInfoItemView simCardInfoItemView;
        if(convertView == null){
            simCardInfoItemView = SimCardInfoItemView_.build(context);
        } else {
            simCardInfoItemView = (SimCardInfoItemView)convertView;
        }
        simCardInfoItemView.bind(getItem(position));
        return simCardInfoItemView;
    }
}
