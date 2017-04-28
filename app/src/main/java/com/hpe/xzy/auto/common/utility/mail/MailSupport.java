package com.hpe.xzy.auto.common.utility.mail;

import android.content.Context;
import java.util.Properties;

/**
 * Created by xuzhenya on 11/3/2016.
 */

public class MailSupport {
    public static void sendMailAsync(String to, String body, String subject, Context context){
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_EMAIL, to);
//        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        intent.putExtra(Intent.EXTRA_TEXT, body);
//
//        context.startActivity(Intent.createChooser(intent, "Send mail..."));
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "16.208.49.245");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.port", "25");

    }


}
