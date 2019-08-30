package com.okSpring.verMobi.controller;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okSpring.verMobi.SocketHandler;
import com.okSpring.verMobi.service.QrService;


@RestController
public class QrController {
	
	public static Logger LOG = Logger.getLogger(QrController.class.getName()); 
	
	@Autowired
	private QrService qrService;
	
	@RequestMapping(value="/qrVerification", method=RequestMethod.POST)
	@ResponseBody
	public void qrVerification(@RequestBody String payload) {		
		LOG.info("Request QrController --> qrVerification" + payload);		
		ObjectMapper mapper = new ObjectMapper();
		try {			
			Map<String, String> map = mapper.readValue(payload, Map.class);	
			
			if(map.containsKey("qrValue")) {
				
				if(qrService.validateMobileAndImei(map)) {
					
					if(SocketHandler.findSession(map.get("qrValue"))){
						
						SocketHandler.sendMessageToClinet(map.get("qrValue"));
					}else {
						
						LOG.info("QrController --> qrVerification, No valid session found");
					}
				}else {
					
					LOG.info("QrController --> qrVerification, Invalid User found");
				}
			}else {
				
				LOG.info("QrController --> qrVerification, In valid request");
			}
			
		}catch(Exception e) {
			LOG.info("Exception QrController --> qrVerification " + e.toString());
		}
	}
	
	@RequestMapping (value = "/otpVerification/{otp}/sessionId/{sessionId}", method = RequestMethod.GET)
	public Object otpVerification(@PathVariable String otp, @PathVariable String sessionId) {		
		LOG.info("Request QrController --> otpVerification, OTP : " + otp + " Sessino ID : " + sessionId);
		if (qrService.verifyOtp(otp, sessionId)) {
			return "OTP verified";
		}else {
			return "Invalid OTP";
		}
	}

}
