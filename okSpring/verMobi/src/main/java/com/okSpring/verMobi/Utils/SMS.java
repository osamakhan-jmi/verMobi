package com.okSpring.verMobi.Utils;


import java.util.logging.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;


public class SMS {

	public static Logger LOG = Logger.getLogger(SMS.class.getName());

    public static void sendSMS(String to, String message) {

    	String urlString = "https://api.msg91.com/api/sendhttp.php?mobiles="+to+"&authkey=291929Ah59RnxSw5d69633c&route=4&sender=TESTIN&message="+message+"&country=91";
    	
    	try {
    		HttpResponse<String> response = Unirest.get(urlString).asString();
    		LOG.info("OTP sent: " + urlString);
    		LOG.info("SMS party: " + response);    		
    	}catch(Exception e) {
    		LOG.info("Exception SMS-->sendSMS " + e.toString());
    	}
    }
}
