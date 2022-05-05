<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>

<xsl:choose>
<xsl:when test="//task/code/server='true'">
<xsl:result-document href="serverConfig.properties"><xsl:if test="//task/code/ksgen='true'"># Please specify the keystore file
keystore=</xsl:if>
# Please specify the keystore's passphrase
pwd=
</xsl:result-document>
</xsl:when>
</xsl:choose>

<xsl:variable name="filename"><xsl:choose><xsl:when test="//task/code/server='true'">serverConfig.properties</xsl:when><xsl:otherwise>clientConfig.properties</xsl:otherwise></xsl:choose></xsl:variable>
<xsl:if test="//task[@description='SecureCommunication']">
<xsl:choose><xsl:when test="//task/code/server='true'">
<xsl:result-document href="TLSServer.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class TLSServer {	
	private static SSLServerSocket sslServersocket = null;

	public TLSServer(int port) {
		<xsl:if test="//task/code/ksgen='false'">System.setProperty("javax.net.ssl.keyStore", "<xsl:value-of select="//task/code/key"/>");</xsl:if>
		InputStream input = null;
		String pwd = null;
		<xsl:if test="//task/code/ksgen='true'">String keystore = null;</xsl:if>
		try {
			// If you move the generated code in another package (default of CogniCrypt is Crypto),
			// you need to change the parameter (replacing Crypto with the package name).
			input = Object.class.getClass().getResourceAsStream("/Crypto/serverConfig.properties");
			Properties prop = new Properties();
			prop.load(input);
			pwd = prop.getProperty("pwd");
			<xsl:if test="//task/code/ksgen='true'">keystore = prop.getProperty("keystore");</xsl:if>
		} catch (IOException ex) {
			System.err.println("Could not read keystore properties from config.");
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.setProperty("javax.net.ssl.keyStorePassword", pwd);
		<xsl:if test="//task/code/ksgen='true'">System.setProperty("javax.net.ssl.keyStore", keystore);</xsl:if>
		SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try {
			sslServersocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

			setCipherSuites();
			setProtocols();
		} catch (IOException ex) {
			System.out.println(
					"Connection to server could not be established. Please check whether the ip/hostname and port are correct");
			ex.printStackTrace();
		}
	}

	public void startAcceptingConnections() {
		System.out.println("Accepting connections now.");
		while (true) {
			try {
				Socket incoming = sslServersocket.accept();
				Thread newConnectionHandler = new Thread() {

					@Override
					public void run() {
						TLSConnection tlsConnection = new TLSConnection((SSLSocket) incoming);
						System.out.println(
								"Accepted connection from " + tlsConnection.getSource().getHostAddress() + ".");
						tlsConnection.receiveData();
					}
				};

				newConnectionHandler.start();
			} catch (IOException e) {
				System.err.println("Establishing connection with client failed.");
			}
		}
	}

	private void setCipherSuites() {
		if (sslServersocket != null) {
			// Insert cipher suites here
			sslServersocket.setEnabledCipherSuites(
					new String[] { "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
							"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
							"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
							"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"

					});
		}
	}

	private void setProtocols() {
		if (sslServersocket != null) {
			// Insert TLSxx here
			sslServersocket.setEnabledProtocols(new String[] { "TLSv1.2"<xsl:if test="//task/element/tlsProtocol='TLSv10'">
			,"TLSv1.1", "TLSv1"</xsl:if> });
		}
	}
}</xsl:result-document>

<xsl:result-document href="TLSConnection.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class TLSConnection {

	private SSLSocket sslSocket = null;
	private static BufferedWriter bufW = null;
	private static BufferedReader bufR = null;

	public TLSConnection(SSLSocket con) {
		sslSocket = con;
		try {
			bufW = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
			bufR = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
		} catch (IOException e) {
		}
	}

	public void closeConnection() {
		try {
			if (!sslSocket.isClosed()) {
				sslSocket.close();
			}
		} catch (IOException ex) {
			System.out.println("Could not close channel.");
			ex.printStackTrace();
		}
	}

	public boolean sendData(String content) {
		try {
			bufW.write(content + "\n");
			bufW.flush();
			return true;
		} catch (IOException ex) {
			System.out.println("Sending data failed.");
			ex.printStackTrace();
			return false;
		}
	}

	public String receiveData() {
		String readLine = null;
		try {
			readLine = bufR.readLine();
		} catch (IOException ex) {
			System.out.println("Receiving data failed.");
			ex.printStackTrace();
		}

		if (readLine == null) {
			this.closeConnection();
		}

		return readLine;
	}

	public InetAddress getSource() {
		return sslSocket.getInetAddress();
	}

	public boolean isClosed() {
		return sslSocket.isClosed();
	}
}</xsl:result-document>
	
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public static void templateUsage(
		<xsl:if test="//task/code/ksgen='true'">
			// You chose that you want to generate a new keystore. Please Follow Oracle's documenation on doing so:
			// https://docs.oracle.com/cd/E19509-01/820-3503/ggfen/
			// When you generated a new keystore, please make sure to add the path the keystore to the "serverConfig.properties" file
		</xsl:if>
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>,int port</xsl:otherwise></xsl:choose>) {
         //You need to set the right host (first parameter) and the port name (second parameter). If you wish to pass a IP address, please use overload with InetAdress as second parameter instead of string.
		 TLSServer tls = new TLSServer(<xsl:choose><xsl:when test="//task/code/port"><xsl:value-of select="//task/code/port"/></xsl:when>
         <xsl:otherwise>port</xsl:otherwise></xsl:choose>);
		 
		 tls.startAcceptingConnections();	
	}
}
</xsl:when>
<!-- Server code is finished. Remaining code is client: TLS Client or "simply" connect to an HTTPS connection -->
<!-- TLS client implementation -->
<xsl:when test="//task/code/HTTPS='false'">
<xsl:result-document href="TLSClient.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */

