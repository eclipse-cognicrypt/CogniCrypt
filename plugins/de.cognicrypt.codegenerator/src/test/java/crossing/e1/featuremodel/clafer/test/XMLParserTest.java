package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.clafer.instance.InstanceClafer;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.FileHelper;
import de.cognicrypt.codegenerator.utilities.XMLParser;

public class XMLParserTest {

	ClaferModel claferModel;
	InstanceGenerator instGen;
	HashMap<Question, Answer> constraints;
	InstanceClafer inst;
	String validFilePath = "src/test/resources/valid.xml";
	String xmlTestFilePath = "src/test/testXMLwriteInstance.xml";

	@Before
	public void setUp() throws Exception {
		String path = "src/main/resources/ClaferModel/CryptoTasks.js";
		path = "src/test/resources/hashing.js";
		this.claferModel = new ClaferModel(path);
		this.instGen = new InstanceGenerator(path, "c0_PasswordStoring", "description");
		this.constraints = new HashMap<>();
		this.inst = this.instGen.generateInstances(this.constraints).get(0);
	}

	@After
	public void tearDown() {
		FileHelper.deleteFile(this.xmlTestFilePath);
	}

	@Test
	public void testWriteToFile() throws IOException, DocumentException {
		final byte[] validBytes = new byte[2000];
		final byte[] generatedBytes = new byte[2000];

		final FileInputStream validFile = new FileInputStream(this.validFilePath);
		validFile.read(validBytes);
		validFile.close();

		final XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.inst, this.constraints);
		xmlparser.writeXMLToFile(this.xmlTestFilePath);

		final FileInputStream testFile = new FileInputStream(this.xmlTestFilePath);
		testFile.read(generatedBytes);
		testFile.close();

		assertArrayEquals(validBytes, generatedBytes);
	}

	@Test
	public void testXMLValidity() throws DocumentException, IOException {
		StringBuilder importBuilder = new StringBuilder();
		for (String importSt : Constants.xmlimportsarr) {
			importBuilder.append("<Import>");
			importBuilder.append(importSt);
			importBuilder.append("</Import>");
		}
		
		final String validXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<task description=\"PasswordStoring\"><Package>Crypto</Package><Imports><Import>java.security.InvalidAlgorithmParameterException</Import><Import>java.security.InvalidKeyException</Import><Import>java.security.NoSuchAlgorithmException</Import><Import>java.security.NoSuchAlgorithmException</Import><Import>javax.crypto.SecretKey</Import><Import>javax.crypto.BadPaddingException</Import><Import>javax.crypto.Cipher</Import><Import>javax.crypto.IllegalBlockSizeException</Import><Import>javax.crypto.NoSuchPaddingException</Import><Import>java.security.SecureRandom</Import><Import>javax.crypto.spec.IvParameterSpec</Import><Import>javax.crypto.spec.SecretKeySpec</Import><Import>java.security.spec.InvalidKeySpecException</Import><Import>java.util.List</Import><Import>java.util.Base64</Import><Import>java.io.InputStream</Import><Import>java.io.OutputStream</Import><Import>java.util.Properties</Import><Import>java.io.FileOutputStream</Import></Imports><Algorithm type=\"Digest\"><outputSize>384</outputSize><name>SHA-384</name><performance>3</performance><status>secure</status></Algorithm><Algorithm type=\"KeyDerivationAlgorithm\"><name>PBKDF</name><performance>2</performance><status>secure</status></Algorithm><name>Password Storing</name><element type=\"PasswordStoring\"><DigestToUse>Sha_384</DigestToUse><KdaToUse>Pbkdf</KdaToUse><Name>\"Password Storing\"</Name></element><code/></task>";
		final XMLParser xmlparser = new XMLParser();

		final String xml = xmlparser.displayInstanceValues(this.inst, this.constraints).asXML();
		assertEquals(validXML, xml);
	}
}
