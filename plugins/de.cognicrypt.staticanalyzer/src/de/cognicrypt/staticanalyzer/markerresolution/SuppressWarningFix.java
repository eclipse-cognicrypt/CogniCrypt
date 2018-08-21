package de.cognicrypt.staticanalyzer.markerresolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;

/**
 * @author André Sonntag
 */
public class SuppressWarningFix implements IMarkerResolution {

	private String label;

	public SuppressWarningFix(String label) {
		super();
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {

		File warningsFile = new File(marker.getResource().getProject().getLocation().toOSString() + "\\Warnings.txt");
		if (!warningsFile.exists()) {
			try {
				warningsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			appendLine(warningsFile, marker);
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method adds a new line in the warnings suppress file
	 * 
	 * @param f
	 *            warning File
	 * @param m
	 *            marker
	 * @throws CoreException
	 * @throws IOException
	 */
	public void appendLine(File f, IMarker m) throws CoreException, IOException {

		int id = (int) m.getAttribute(IMarker.SOURCE_ID);
		String message = (String) m.getAttribute(IMarker.MESSAGE);
		int lineNumber = (int) m.getAttribute(IMarker.LINE_NUMBER);
		String ressource = m.getResource().getName();
		FileOutputStream fos = new FileOutputStream(f, true);
		String line = id + " File: " + ressource + " Linenumber: " + lineNumber + " Error: " + message + "\n";
		fos.write(line.getBytes());
		fos.close();
	}

}
