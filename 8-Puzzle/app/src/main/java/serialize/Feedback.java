package serialize;

import java.io.Serializable;

public class Feedback implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String email,feedback,ipaddress,country,device_id;
	
	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Feedback(String email, String feedback,String country)
	{
		this.email=email;
		this.feedback=feedback;
		
		this.country=country;
	}

}
