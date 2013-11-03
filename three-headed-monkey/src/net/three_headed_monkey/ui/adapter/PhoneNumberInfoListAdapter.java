package net.three_headed_monkey.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.data.SimCardInfo;
import net.three_headed_monkey.ui.custom_views.SimCardInfoItemView;
import net.three_headed_monkey.ui.custom_views.SimCardInfoItemView_;

import java.util.List;

@EBean
public class PhoneNumberInfoListAdapter extends BaseAdapter {

    List<PhoneNumberInfo> phoneNumberInfos;

    @App
    ThreeHeadedMonkeyApplication application;

    @RootContext
    Context context;

    @AfterInject
    void initAdapter(){
        phoneNumberInfos = application.phoneNumberSettings.getAll();
    }

    @Override
    public int getCount() {
        return phoneNumberInfos.size();
    }

    @Override
    public PhoneNumberInfo getItem(int i) {
        return phoneNumberInfos.get(i);
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
