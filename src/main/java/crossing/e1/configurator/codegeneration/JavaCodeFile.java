package crossing.e1.configurator.codegeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Represents a Java source code file.
 */
public class JavaCodeFile {

	private ArrayList<String> sourceCode = new ArrayList<String>();
	private File javaCodeFile;

	public JavaCodeFile(String path) {
		javaCodeFile = new File(path);
	}

	public void addCodeLine(String codeLine) {
		sourceCode.add(codeLine + "\n");
	}

	/**
	 * Writes the stored source code to the disk.
	 * 
	 * @throws Exception
	 */
	public File writeToDisk() throws Exception {
		try (FileOutputStream fileOutputStream = new FileOutputStream(javaCodeFile)) {
			for (String codeLine : sourceCode) {
				fileOutputStream.write(codeLine.getBytes());
			}
		} catch (Exception e) {
			throw new Exception("Writing source code to file failed.");
		}

		return javaCodeFile;

	}

	/**
	 * Output code on console for tests
	 */
	public void printCode() {
		for (String codeLine : sourceCode) {
			System.out.println(codeLine);
		}
	}
}
