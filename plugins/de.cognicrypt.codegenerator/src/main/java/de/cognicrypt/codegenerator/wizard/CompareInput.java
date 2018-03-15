package de.cognicrypt.codegenerator.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

class CompareInput extends CompareEditorInput {	
	//private InstanceListPage instanceListPage;

	public CompareInput(InstanceListPage instanceListPage) {
		super(new CompareConfiguration());
		//this.instanceListPage = instanceListPage;
	}

	protected Object prepareInput(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
		CompareItem left = new CompareItem("Left", "old content");
		CompareItem right = new CompareItem("Right", "new content");
		return new DiffNode(left, right);
	}
}

class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

	private String contents, name;
	private long time;

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

	public long getModificationDate() {
		return time;
	}

	public String getName() {
		return name;
	}

	public String getString() {
		return contents;
	}

	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}
}