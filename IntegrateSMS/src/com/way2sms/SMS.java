package com.way2sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.prefs.Preferences;


public class SMS {
	
	
	private static final String ERROR_IMPORTING_CONTACTS = "Failed to get online contacts...";

	private static final String LOGIN_SUCCESSFUL = "Login Successful...";

	private static final String LOGIN_FAILED = "Login Failed...";

	private static final String INPUT_VALIDATION_FAILED = "Failure: Input Validation Failed.";

	private static final String FAILURE_SMS_SENT = "Failure: SMS Sent Failed.";

	private static final String CONNECTION_FAILURE = "Failure: Failed to connect to way2sms.com, Check your internet connection/proxy.";

	private static final String SUCCESS = "Message has been submitted successfully";

	private static String Token;

	private URL url = null;

	private HttpURLConnection connection = null;

	private static String CUST_FROM = null;

	private Preferences prefs = null;
	
	private  String loginString;

	
	public void send(String uid, String pwd, String phone, String msg,
			String proxy) {
		try {
			prefs = Preferences.userNodeForPackage(SMS.class);
			CUST_FROM = prefs.get("custFrom", null);

			uid = validateUserId(uid);
			pwd = validatePassword(pwd);
			msg = validatePhoneNumberAndMessage(phone, msg);
			setupProxy(proxy);

			// Login
			String cookie = login(uid, pwd);
			if (cookie != null && cookie.trim().length() > 0) {
				// Send SMS
				sendMessages(msg, phone, cookie);
				// Logout
				logout(cookie);
			}
		} catch (UnknownHostException e1) {
			log(CONNECTION_FAILURE);
			e1.printStackTrace();
		} catch (IOException e1) {
			log(FAILURE_SMS_SENT);
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			log(INPUT_VALIDATION_FAILED);
			e1.printStackTrace();
		}
	}

	
	private String login(String uid, String pwd) {
		log("Logging in ...");
		String cookie = "";
		try {
			loginString = "username=" + uid + "&password=" + pwd;
			PrintWriter pw1 = null;
			(connection = (HttpURLConnection) (url = new URL("http://site21.way2sms.com/Login1.action")).openConnection()).setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Host", "www.way2sms.com");
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729) FBSMTWB");
			connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language","en-US,ja-JP;q=0.5");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
			connection.setRequestProperty("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			connection.setRequestProperty("keep-alive", "300 ");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Referer", "http://wwwd.way2sms.com//entry.jsp");
			connection.setRequestProperty("Cookie", cookie);
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",String.valueOf(loginString.length()));
			connection.setRequestMethod("POST");
			connection.setInstanceFollowRedirects(false);
			
			(pw1 = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()), true)).print(loginString);
			pw1.flush();
			pw1.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			int httpResponseCode = connection.getResponseCode();

			StringBuffer msgResponse = new StringBuffer(10000);
			if (httpResponseCode == 200 || httpResponseCode == 302) {
				String s2 = br.readLine();

				while (s2 != null) {
					msgResponse.append(s2);
					s2 = br.readLine();
				}
			}

			cookie = connection.getHeaderField("Set-Cookie");
			//cookie = connection.getHeaderField("Set-Cookie");
			Token = cookie.substring(cookie.indexOf("~") + 1);
			Token = Token.substring(0, Token.indexOf(";"));
			System.out.println(cookie);
			System.out.println(Token);
			if (cookie != null && cookie.length() > 0) {
				log(LOGIN_SUCCESSFUL);
			} else {
				log(LOGIN_FAILED);
			}

		} catch (UnknownHostException e1) {
			log(LOGIN_SUCCESSFUL);
			log(CONNECTION_FAILURE);
			e1.printStackTrace();
		} catch (IOException e1) {
			log(LOGIN_FAILED);
			e1.printStackTrace();
		}
		return cookie;
	}

	
	private void sendMessages(String msg, String numbers, String cookie)throws MalformedURLException, IOException, ProtocolException {
		
		String content = "";
		PrintWriter pw = null;
		BufferedReader br = null;
		url = null;
		connection = null;
		PrintWriter pw2 = null;
		PrintWriter pw3 = null;
		
		url = new URL("http://site21.way2sms.com/main.action??section=s&Token="+ Token + "&vfType=register_verify");

		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("Referer","http://site21.way2sms.com/ebrdg.action?id"+Token);
		connection.setRequestProperty("Cookie", cookie);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("GET");
		connection.setInstanceFollowRedirects(false);
		
		(pw2 = new PrintWriter(new OutputStreamWriter(
				connection.getOutputStream()), true)).print(loginString);
		pw2.flush();
		pw2.close();

		if (connection.getResponseCode() == 200 || connection.getResponseCode() == 302) {

			url = new URL("http://site21.way2sms.com/smstoss.action");
			Integer msgLength = 140 - msg.length();
			content = "ssaction=ss&Token="+Token+"&mobile="+numbers+"&message="+msg+"&Send=Send Sms&msgLen="+msgLength.toString();
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5");
			connection.setRequestProperty("Content-Length",String.valueOf(content.length()));
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Referer","http://site21.way2sms.com/sendSMS?Token="+Token);
			connection.setRequestProperty("Cookie", cookie);
			connection.setInstanceFollowRedirects(true);
			
			(pw = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream()), true)).print(content);
		 
			//for(int i=0;i<60000000;i++)
			
			pw.flush();
			pw.close();

			//br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			//String httpResponse = br.readLine();
			int httpResponseCode = connection.getResponseCode();
			
			if (httpResponseCode == 302 || httpResponseCode == 200) {
				
				url = new URL("http://site21.way2sms.com/smscofirm.action?SentMessage="+msg+"&Token="+Token+"&status=0");
				
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5");
				connection.setRequestProperty("Content-Length",String.valueOf(content.length()));
				connection.setRequestProperty("Accept", "*/*");
				connection.setRequestProperty("Referer","http://site21.way2sms.com/sendSMS?Token="+Token);
				connection.setRequestProperty("Cookie", cookie);
				connection.setInstanceFollowRedirects(false);
				
				
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String httpResponse = br.readLine();
				
				StringBuffer msgResponse = new StringBuffer(10000);
				while (httpResponse != null) {
					httpResponse = br.readLine();
					msgResponse.append(httpResponse);
				}
				
				if (msgResponse.indexOf(SUCCESS) >= 0)
					log(SUCCESS);
				else
					log(FAILURE_SMS_SENT);
				
				System.out.println(msgResponse.toString());

				
				br.close();
				url = null;
				connection = null;
			}
		}

	}

	
	private void logout(String cookie) throws MalformedURLException,
			IOException, ProtocolException {
		url = new URL("http://wwwd.way2sms.com/jsp/logout.jsp");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Cookie", cookie);
		connection.setRequestMethod("GET");
		connection.setInstanceFollowRedirects(false);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while (br.readLine() != null) {
		}
		br.close();
		int httpResponseCode = connection.getResponseCode();

		if (httpResponseCode == 200 || httpResponseCode == 302) {
			log("Logout successfull ...");
		} else {
			log("Logout Failed ...");
		}

		url = null;
		connection = null;
	}

	
	public ArrayList<Contact> importOnlineContacts(String uid, String pwd,
			String proxy) {
		ArrayList<Contact> contacts = null;
		try {
			uid = validateUserId(uid);
			pwd = validatePassword(pwd);
			setupProxy(proxy);
			String cookie = login(uid, pwd);
			String quickContactsResponse = getContacts(cookie);
			contacts = parseOnlineContacts(quickContactsResponse);
		} catch (Exception e1) {
			log("Failed to connect to way2sms.com");
			e1.printStackTrace();
		}
		return contacts;
	}

	private String getContacts(String cookie) {
		PrintWriter pw1 = null;
		String quickContactsResponse = "";
		try {
			if (cookie != null) {
				url = new URL("http://wwwd.way2sms.com//QuickContacts");
				String loginString = "folder=DashBoard";
				(connection = (HttpURLConnection) url.openConnection())
						.setDoOutput(true);
				connection.setRequestProperty("User-Agent", "Mozilla/5.0");
				connection.setRequestProperty("Content-Length",
						String.valueOf(loginString.length()));
				connection.setRequestProperty("Cookie", cookie);
				connection.setRequestMethod("POST");
				connection.setInstanceFollowRedirects(false);
				(pw1 = new PrintWriter(new OutputStreamWriter(
						connection.getOutputStream()), true))
						.print(loginString);
				pw1.flush();
				pw1.close();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				int httpResponseCode = connection.getResponseCode();

				if (httpResponseCode == 200 || httpResponseCode == 302) {
					String s2 = br.readLine();
					StringBuffer msgResponse = new StringBuffer(10000);
					while (s2 != null) {
						msgResponse.append(s2);
						s2 = br.readLine();
					}
					quickContactsResponse = msgResponse.toString();

					if (msgResponse.length() <= 1) {
						log(ERROR_IMPORTING_CONTACTS);
					} else
						log("Import Sucessfull...");
				} else {
					log(ERROR_IMPORTING_CONTACTS);
				}
			} else {
				log(ERROR_IMPORTING_CONTACTS);
			}

		} catch (UnknownHostException e1) {
			log(ERROR_IMPORTING_CONTACTS);
			log("Failed to connect to way2sms.com");
			e1.printStackTrace();
		} catch (IOException e1) {
			log(ERROR_IMPORTING_CONTACTS);
			e1.printStackTrace();
		}
		return quickContactsResponse;
	}

	
	private ArrayList<Contact> parseOnlineContacts(String quickContactsResponse) {
		String prefix = "seleContacts1(";
		String csv = quickContactsResponse.substring(
				quickContactsResponse.indexOf(prefix) + prefix.length(),
				quickContactsResponse.indexOf(")\">"));
		csv = csv.replaceAll("['\\ ]", "");
		csv = csv.replaceAll(",,", ",");
		String[] csvValues = csv.split(",");
		ArrayList<Contact> contactList = new ArrayList<Contact>();

		if (csvValues.length > 0) {
			int noOfContacts = Integer.parseInt(csvValues[0]);
			Contact contact = null;
			for (int i = 0; i < noOfContacts; i++) {
				contact = new Contact(false, csvValues[i + 1], csvValues[i + 1
						+ noOfContacts], "");
				contactList.add(contact);
			}
		}
		return contactList;
	}

	
	private static void setupProxy(String proxy) {
		// Proxy
		if (proxy.length() > 0) {
			String host = "";
			String port = "";
			int colonIndex = proxy.indexOf(':');

			if (colonIndex > 0) {
				host = proxy.substring(0, colonIndex);
				port = proxy.substring(colonIndex + 1, proxy.length());
			} else {
				host = proxy;
				port = "80";
			}
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyHost", host);
			System.getProperties().put("proxyPort", port);
			log("Proxy Enabled : " + host + " : " + port);
		} else {
			System.getProperties().put("proxySet", "false");
			System.getProperties().remove("proxyHost");
			System.getProperties().remove("proxyPort");
		}
	}

	
	private static void parseNumbers(String phone, Vector<Long> numbers) {
		String pharr[];
		if (phone.indexOf(',') >= 0) {
			pharr = phone.split(",");
			for (String t : pharr) {
				try {
					numbers.add(Long.valueOf(t));
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException(
							"Give proper phone numbers.");
				}
			}
		} else {
			try {
				numbers.add(Long.valueOf(phone));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Give proper phone numbers.");
			}
		}

		if (0 == numbers.size())
			throw new IllegalArgumentException(
					"At least one proper phone number should be present to send SMS.");
	}

	
	private static String validatePhoneNumberAndMessage(String phone, String msg)
			throws UnsupportedEncodingException {
		if (phone == null || 0 == phone.length())
			throw new IllegalArgumentException(
					"At least one phone number should be present.");

		if (msg == null || 0 == msg.length())
			throw new IllegalArgumentException("SMS message should be present.");
		else
			msg = URLEncoder.encode(msg, "UTF-8");
		return msg;
	}

	private static String validatePassword(String pwd)
			throws UnsupportedEncodingException {
		if (pwd == null || 0 == pwd.length())
			throw new IllegalArgumentException("Password should be present.");
		else
			pwd = URLEncoder.encode(pwd, "UTF-8");
		return pwd;
	}

	
	private static String validateUserId(String uid)
			throws UnsupportedEncodingException {
		if (uid == null || 0 == uid.length())
			throw new IllegalArgumentException("User ID should be present.");
		else
			uid = URLEncoder.encode(uid, "UTF-8");
		return uid;
	}

	
	static void log(String logInfo) {
		System.out.println(logInfo);
	}
}
