package crossing.e1.primitiveintegration.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.providerUtils.Helper;
import de.cognicrypt.codegenerator.utilities.Utils;

public class HelperTest {

	static File file = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "testJava.java");
	Helper helper = new Helper();
	int nbLinesInFile = 0;
	int nbLinesInCode = 0;

	@Before
	public void countLines() throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
		try {
			byte[] c = new byte[1024];
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++nbLinesInFile;
					}
				}
			}

		} finally {
			is.close();
		}
	}

	@Test
	public void CompareCodeSource() {
		LinkedHashMap<String, String> map = helper.getSourceCode(Utils.getResourceFromWithin(Constants.testPrimitverFolder));
		for (String key : map.keySet()) {
			nbLinesInCode = countLinesInString(map.get(key));
		}
		assertEquals(nbLinesInCode, nbLinesInFile);

	}

	private static int countLinesInString(String str) {
		String[] lines = str.split("\r\n|\r|\n");
		return lines.length;
	}
}
