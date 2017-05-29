/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Stefan Krüger, Karim Ali, André Sonntag
 *
 */
package crossing.e1.configurator.wizard;

import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import crossing.e1.configurator.Activator;

public class TLSConnection {

	public TLSConnection(){}

	/**
	 * The method testConnection check if it possible to establish a ssl connection to the host ip.
	 * @param host ip
	 * @param port
	 * @return A string array with request answer and output text. array[0] contains true or false, array[1] contains the output text.
	 */
	public String[] testConnection(String host, int port){
		boolean isConnected = false;
		String[] returnArray = new String[2];
		returnArray[1] = "Error! Connection to host could not be established.";
		returnArray[0] = "false";
		try {
			final SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			final SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);	
			isConnected = sslsocket.isConnected();
			sslsocket.close();
			
			if(isConnected){
				returnArray[1] = "Connection established successfully!";
				returnArray[0] = "true";
			}
			
		} catch (IOException e) {
			Activator.getDefault().logError(e);
		}
		return returnArray;
	}

}
