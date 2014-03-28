package net.three_headed_monkey.communication;

import android.content.Context;
import android.telephony.SmsManager;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
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
