package com.joelholder.twitter;

public class TwitterStreamBean {
	long id;
	String userName;
	String inReplyUserName;
	String content;
	String retweetUserName;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getInReplyUserName() {
		return inReplyUserName;
	}
	public void setInReplyUserName(String inReplyUserName) {
		this.inReplyUserName = inReplyUserName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRetweetUserName() {
		return retweetUserName;
	}
	public void setRetweetUserName(String retweetUserName) {
		this.retweetUserName = retweetUserName;
	}
}
