package net.three_headed_monkey.ui.custom_views;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import net.three_headed_monkey.R;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.data.SimCardInfo;

@EViewGroup(R.layout.sim_card_info_item)
public class PhoneNumberInfoItemView extends RelativeLayout{
    @ViewById
    protected TextView text_phonenumber;
    public PhoneNumberInfoItemView(Context context) {
        super(context);
    }

    public void bind(PhoneNumberInfo phoneNumberInfo){
        text_phonenumber.setText(phoneNumberInfo.phoneNumber);
    }
}
