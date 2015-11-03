package main.java;

import org.codehaus.jackson.annotate.JsonProperty;


public class LoginForm {
	
    @JsonProperty 
	private String userName;
    @JsonProperty  
	private String password;
    
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
