<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>

<xsl:choose>
	<xsl:when test="//task/code/server='true'">
		<xsl:result-document href="serverConfig.properties">
			pwd="<xsl:value-of select="//task/code/keystorepassword"/>"
		</xsl:result-document>
	</xsl:when>
	<xsl:otherwise>
		<xsl:result-document href="clientConfig.properties">
			pwd="<xsl:value-of select="//task/code/keystorepassword"/>"
		</xsl:result-document>
	</xsl:otherwise>
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
	private static List&lt;TLSConnection&gt; sslConnections = null;
	private static Properties prop = new Properties();
	private static InputStream input = null;
	private static String pwd = null; 
			
	public TLSServer(int port) {
			System.setProperty("javax.net.ssl.keyStore","<xsl:value-of select="//task/code/keystore"/>");
			try {
				// If you move the generated code in another package (default of CogniCrypt is Crypto),
				// you need to change the parameter (replacing Crypto with the package name).
				input = Object.class.getClass().getResourceAsStream("/Crypto/serverConfig.properties");
				prop.load(input);
				pwd = prop.getProperty("pwd"); 
			} catch (IOException ex) {
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
        	System.setProperty("javax.net.ssl.keyStorePassword",pwd);
	        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try {
			sslServersocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(<xsl:choose><xsl:when test="//task/code/port"><xsl:value-of select="//task/code/port"/></xsl:when>
         <xsl:otherwise>port</xsl:otherwise>
		 </xsl:choose>
         );
         
			setCipherSuites();
			setProtocols();
			
			sslConnections = new ArrayList&lt;TLSConnection&gt;();
			startAcceptingConnections();
		} catch (IOException ex) {
			System.out.println("Connection to server could not be established. Please check whether the ip/hostname and port are correct");
			ex.printStackTrace();
		}
	}
	
	private static void startAcceptingConnections() throws IOException {
		while (true) {
			sslConnections.add(new TLSConnection((SSLSocket) sslServersocket.accept()));
		}
	}

	public List&lt;TLSConnection&gt; getCurrentConnections() {
		return sslConnections;
	}
        
    private void setCipherSuites() {
		if (sslServersocket != null) {
		sslServersocket.setEnabledCipherSuites(new String[]{
		"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256", "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
		"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 
		"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"
		<xsl:if test="//task/code/legacy='true']">
		"TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA ++ TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
		"TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_CBC_256_SHA", "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA" 
		"TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_256_GCM_SHA384", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA"
		"TLS_RSA_WITH_3DES_EDE_CBC_SHA"		
		</xsl:if>
		});
		}
	}

	private void setProtocols() {
		if (sslServersocket != null) {
			//Insert TLSxx here
			sslServersocket.setEnabledProtocols( new String[]{
			"TLSv1.1", "TLSv1.2" <!-- <xsl:for-each select="//task/element[@type='SecureCommunication']/TlsVersion">"<xsl:value-of select="."/>",</xsl:for-each>-->
			} );
		}
	}
}
</xsl:result-document>

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
		try {
			return bufR.readLine();
		} catch (IOException ex) {
			System.out.println("Receiving data failed.");
			ex.printStackTrace();
			return null;
		}
	}

}
</xsl:result-document>
	
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public void templateUsage(
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>,int port</xsl:otherwise></xsl:choose>) {
         //You need to set the right host (first parameter) and the port name (second parameter). If you wish to pass a IP address, please use overload with InetAdress as second parameter instead of string.
		 TLSServer tls = new TLSServer(<xsl:choose><xsl:when test="//task/code/port"><xsl:value-of select="//task/code/port"/></xsl:when>
         <xsl:otherwise>port</xsl:otherwise></xsl:choose>);
		 
		 tls.getCurrentConnections();
		
	}
}
</xsl:when><xsl:otherwise>
<xsl:result-document href="TLSClient.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class TLSClient {	
	private static SSLSocket sslsocket = null;
	private static BufferedWriter bufW = null;
	private static BufferedReader bufR = null;
	
		
	public TLSClient(<xsl:choose>
         <xsl:when test="//task/code/host"></xsl:when>
         <xsl:otherwise> String host</xsl:otherwise>
		 </xsl:choose>
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>,int port</xsl:otherwise>
		 </xsl:choose>
		 	) {
			Properties prop = new Properties();
			InputStream input = null;
			String pwd = null;
			System.setProperty("javax.net.ssl.trustStore","<xsl:value-of select="//task/code/keystore"/>");
			try {
				// If you move the generated code in another package (default of CogniCrypt is Crypto),
				// you need to change the parameter (replacing Crypto with the package name).
				input = Object.class.getClass().getResourceAsStream("/Crypto/clientConfig.properties");
				prop.load(input);
				pwd = prop.getProperty("pwd"); 
			} catch (IOException ex) {
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
          System.setProperty("javax.net.ssl.trustStorePassword",pwd);
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
		 </xsl:choose>
         );
         
			setCipherSuites();
			setProtocols();
			sslsocket.startHandshake();
	        bufW = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
	        bufR = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
		} catch (IOException ex) {
			System.out.println("Connection to server could not be established. Please check whether the ip/hostname and port are correct");
			ex.printStackTrace();
		}
	        
        }
        
        private void setCipherSuites() {
		if (sslsocket != null) {
			//Insert cipher suites here
			sslsocket.setEnabledCipherSuites(new String[]{
			"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256", "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
		"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 
		"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"
		<xsl:if test="//task/code/legacy='true']">
		"TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA ++ TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
		"TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_CBC_256_SHA", "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA" 
		"TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_256_GCM_SHA384", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA"
		"TLS_RSA_WITH_3DES_EDE_CBC_SHA"		
		</xsl:if>
			});
		}
	}

	private void setProtocols() {
		if (sslsocket != null) {
			//Insert TLSxx here
			sslsocket.setEnabledProtocols( new String[]{
			"TLSv1.2"
			<xsl:if test="//task/code/legacy='true']">
			"TLSv1.1", "TLSv1.0"
			</xsl:if>
			} );
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
	
}
</xsl:result-document>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public void templateUsage(<xsl:choose>
         <xsl:when test="//task/code/host"></xsl:when>
         <xsl:otherwise>String host</xsl:otherwise>
		 </xsl:choose>
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>,int port</xsl:otherwise></xsl:choose>) {
         //You need to set the right host (first parameter) and the port name (second parameter). If you wish to pass a IP address, please use overload with InetAdress as second parameter instead of string.
		 TLSClient tls = new TLSClient(<xsl:choose>
         <xsl:when test="//task/code/host"></xsl:when>
         <xsl:otherwise>host</xsl:otherwise>
		 </xsl:choose>
		 <xsl:choose>
         <xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>, port</xsl:otherwise>
		 </xsl:choose>);
		 
		 Boolean sendingSuccessful = tls.sendData("");
		 String data = tls.receiveData();
		
		tls.closeConnection();		
	}
}
</xsl:otherwise></xsl:choose>

</xsl:if>


</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>


</xsl:stylesheet>
