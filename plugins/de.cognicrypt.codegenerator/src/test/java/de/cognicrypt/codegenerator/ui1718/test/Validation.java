/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.ui1718.test;

import de.cognicrypt.codegenerator.Activator;

public class Validation {

	public int validationInteger(final String testInput) {

		//count=0 is the input accepting case
		//count=number of error

		int count = 0;
		final char[] chars = new char[testInput.length()];
		testInput.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < testInput.length(); i++) {
			if (!('0' <= chars[i] && chars[i] <= '9')) {
				count++;
			}
		}

		return count;
	}

	public int validationPortNumber(final String testInput) {

		//count=0 is the input accepting case
		//count=1 is the error case

		int count = 0;
		final String port = testInput;
		try {
			final int portNum = Integer.valueOf(port);
			if (portNum < 0 || portNum > 65535) {
				count++;
			}
		} catch (final NumberFormatException ex) {
			if (!port.equals("")) {
				count++;
			}
		}

		return count;
	}

	public int validationIpAddress(final String testInput) {

		//count=0 is the input accepting case
		//count=1 is the error case
		int count = 0;
		final String ip = testInput;
		int i = 0;

		try {
			if (!ip.isEmpty()) {
				final String[] ipAddress = ip.split("\\.");
				count = 0;
				if (ipAddress.length > 4) {
					count++;
				}
				for (i = 0; i <= ipAddress.length - 1; i++) {
					final int j = Integer.parseInt(ipAddress[i]);
					if (j < 0 || j > 255) {
						count++;
					}
				}
				if (ip.endsWith("..") || ip.startsWith(".")) {
					count++;
				}
				if (ip.endsWith(".") || ip.endsWith("[0-9]")) {
					count++;
				}
				if (i == 4 && ip.endsWith(".")) {
					count++;
				}

			}
		} catch (final NumberFormatException ex) {
			Activator.getDefault().logError(ex);
			if (!ip.equals("")) {
				count++;
			}

		}
		return count;
	}
}
