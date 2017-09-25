package crossing.e1.configurator.codegeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

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

		StringBuilder stringBuilder = new StringBuilder();

		for (String codeLine : sourceCode) {
			stringBuilder.append(codeLine);
		}

		// take default Eclipse formatting options
		// FIXME warning
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		// initialise the compiler settings to be able to format 1.8 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);

		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);
		String javaCode = stringBuilder.toString();
		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, javaCode, 0, javaCode.length(), 0, System.getProperty("line.separator"));
		IDocument document = new Document(javaCode);

		try {
			textEdit.apply(document);
			System.out.println(document.get());
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream("eclipse-runtime-fork-crossing-tud/" + javaCodeFile)) {
			fileOutputStream.write(document.get().getBytes());
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
