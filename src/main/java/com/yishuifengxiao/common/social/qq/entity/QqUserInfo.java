package com.yishuifengxiao.common.social.qq.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * QQ用户信息
 * 
 * @author yishui
 *
 */
public class QqUserInfo {
	@JsonProperty("ret")
	private Integer ret;
	
	@JsonProperty("msg")
	private String msg;
	
	@JsonProperty("is_lost")
	private Integer lost;
	
	@JsonProperty("nickname")
	private String nickname;
	
	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("province")
	private String province;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("year")
	private String year;
	
	@JsonProperty("constellation")
	private String constellation;
	
	@JsonProperty("figureurl")
	private String figureurl;
	
	@JsonProperty("figureurl_1")
	private String figureurl1;
	
	@JsonProperty("figureurl_2")
	private String figureurl2;
	
	@JsonProperty("figureurl_qq_1")
	private String figureurlQq1;
	
	@JsonProperty("figureurl_qq_2")
	private String figureurlQq2;
	
	@JsonProperty("figureurl_qq")
	private String figureurlQq;
	
	@JsonProperty("figureurl_type")
	private String figureurlType;
	
	@JsonProperty("is_yellow_vip")
	private String yellowVip;
	
	@JsonProperty("vip")
	private String vip;
	
	@JsonProperty("yellow_vip_level")
	private String yellowVipLevel;
	
	@JsonProperty("level")
	private String level;
	
	@JsonProperty("is_yellow_year_vip")
	private String yellowYearVip;
	
	@JsonProperty("openId")
	private String openId;

	public Integer getRet() {
		return ret;
	}

	public void setRet(Integer ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getLost() {
		return lost;
	}

	public void setLost(Integer lost) {
		this.lost = lost;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public String getFigureurl() {
		return figureurl;
	}

	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}

	public String getFigureurl1() {
		return figureurl1;
	}

	public void setFigureurl1(String figureurl1) {
		this.figureurl1 = figureurl1;
	}

	public String getFigureurl2() {
		return figureurl2;
	}

	public void setFigureurl2(String figureurl2) {
		this.figureurl2 = figureurl2;
	}

	public String getFigureurlQq1() {
		return figureurlQq1;
	}

	public void setFigureurlQq1(String figureurlQq1) {
		this.figureurlQq1 = figureurlQq1;
	}

	public String getFigureurlQq2() {
		return figureurlQq2;
	}

	public void setFigureurlQq2(String figureurlQq2) {
		this.figureurlQq2 = figureurlQq2;
	}

	public String getFigureurlQq() {
		return figureurlQq;
	}

	public void setFigureurlQq(String figureurlQq) {
		this.figureurlQq = figureurlQq;
	}

	public String getFigureurlType() {
		return figureurlType;
	}

	public void setFigureurlType(String figureurlType) {
		this.figureurlType = figureurlType;
	}

	public String getYellowVip() {
		return yellowVip;
	}

	public void setYellowVip(String yellowVip) {
		this.yellowVip = yellowVip;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getYellowVipLevel() {
		return yellowVipLevel;
	}

	public void setYellowVipLevel(String yellowVipLevel) {
		this.yellowVipLevel = yellowVipLevel;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getYellowYearVip() {
		return yellowYearVip;
	}

	public void setYellowYearVip(String yellowYearVip) {
		this.yellowYearVip = yellowYearVip;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}


}