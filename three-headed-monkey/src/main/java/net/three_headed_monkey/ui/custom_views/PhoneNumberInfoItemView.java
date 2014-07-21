package net.three_headed_monkey.ui.custom_views;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.data.PhoneNumberInfo;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.phone_number_info_item)
public class PhoneNumberInfoItemView extends RelativeLayout {
    @ViewById
    protected TextView text_name, text_phonenumber;

    public PhoneNumberInfoItemView(Context context) {
        super(context);
    }

    public void bind(PhoneNumberInfo phoneNumberInfo) {
        text_phonenumber.setText(phoneNumberInfo.phoneNumber);
        text_name.setText(phoneNumberInfo.name);
    }
}
