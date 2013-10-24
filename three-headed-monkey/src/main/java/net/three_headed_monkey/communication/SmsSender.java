package net.three_headed_monkey.communication;

import android.content.Context;
import android.telephony.SmsManager;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.PhoneNumberInfo;

@EBean
public class SmsSender {

    @App
    ThreeHeadedMonkeyApplication application;

    @RootContext
    Context context;

    public void sendSms(String text){
        for(PhoneNumberInfo phoneNumberInfo : application.phoneNumberSettings.getAll()){
            //@TODO check phoneNumberInfo for notification flag
            sendSms(text, phoneNumberInfo.phoneNumber);
        }
    }

    public void sendSms(String text, String number){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, text, null, null);
    }

    @AfterInject
    //Hack because android annotations does only set the application var if the context is an activity
    protected void setApplicationFromContext(){
        if(context instanceof ThreeHeadedMonkeyApplication)
            application = (ThreeHeadedMonkeyApplication)context;
    }

}
