package com.cmu.mobilepervasive.allgroup;


public class Message {
	private String fromName, message;
	private boolean isSelf;

    private int icon;

	public Message() {
	}

	public Message(String fromName, String message, int icon, boolean isSelf) {
		this.fromName = fromName;
		this.message = message;
		this.isSelf = isSelf;

        this.icon = icon;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

	public boolean isSelf() {
		return isSelf;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

}