public class TLSClient {
	private static SSLSocket sslsocket = null;
	private static BufferedWriter bufW = null;
	private static BufferedReader bufR = null;

	public TLSClient(<xsl:choose><xsl:when test="//task/code/host"></xsl:when><xsl:otherwise> String host</xsl:otherwise>
		 </xsl:choose><xsl:choose><xsl:when test="//task/code/port"></xsl:when><xsl:otherwise>,int port</xsl:otherwise></xsl:choose>) {
		System.setProperty("javax.net.ssl.trustStore", "<xsl:value-of select="//task/code/key"/>");
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(<xsl:choose>
         <xsl:when test="//task/code/host">
         "<xsl:value-of select="//task/code/host"/>"</xsl:when>
          <xsl:otherwise> host</xsl:otherwise>
		 </xsl:choose>, 
        <xsl:choose>
         <xsl:when test="//task/code/port"><xsl:value-of select="//task/code/port"/></xsl:when>
         <xsl:otherwise>port</xsl:otherwise>
		</xsl:choose>);
			setCipherSuites();
			setProtocols();
			bufW = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
			bufR = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
		} catch (IOException ex) {
			System.out.println(
					"Connection to server could not be established. Please check whether the ip/hostname and port are correct");
			ex.printStackTrace();
		}

	}

	private void setCipherSuites() {
		if (sslsocket != null) {
			// Insert cipher suites here
			sslsocket.setEnabledCipherSuites(new String[] { "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
					"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
					"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
					"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
					"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"

			});
		}
	}

	private void setProtocols() {
		if (sslsocket != null) {
			// Insert TLSxx here
			sslsocket.setEnabledProtocols(new String[] { "TLSv1.2"
			<xsl:if test="//task/element/tlsProtocol='TLSv10'">
			,"TLSv1.1", "TLSv1"
			</xsl:if>
			});
		}
	}

