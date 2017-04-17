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
	String validFilePath = "src/test/resources/valid.xml";
	String xmlTestFilePath = "testXMLwriteInstance.xml";

	@Before
	public void setUp() throws Exception {
		String path = "src/main/resources/ClaferModel/CryptoTasks.js";
		path = "src/test/resources/hashing.js";
		this.claferModel = new ClaferModel(path);
		this.instGen = new InstanceGenerator(path, "PasswordBasedEncryption", "description");
		this.constraints = new HashMap<Question, Answer>();
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
		;

		final FileInputStream validFile = new FileInputStream(this.validFilePath);
		validFile.read(validBytes);
		validFile.close();

		final XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.inst, this.constraints);
		xmlparser.writeClaferInstanceToFile(this.xmlTestFilePath);

		final FileInputStream testFile = new FileInputStream(this.xmlTestFilePath);
		testFile.read(generatedBytes);
		testFile.close();

		assertArrayEquals(validBytes, generatedBytes);
	}

	@Test
	public void testXMLValidity() throws DocumentException, IOException {
		final String validXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<task description=\"Main\"><Package>Crypto</Package><Imports><Import>java.security.InvalidAlgorithmParameterException</Import><Import>java.security.InvalidKeyException</Import><Import>java.security.NoSuchAlgorithmException</Import><Import>java.security.NoSuchAlgorithmException</Import><Import>javax.crypto.SecretKey</Import><Import>javax.crypto.BadPaddingException</Import><Import>javax.crypto.Cipher</Import><Import>javax.crypto.IllegalBlockSizeException</Import><Import>javax.crypto.NoSuchPaddingException</Import><Import>java.security.SecureRandom</Import><Import>javax.crypto.spec.IvParameterSpec</Import><Import>javax.crypto.spec.SecretKeySpec</Import><Import>java.security.spec.InvalidKeySpecException</Import></Imports><algorithm type=\"Digest\"><outputSize>224</outputSize><name>SHA-224</name><performance>2</performance><status>secure</status></algorithm><code/></task>";
		final XMLParser xmlparser = new XMLParser();

		final String xml = xmlparser.displayInstanceValues(this.inst, this.constraints);
		assertEquals(validXML, xml);
	}
}
