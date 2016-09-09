package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.clafer.instance.InstanceClafer;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.beginer.question.Question;
import crossing.e1.configurator.utilities.FileHelper;
import crossing.e1.configurator.utilities.XMLParser;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;

public class XMLParserTest {
	ClaferModel claferModel;
	InstanceGenerator instGen;
	HashMap<Question, Answer> constraints;
	InstanceClafer inst;
	String validFile = "src/test/resources/valid.xml";
	String xmlTestFilePath = "testXMLwriteInstance.xml";

	@Before
	public void setUp() throws Exception {
		String path = "src/main/resources/ClaferModel/CryptoTasks.js";
		this.claferModel = new ClaferModel(path);
		this.instGen = new InstanceGenerator(path, "PasswordBasedEncryption", "description");
		this.constraints = new HashMap<Question, Answer>();
		this.inst = this.instGen.generateInstances(constraints).get(0);
	}

	@After
	public void tearDown() {
		FileHelper.deleteFile(xmlTestFilePath);
	}

	@Test
	public void testXMLValidity() throws DocumentException, IOException {
		String validXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<task description=\"PasswordBasedEncryption\"><Package>Crypto</Package><Imports><Import>java.security.InvalidAlgorithmParameterException</Import><Import>java.security.InvalidKeyException</Import><Import>java.security.NoSuchAlgorithmException</Import><Import>java.security.NoSuchAlgorithmException</Import><Import>javax.crypto.SecretKey</Import><Import>javax.crypto.BadPaddingException</Import><Import>javax.crypto.Cipher</Import><Import>javax.crypto.IllegalBlockSizeException</Import><Import>javax.crypto.NoSuchPaddingException</Import><Import>java.security.SecureRandom</Import><Import>javax.crypto.spec.IvParameterSpec</Import><Import>javax.crypto.spec.SecretKeySpec</Import><Import>java.security.spec.InvalidKeySpecException</Import></Imports><algorithm type=\"KeyDerivationAlgorithm\"><iterations>1000</iterations><outputSize>192</outputSize><name>bcrypt</name><description>Bcrypt password-based key derivation</description><security>Medium</security><performance>Slow</performance></algorithm><algorithm type=\"SymmetricBlockCipher\"><mode>OFB</mode><padding>PKCS5Padding</padding><keySize>192</keySize><name>AES</name><description>Advanced Encryption Standard (AES) cipher</description><security>Strong</security><performance>Fast</performance></algorithm><description>Encrypt data using a given password</description><code/></task>";
		XMLParser xmlparser = new XMLParser();
		
		String xml = xmlparser.displayInstanceValues(this.inst, this.constraints);
		assertEquals(validXML, xml);
	}

	@Test
	public void testWriteToFile() throws IOException, DocumentException {
		byte[] validBytes = new byte[2000];
		byte[] generatedBytes = new byte[2000];;
		
		FileInputStream validFile = new FileInputStream(this.validFile);
		validFile.read(validBytes);
		validFile.close();
		
		XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.inst, this.constraints);
		xmlparser.writeClaferInstanceToFile(this.xmlTestFilePath);
		
		FileInputStream testFile = new FileInputStream(this.xmlTestFilePath);
		testFile.read(generatedBytes);
		testFile.close();
		
		assertArrayEquals(validBytes, generatedBytes);
	}
}
