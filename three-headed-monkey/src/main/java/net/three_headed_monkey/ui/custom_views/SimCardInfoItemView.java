package net.three_headed_monkey.ui.custom_views;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.data.SimCardInfo;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.sim_card_info_item)
public class SimCardInfoItemView extends RelativeLayout {
    @ViewById
    protected TextView text_serial_number, text_country, text_operator;

    public SimCardInfoItemView(Context context) {
        super(context);
    }

    public void bind(SimCardInfo simCardInfo) {
        text_serial_number.setText(simCardInfo.serial_number);
        text_country.setText(simCardInfo.country_iso_code);
        text_operator.setText(simCardInfo.operator_name);
    }
}
