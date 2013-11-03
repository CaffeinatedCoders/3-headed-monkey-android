package net.three_headed_monkey.ui;

import android.app.Activity;
import com.googlecode.androidannotations.annotations.ViewById;

public class PhoneNumbersSettingsActivity extends Activity {

    @ViewById TextView text_, text_operator,text_country_code, text_currently_authorized;
    @ViewById Button button_authorize_card;

    @ViewById
    ListView authorized_simcards_list;

    @Bean
    PhoneNumberInfoListAdapter adapter;

    @App
    ThreeHeadedMonkeyApplication application;

    @Trace
    @AfterViews
    void bindAdapter(){
        authorized_simcards_list.setAdapter(adapter);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Click(R.id.button_authorize_card)
    public void toogleCurrentCardAuthorization(){
        if(application.simCardSettings.currentSimCardAuthorized())
            application.simCardSettings.removeCurrentSimCard();
        else
            application.simCardSettings.addCurrentSimCard();
        loadCurrentSimCardInfo();
        adapter.notifyDataSetChanged();
    }


    @AfterViews
    protected void loadCurrentSimCardInfo() {
        SimCardInfo current_simcard = SimCardInfo.createFromSimCard(this);
        if(current_simcard != null){
            text_serial_number.setText(current_simcard.serial_number);
            text_operator.setText(current_simcard.operator_name);
            text_country_code.setText(current_simcard.country_iso_code);

            if(application.simCardSettings.currentSimCardAuthorized()){
                text_currently_authorized.setText(getString(R.string.yes));
                text_currently_authorized.setTextColor(getResources().getColor(R.color.Positive));
                button_authorize_card.setText(getString(R.string.unauthorize_card));
            } else {
                text_currently_authorized.setText(getString(R.string.no));
                text_currently_authorized.setTextColor(getResources().getColor(R.color.Negative));
                button_authorize_card.setText(getString(R.string.authorize_card));
            }
        }
    }
}
