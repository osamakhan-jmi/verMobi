package com.okSpring.verMobi.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserPhone implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="msisdn")
	private String msisdn;

	@Column(name="imei")
	private String imei;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}




