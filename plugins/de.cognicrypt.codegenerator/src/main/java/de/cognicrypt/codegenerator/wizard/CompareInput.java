package de.cognicrypt.codegenerator.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

class CompareInput extends CompareEditorInput {

	private final String left;
	private final String right;
	private Object fRoot;

	public CompareInput(String left, String right) {
		super(new CompareConfiguration());
		setTitle("Compare Code");
		this.left = left;
		this.right = right;
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		String leftLabel = "Old Source";
		String rightLabel = "Modified Source";

		getCompareConfiguration().setLeftLabel(leftLabel);
		getCompareConfiguration().setRightLabel(rightLabel);

		Differencer d = new Differencer();
		fRoot = d.findDifferences(false, monitor, null, null, new CompareItem(leftLabel, left), new CompareItem(rightLabel, right));
		return fRoot;
	}

}

class CompareItem implements IStreamContentAccessor, ITypedElement {

	private String contents, name;

	CompareItem(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(contents.getBytes());
	}

	public Image getImage() {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public String getString() {
		return this.contents;
	}

	public String getType() {
		return "java";
	}
}