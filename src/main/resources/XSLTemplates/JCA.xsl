<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>

<xsl:if test="//task/algorithm[@type='SymmetricBlockCipher']">
<xsl:result-document href="Enc.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class Enc {	
	
	public byte[] encrypt(byte [] data, SecretKey key) throws GeneralSecurityException { 
		byte [] ivb = new byte [16];
	    SecureRandom.getInstanceStrong().nextBytes(ivb);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		
		Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/padding"/>");
		c.init(Cipher.ENCRYPT_MODE, key, iv);
		<xsl:choose>
		<xsl:when test="//task/code/textsize='false'">
		byte[] res = c.doFinal(data);
		</xsl:when>        
         <xsl:otherwise>
         int conv_len = 0;
         byte[] res = new byte[c.getOutputSize(data.length)];
         for (int i = 0; i + 1024 &lt;= data.length; i += 1024) {
			byte[] input = new byte[1024];
			System.arraycopy(data, i, input, 0, 1024);
			conv_len += c.update(input, 0, input.length, res, i);
		}
		conv_len += c.doFinal(data, conv_len, data.length-conv_len, res, conv_len);
        </xsl:otherwise>
		</xsl:choose>
		byte [] ret = new byte[res.length + ivb.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(res, 0, ret, ivb.length, res.length);
		return ret;
	}
	
	public byte[] decrypt(byte [] ciphertext, SecretKey key) throws GeneralSecurityException { 
		
		byte [] ivb = new byte [16];
		System.arraycopy(ciphertext, 0, ivb, 0, ivb.length);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		byte[] data = new byte[ciphertext.length - ivb.length];
		System.arraycopy(ciphertext, ivb.length, data, 0, data.length);
		
		Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/padding"/>");
		c.init(Cipher.DECRYPT_MODE, key, iv);
		<xsl:choose>
		<xsl:when test="//task/code/textsize='false'">
		byte[] res = c.doFinal(data);
		</xsl:when>        
         <xsl:otherwise>
         int conv_len = 0;
         byte[] res = new byte[c.getOutputSize(data.length)];
         for (int i = 0; i + 1024 &lt;= ciphertext.length; i += 1024) {
			byte[] input = new byte[1024];
			System.arraycopy(data, i, input, 0, 1024);
			conv_len += c.update(input, 0, input.length, res, i);
		}
		conv_len += c.doFinal(data, conv_len, data.length-conv_len, res, conv_len);
        </xsl:otherwise>
		</xsl:choose>
		
		return res;
	}
}
</xsl:result-document>
</xsl:if>

<xsl:if test="//task[@description='SymmetricEncryption']">

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="KeyDeriv.java">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>

public class KeyDeriv {
	
	public SecretKey getKey(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
		PBEKeySpec spec = new PBEKeySpec(pwd, salt, <xsl:choose>
         <xsl:when test="$Rounds > 1000"> <xsl:value-of select="$Rounds"/> </xsl:when>
         <xsl:otherwise> 1000 </xsl:otherwise>
		 </xsl:choose>, <xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>WithHmacSHA256");
		
		return new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>" );
	}
}
</xsl:result-document>
</xsl:if>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public byte[] templateUsage(byte[] data<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">, char[] pwd</xsl:if>) throws GeneralSecurityException  {
		<xsl:choose>
        <xsl:when test="//task/algorithm[@type='KeyDerivationAlgorithm']">KeyDeriv kd = new KeyDeriv();
		SecretKey key = kd.getKey(pwd); </xsl:when>
        <xsl:otherwise>SecretKeySpec key = getKey(); </xsl:otherwise>
		</xsl:choose>		
		
		Enc enc = new Enc();
		return enc.encrypt(data, key);
	}
}
</xsl:if>

