package de.cognicrypt.codegenerator.ui1718.test;

public class Validation {

	public int validationInteger(String testInput) {
		 
		//count=0 is the input accepting case
		//count=number of error
		 
		int count = 0;
		char[] chars = new char[testInput.length()];
		testInput.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < testInput.length(); i++) {
			if (!('0' <= chars[i] && chars[i] <= '9')) {
				count++;
			}
		}

		return count;
	}

	public int validationPortNumber(String testInput) {

		//count=0 is the input accepting case 
		//count=1 is the error case
		
		int count = 0;
		String port = testInput;
		try {
			int portNum = Integer.valueOf(port);
			if (portNum < 0 || portNum > 65535) {
				count++;
			}
		} catch (NumberFormatException ex) {
			if (!port.equals("")){
				count++;
			}
		}
		
		return count;
	}
	

	public int validationIpAddress(String testInput) {

		//count=0 is the input accepting case 
		//count=1 is the error case
		int count = 0;
		String ip = testInput;

			try {
				if (ip != null && !ip.isEmpty()) {
					String[] ipAddress = ip.split("\\.");
					count = 0;
					if (ipAddress.length > 4 || ipAddress.length < 4) {
						count++;
					}
					for (int i = 0; i <= ipAddress.length - 1; i++) {
						int j = Integer.parseInt(ipAddress[i]);
						if (j < 0 || j > 255) {
							count++;
						}
					}
					if (ip.endsWith("..")) {
						count++;
					}
					if (ip.startsWith(".")) {
						count++;
					}
					
				}
			} catch (NumberFormatException ex) {
				if (!ip.equals("")){
					count++;
				}
	
		}
		return count;
	}
	}

