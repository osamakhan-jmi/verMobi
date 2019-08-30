package com.okSpring.verMobi.model;

import java.sql.Date;

import javax.persistence.*;


@Entity
@Table(name="userInfo")
@NamedQuery(name="UserInfo.findAll", query="SELECT s FROM UserInfo s")
public class UserInfo {

	@EmbeddedId
	private UserPhone userPhone;
	
	@Column(name="OTP")
	private String otp;
	
	@Column(name="IS_MSISDN_VERIFIED")
	private String isMsisdnVerfied;
	
	@Column(name="session_ID")
	private String sessionId;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIsMsisdnVerfied() {
		return isMsisdnVerfied;
	}

	public void setIsMsisdnVerfied(String isMsisdnVerfied) {
		this.isMsisdnVerfied = isMsisdnVerfied;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public UserPhone getUserPhone() {
		return this.userPhone;
	}
	public void setUserPhone(UserPhone userPhone) {
		this.userPhone = userPhone;
	}
}