<xsl:if test="//task[@description='SymmetricEncryption']">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public byte[] templateUsage(byte[] data<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">, char[] pwd</xsl:if>) throws GeneralSecurityException {
		<xsl:choose>
        <xsl:when test="//task/algorithm[@type='KeyDerivationAlgorithm']">KeyDeriv kd = new KeyDeriv();
		SecretKey key = kd.getKey(pwd); </xsl:when>
        <xsl:otherwise>KeyGenerator kg = KeyGenerator.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>");
		kg.init(<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SecretKey key = kg.generateKey(); </xsl:otherwise>
		</xsl:choose>	
		Enc enc = new Enc();
		byte[] ciphertext = enc.encrypt(data, key);
		enc.decrypt(ciphertext, key);
		return ciphertext;
	}
}
</xsl:if>
<xsl:if test="//task[@description='SecurePassword']">

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="PWHasher.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class PWHasher {	
	//adopted code from https://github.com/defuse/password-hashing
	
	public String createPWHash(char[] pwd) throws GeneralSecurityException { 
		byte[] salt = new byte[<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>/8];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>);
		SecretKeyFactory f = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>WithHmac<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/name"/><xsl:choose><xsl:when test="$outputSize > 200"> <xsl:value-of select="$outputSize"/> </xsl:when>
         <xsl:otherwise>1</xsl:otherwise></xsl:choose>");
		String pwdHash = toBase64(salt) + ":" + toBase64(f.generateSecret(spec).getEncoded());
		spec.clearPassword();
		return pwdHash;
	}
	
	public Boolean verifyPWHash(char[] pwd, String pwdhash) throws GeneralSecurityException {
		String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);

		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>);
		SecretKeyFactory f = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>WithHmac<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/name"/><xsl:choose><xsl:when test="$outputSize > 200"> <xsl:value-of select="$outputSize"/> </xsl:when>
         <xsl:otherwise>1</xsl:otherwise></xsl:choose>");
		Boolean areEqual = slowEquals(f.generateSecret(spec).getEncoded(), fromBase64(parts[1]));
		spec.clearPassword();
		return areEqual;
	}
	
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i &lt; a.length <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> i &lt; b.length; i++) {
			diff |= a[i] ^ b[i];
		}	
		return diff == 0;
	}

	private static String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	private static byte[] fromBase64(String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}
}
</xsl:result-document>
</xsl:if>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public void templateUsage(char[] pwd) throws GeneralSecurityException  {
		PWHasher pwHasher = new PWHasher();
		String pwdHash = pwHasher.createPWHash(pwd);
		Boolean t = pwHasher.verifyPWHash(pwd, pwdHash);
	}
}
</xsl:if>

<xsl:if test="//task[@description='LongTermArchiving']">
<xsl:result-document href="LongTermArchivingClient.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
import java.util.List;

public class LongTermArchivingClient {

	private ArchivingSystem archivingSystem;
	private ArchiveConfiguration archiveConfig;
	
	/**
	 * 
	 * Translates a `dataStructure` string to an ArchivingConfiguration.
	 * 
	 * Allowed dataStructures: 
	 * 	- Simple_List
	 *  - Skip_List
	 *  - Merkle_Tree_Sequence
	 *  - Notarial_Attestation_Wrapper
	 * 
	 * @param dataStructure DataStructe that fulfill the clafer constrains w.r.t. to the questionaire.
	 * @return null, if the given dataStructure is unknown else the correct ArchiveConfiguration that
	 * is required to create an Archive.
	 */
	private  static ArchiveConfiguration mapDatastructureToScheme(String dataStructure){
		Scheme archivingScheme = null;
		switch(dataStructure){
			case "Simple_List": 
				archivingScheme = Scheme.AdES;
				break;
			case "Skip_List": 
				archivingScheme = Scheme.CISS;
				break;
			case "Merkle_Tree_Sequence": 
				archivingScheme= Scheme.ERS;
				break;
			case "Notarial_Attestation_Wrapper": 
				archivingScheme= Scheme.AC;
				break;
		}
		
		ArchiveConfiguration archConfig = (archivingScheme == null)? null : 
				ArchiveConfiguration.createDefaultArchiveConfiguration(archivingScheme);
		
		return archConfig;
	}
	
