package com.okSpring.verMobi.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okSpring.verMobi.Utils.OTP;
import com.okSpring.verMobi.Utils.SMS;
import com.okSpring.verMobi.controller.QrController;
import com.okSpring.verMobi.model.UserInfo;
import com.okSpring.verMobi.model.UserPhone;
import com.okSpring.verMobi.repository.UserInfoRepository;

@Service
public class QrService {
	
	public static Logger LOG = Logger.getLogger(QrController.class.getName()); 
	
	@Autowired
	private UserInfoRepository userInfoRepository;
	
	public Boolean validateMobileAndImei(Map<String, String> map){
		
		String msisdn = map.get("msisdn");
		String imei = map.get("imei");
		String sessionId = map.get("qrValue");
		
		if (isExsistingUser(msisdn,imei)) {
			UserInfo userInfo = getExsistingUser(msisdn,imei);
			if (userInfo != null) {
				if (getIsMsisdnVerified(userInfo)) {
					LOG.info("MSISDN already verified");
					return false;
				}else {
					saveUserInfo(msisdn,imei,OTP.generatorOTP(6),"N",sessionId);
					return true;
				}
			}else {
				LOG.info("Invalid user");
				return false;
			}			
		}else {
			saveUserInfo(msisdn,imei,OTP.generatorOTP(6),"N",sessionId);
			return true;
		}
	}
	
	public Boolean verifyOtp(String otp, String sessionId) {
		
		List<UserInfo> userInfoList = userInfoRepository.findAll();
		for(int i=0;i<userInfoList.size();i++) {
			UserInfo userInfo = userInfoList.get(i);
			if (userInfo.getSessionId().equalsIgnoreCase(sessionId)) {
				if (userInfo.getOtp().equalsIgnoreCase(otp)) {
					saveUserInfo(userInfo.getUserPhone().getMsisdn(),userInfo.getUserPhone().getImei(),otp,"Y",sessionId);
					return true;
				}else {
					return false;
				}				
			}
		}
		return false;
	}
	
	private void saveUserInfo(String msisdn, String imei,String otp, String isMsisdnVerified,String sessionId) {
		UserPhone userPhone = new UserPhone();
		userPhone.setImei(imei);
		userPhone.setMsisdn(msisdn);
		
		UserInfo userInfo = new UserInfo();
		userInfo.setIsMsisdnVerfied(isMsisdnVerified);
		userInfo.setOtp(otp);
		userInfo.setUserPhone(userPhone);
		userInfo.setSessionId(sessionId);
		
		SMS.sendSMS(msisdn, otp);
		userInfoRepository.save(userInfo);
		
	}
	
	private Boolean isExsistingUser(String msisdn, String imei) {
		
		UserPhone userPhone = new UserPhone();
		userPhone.setImei(imei);
		userPhone.setMsisdn(msisdn);
		
		Optional<UserInfo> optionalUserInfo = userInfoRepository.findById(userPhone);
		if (optionalUserInfo.isPresent()) {
			return true;
		}else {
			return false;
		}
	}
	
	private UserInfo getExsistingUser(String msisdn, String imei) {
		
		UserPhone userPhone = new UserPhone();
		userPhone.setImei(imei);
		userPhone.setMsisdn(msisdn);
		
		Optional<UserInfo> optionalUserInfo = userInfoRepository.findById(userPhone);
		if (optionalUserInfo.isPresent()) {
			return optionalUserInfo.get();
		}else {
			return null;
		}
	}
	
	private Boolean getIsMsisdnVerified(UserInfo userinfo) {
		if (userinfo.getIsMsisdnVerfied().equalsIgnoreCase("Y")) {
			return true;
		}else {
			return false;
		}
	}

}
