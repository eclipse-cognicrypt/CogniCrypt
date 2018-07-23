/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import de.cognicrypt.codegenerator.Activator;

public class TLSConnection {

	public TLSConnection() {}

	/**
	 * The method testConnection check if it possible to establish a ssl connection to the host ip.
	 *
	 * @param host
	 *        ip
	 * @param port
	 * @return A string array with request answer and output text. array[0] contains true or false, array[1] contains the output text.
	 */
	public String[] testConnection(final String host, final int port) {
		boolean isConnected = false;
		final String[] returnArray = new String[2];
		returnArray[1] = "Error! Connection to host could not be established.";
		returnArray[0] = "false";
		try {
			final SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			final SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
			isConnected = sslsocket.isConnected();
			sslsocket.close();

			if (isConnected) {
				returnArray[1] = "Connection established successfully!";
				returnArray[0] = "true";
			}

		} catch (final IOException e) {
			Activator.getDefault().logError(e);
		}
		return returnArray;
	}
}
