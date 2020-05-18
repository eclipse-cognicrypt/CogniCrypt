package de.cognicrypt.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import de.cognicrypt.core.Activator;

public class UIUtils {

	public static Group addHeaderGroup(Composite parent, String text) {
		final Group headerGroup = new Group(parent, SWT.SHADOW_IN);
		headerGroup.setText(text);
		headerGroup.setLayout(new GridLayout(1, true));
		return headerGroup;
	}

	protected static void setWindow(final IWorkbenchWindow activeWorkbenchWindow) {
		Utils.window = activeWorkbenchWindow;
	}

	/**
	 * This method closes the currently open editor.
	 *
	 * @param editor
	 */
	public static void closeEditor(final IEditorPart editor) {
		final IWorkbenchPage workbenchPage = UIUtils.getCurrentlyOpenPage();
		if (workbenchPage != null) {
			workbenchPage.closeEditor(editor, true);
		}
	}

	/**
	 * This method returns the currently open page as an {@link IWorkbenchPage}.
	 *
	 * @return Current editor.
	 */
	public static IWorkbenchPage getCurrentlyOpenPage() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			return window.getActivePage();
		}
		return null;
	}

	/**
	 * This method gets the file that is currently opened in the editor as an {@link IFile}.
	 *
	 * @param part Editor part that contains the file.
	 * @return Currently open file.
	 */
	public static IFile getCurrentlyOpenFile(final IEditorPart part) {
		if (part != null) {
			final IEditorInput editorInput = part.getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				final FileEditorInput inputFile = (FileEditorInput) part.getEditorInput();
				return inputFile.getFile();
			}
		}
		return null;
	}

	/**
	 * This method returns the currently open editor as an {@link IEditorPart}.
	 *
	 * @return Current editor.
	 */
	public static IEditorPart getCurrentlyOpenEditor() {
		final Display defaultDisplay = Display.getDefault();
		final Runnable getWindow = () -> setWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		defaultDisplay.syncExec(getWindow);
		if (Utils.window == null) {
			try {
				Thread.sleep(500);
			}
			catch (final InterruptedException e) {
				Activator.getDefault().logError(e);
			}
			defaultDisplay.asyncExec(getWindow);
		}
	
		if (Utils.window != null) {
			return Utils.window.getActivePage().getActiveEditor();
		}
		return null;
	}

}
