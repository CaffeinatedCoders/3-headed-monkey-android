package net.three_headed_monkey.data;

public class PhoneNumberInfo {
    public String phoneNumber;

    public PhoneNumberInfo(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!o.getClass().equals(PhoneNumberInfo.class))
            return false;
        PhoneNumberInfo phoneNumberInfo = (PhoneNumberInfo)o;
        return phoneNumberInfo.phoneNumber.equals(phoneNumber);
    }
}
