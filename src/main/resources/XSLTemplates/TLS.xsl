<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">


<xsl:result-document href="TLSClient.java">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>

public class TLSClient {	
	private static SSLSocket sslsocket = null;
	private static BufferedWriter bufW = null;
	private static BufferedReader bufR = null;
	
		
	public TLSClient() throws IOException {
			System.setProperty("javax.net.ssl.<xsl:choose><xsl:when test="//task/code/server='true'">key</xsl:when><xsl:otherwise>trust</xsl:otherwise></xsl:choose>Store","path");
        System.setProperty("javax.net.ssl.<xsl:choose><xsl:when test="//task/code/server='true'">key</xsl:when><xsl:otherwise>trust</xsl:otherwise></xsl:choose>StorePassword","password");
	        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			sslsocket = (SSLSocket) sslsocketfactory.createSocket("<xsl:value-of select="//task/code/host"/>", <xsl:value-of select="//task/code/port"/>);
			setCipherSuites();
			setProtocols();
			sslsocket.startHandshake();
	        bufW = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
	        bufR = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
		} catch (IOException e) {
			throw new IOException("Connection to server could not be established. Please check whether the ip/hostname and port are correct");
		}
	        
        }
        
        private void setCipherSuites() {
		if (sslsocket != null) {
			//Insert cipher suites here
			sslsocket.setEnabledCipherSuites(new String[]{
			<xsl:for-each select="//task/ciphersuites/ciphersuite">"<xsl:value-of select="."/>",</xsl:for-each>
			});
		}
	}

	private void setProtocols() {
		if (sslsocket != null) {
			//Insert TLSxx here
			sslsocket.setEnabledProtocols( new String[]{
			<xsl:for-each select="//task/protocol/tlsversion">"<xsl:value-of select="."/>",</xsl:for-each>
			} );
		}
	}
	
	public void closeConnection() throws IOException {
		if (!sslsocket.isClosed()) {
			sslsocket.close();
		}
	}
	
	public boolean sendData(String content) {
		try {
			bufW.write(content + "\n");
			bufW.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public String receiveData() throws IOException {
		return bufR.readLine();
	}
	
}
</xsl:result-document>

<xsl:if test="//task[@description='Communicate securely']">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public void run(byte[] file<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">, String pwd</xsl:if>) {
		 TLSClient tls = null;
		try {
			//You need to set the right port (first parameter) and the host name (second parameter). If you wish to pass a IP address, please use overload with InetAdress as second parameter instead of string.
			tls = new TLSClient();
			tls.sendData("");
			String data = tls.receiveData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			try {
				if (tls != null) {
					tls.closeConnection();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
</xsl:if>

</xsl:template>
	
<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
<xsl:template match="StringArr">
<xsl:value-of select="."/>,
</xsl:template>

</xsl:stylesheet>