	public void closeConnection() {
		try {
			if (!sslsocket.isClosed()) {
				sslsocket.close();
			}
		} catch (IOException ex) {
			System.out.println("Could not close channel.");
			ex.printStackTrace();
		}
	}

	public boolean sendData(String content) {
		try {
			System.out.println("Sending Message.");
			bufW.write(content + "\n");
			bufW.flush();
			return true;
		} catch (IOException ex) {
			System.out.println("Sending data failed.");
			ex.printStackTrace();
			return false;
		}
	}

	public String receiveData() {
		try {
			return bufR.readLine();
		} catch (IOException ex) {
			System.out.println("Receiving data failed.");
			ex.printStackTrace();
			return null;
		}
	}
	
}</xsl:result-document>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public static void templateUsage(<xsl:choose>
         <xsl:when test="//task/code/host"></xsl:when>
         <xsl:otherwise>String host</xsl:otherwise>
		 </xsl:choose>
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>,int port</xsl:otherwise></xsl:choose>) {
        
         <xsl:choose>
         <xsl:when test="//task/code/host">//You need to set the right host (first parameter) and the port name (second parameter). If you wish to pass a IP address, please use overload with InetAdress as second parameter instead of string.</xsl:when>
		 </xsl:choose> 
		 
		 TLSClient tls = new TLSClient(<xsl:choose>
         <xsl:when test="//task/code/host"></xsl:when>
         <xsl:otherwise>host</xsl:otherwise>
		 </xsl:choose>
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>, port</xsl:otherwise>
		 </xsl:choose>);
		 
		 boolean sendingSuccessful = tls.sendData(""); // This call sends the passed message over the connection.
		 String data = tls.receiveData(); //This call makes the socket listen for incoming messages.
		
		tls.closeConnection(); // This call properly closes the connection. Do not forget it.		
	}
}</xsl:when><xsl:otherwise>
<xsl:result-document href="HTTPSConnection.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class HTTPSConnection {	
  private static HttpsURLConnection con = null;
  private static BufferedReader reader = null;
  private static BufferedWriter writer = null;
  private static URL url = null;

  public HTTPSConnection(String host) throws IOException{
  url = new URL(host);
  con = (HttpsURLConnection) url.openConnection();
  con.setConnectTimeout(6000);
  }
  
  	private void closeConnection() {
		con.disconnect();
	}
	
	private void openConnection() throws IOException {
		con.connect();
	}
	
	public String get() throws IOException {
		con.setRequestMethod("GET");
		openConnection();
		reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		try {
			return reader.readLine();
		} catch (IOException ex) {
			System.out.println("Receiving data failed.");
			ex.printStackTrace();
			return null;
		}
		finally {
			reader.close();
			closeConnection();
		}
	}
	
		public boolean post(String content) throws IOException {
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		openConnection();
		
		try {
			writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			writer.write(content + "\n");
			writer.flush();
			return true;
		} catch (IOException ex) {
			System.out.println("Sending data failed.");
			ex.printStackTrace();
			return false;
		}
		finally {
			writer.close();
		}
	}
	
}</xsl:result-document>

<!-- Template Usage file for HTTPS Client connection -->
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public static void templateUsage(<xsl:choose><xsl:when test="//task/code/host"></xsl:when><xsl:otherwise>String host</xsl:otherwise></xsl:choose>) throws IOException{
		 
		 HTTPSConnection https = new HTTPSConnection(<xsl:choose>
		 											<xsl:when test="//task/code/host">"<xsl:value-of select="//task/code/host"/>"</xsl:when>
		 											<xsl:otherwise>host</xsl:otherwise>
		 											</xsl:choose>);
		 
		// HTTPS get example
		String data = https.get();
		System.out.println("received by get: " + data);	
		
		// HTTPS post example
		// Boolean suc = https.post("Test");
		// System.out.println("received by post maintest: " + suc);
	}	
}</xsl:otherwise>
</xsl:choose>
</xsl:if>
</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
</xsl:stylesheet>