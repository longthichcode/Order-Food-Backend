package com.project.DTO;

public class ErrDTO {
	private String message;
	private String path;
	public ErrDTO(String message, String path) {
		super();
		this.message = message;
		this.path = path;
	}
	public ErrDTO() {
		
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
