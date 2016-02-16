package com.way2sms;

/**
 * 
 *
 */
public class TestSMS {

	public static void main(String[] args) {
		if (args.length == 4) {
			SMS smsClient = new SMS();
			String uid = args[0];
			String pwd = args[1];
			String phone = args[2];
			String msg = args[3];
			String proxy = "";
			smsClient.send(uid, pwd, phone, msg, proxy);
		} else {
			System.out
					.println("USAGE: java -jar IntegrateSMS.jar <userId> <Password> <PhoneNos(csv)> <Message>");
		}

	}

}