	public LongTermArchivingClient() throws ServiceClientCreationException {
		if(archivingSystem != null){
			this.archivingSystem = (ArchivingSystem) ServiceClientCreator.createServiceClient(ServiceType.ARCHIVING_SYSTEM);
			this.archiveConfig = mapDatastructureToScheme("<xsl:value-of select="//task/algorithm[@type='List']/list"/>");
		}
	}
	
	public Archive createArchive(String archiveName) throws InternalServiceErrorException, IOException{
		return archivingSystem.createArchive(archiveName, archiveConfig);
	}
	
	public void renameArchive(long archiveId, String newName) throws EntityNotFoundException, InternalServiceErrorException{
		archivingSystem.renameArchive(archiveId, newName);
	}
	
	public List&lt;Archive&gt; getArchives() throws InternalServiceErrorException{
		return archivingSystem.getArchives();
	}
	
	public void deleteArchive(long archiveID) throws EntityNotFoundException, InternalServiceErrorException, IOException{
		archivingSystem.deleteArchive(archiveID);
	}
} 
</xsl:result-document>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public void templateUsage(String archiveName) throws ServiceClientCreationException, InternalServiceErrorException, IOException  {
		LongTermArchivingClient ltac = new LongTermArchivingClient();
		Archive a = ltac.createArchive(archiveName);
	}
}
</xsl:if>


<xsl:if test="//task[@description='SecureCommunication']">
<xsl:choose><xsl:when test="//task/code/server='true'">
<xsl:result-document href="TLSServer.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class TLSServer {	
	private static SSLServerSocket sslServersocket = null;
	private static List&lt;TLSConnection&gt; sslConnections = null;
			
	public TLSServer(int port) {
			System.setProperty("javax.net.ssl.keyStore","<xsl:value-of select="//task/code/keystore"/>");
        System.setProperty("javax.net.ssl.keyStorePassword","<xsl:value-of select="//task/code/keystorepassword"/>");
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
		//Insert cipher suites here
		sslServersocket.setEnabledCipherSuites(new String[]{
		<xsl:for-each select="//task/element[@type='SecureCommunication']/Ciphersuites">"<xsl:value-of select="."/>",</xsl:for-each>
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
			System.setProperty("javax.net.ssl.trustStore","<xsl:value-of select="//task/code/keystore"/>");
        System.setProperty("javax.net.ssl.trustStorePassword","<xsl:value-of select="//task/code/keystorepassword"/>");
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
			<xsl:for-each select="//task/element[@type='SecureCommunication']/Ciphersuites">"<xsl:value-of select="."/>",</xsl:for-each>
			});
		}
	}

	private void setProtocols() {
		if (sslsocket != null) {
			//Insert TLSxx here
			sslsocket.setEnabledProtocols( new String[]{
			"TLSv1.1", "TLSv1.2" <!-- <xsl:for-each select="//task/element[@type='SecureCommunication']/TlsVersion">"<xsl:value-of select="."/>",</xsl:for-each>-->
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

<xsl:if test="//task[@description='SECMUPACOMP']">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

<xsl:if test="//task[@description='SECMUPACOMP']//element[@type='SECMUPACOMP']//Aby='Euclid'">

	public double templateUsage(int pos_x, int pos_y <xsl:choose><xsl:when test="not(//task/code/host or //task/code/server='false')"></xsl:when>
         <xsl:otherwise>, String host</xsl:otherwise></xsl:choose> <xsl:choose><xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>, int port</xsl:otherwise></xsl:choose>, int bitlength ) {
        
        return euc_dist.run(<xsl:choose><xsl:when test="//task/code/server='true'">0</xsl:when><xsl:otherwise>1</xsl:otherwise></xsl:choose>, pos_x, pos_y, <xsl:value-of select="//task/element[@type='SECMUPACOMP']/Security"/>, bitlength,
         <xsl:choose><xsl:when test="//task/code/host"><xsl:value-of select="//task/code/host"/></xsl:when><xsl:when test="//task/code/server='true'">"This will be ignored."</xsl:when><xsl:otherwise>host</xsl:otherwise></xsl:choose>,
		 <xsl:choose><xsl:when test="//task/code/port"><xsl:value-of select="//task/code/port"/></xsl:when><xsl:otherwise>port</xsl:otherwise></xsl:choose>);
	}
</xsl:if>
<xsl:if test="//task[@description='SECMUPACOMP']//element[@type='SECMUPACOMP']//Aby='Millionaire'">

	public int templateUsage(<xsl:choose><xsl:when test="not(//task/code/host or //task/code/server='false')"></xsl:when>
         <xsl:otherwise> String host, </xsl:otherwise></xsl:choose>int money) {
        
       return mill_jni.run(<xsl:choose><xsl:when test="//task/code/server='true'">0</xsl:when><xsl:otherwise>1</xsl:otherwise></xsl:choose>, money);
	}
</xsl:if>

}
</xsl:if>
</xsl:template>
	
<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
</xsl:stylesheet>
