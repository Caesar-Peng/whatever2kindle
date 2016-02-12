package com.molecode.w2k.models;

/**
 * Created by YP on 2016-01-21.
 */
public class User {

	private Integer id;

	private String kindleEmail;

	private String w2kTag;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKindleEmail() {
		return kindleEmail;
	}

	public void setKindleEmail(String kindleEmail) {
		this.kindleEmail = kindleEmail;
	}

	public String getW2kTag() {
		return w2kTag;
	}

	public void setW2kTag(String w2kTag) {
		this.w2kTag = w2kTag;
	}
}
