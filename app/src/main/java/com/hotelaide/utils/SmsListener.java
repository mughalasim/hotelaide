package com.hotelaide.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction()
                .equals("android.provider.Telephony.SMS_RECEIVED")) {

            //---get the SMS message passed in---

            Bundle bundle = intent.getExtras();
            SmsMessage[] messages;
            String message_from;

            //---retrieve the SMS message received---
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        message_from = messages[i].getOriginatingAddress();
                        String msgBody = messages[i].getMessageBody();
                        Helpers.LogThis("SMS", msgBody);
                        if (message_from.contains("Eatout")
                                || message_from.contains("+12243477026")
                                || message_from.contains("Verify")) {

                            if (msgBody.length() > 4) {
                                // [0]Welcome to EatOut.
                                // [1]Your one time passcode is 7330.
                                // [2]Please use this to login within 24 hours.
                                // [3]Call us on 0711 222 222 if you have any enquiries.
                                // [4]STOP20465

                                String newmsgbody[] = msgBody.split("\\.");
                                newmsgbody[1] = newmsgbody[1].substring(newmsgbody[1].length() - 4);

                                Intent k = new Intent("SMSReceived");
                                k.putExtra("SMSbody", newmsgbody[1]);
                                context.sendBroadcast(k);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("SMS ERROR", e.getMessage());
                }
            }
        }
    }
}