package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * This class is responsible for displaying the methods related to the custom algorithm For instance, in case of primitive of type symmetric block cipher, the required methods are
 * encryption an decryption.
 * 
 * @author Ahmed Ben Tahar
 */

public class MethodSelectorPage extends WizardPage {

	private Label encryptionLabel;
	private File javaFile;

	public MethodSelectorPage(File file) {
		super("Methods Selector");
		setTitle("Methods Selector");
		setDescription("Getting methods-related algorithm");
		this.javaFile = file;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		encryptionLabel = new Label(container, SWT.NULL);
		encryptionLabel.setText("Select the encryption method:   ");		

		Combo encryptionCombo = new Combo(container, SWT.NONE);
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 140;
		encryptionCombo.setLayoutData(gd_combo);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		//Field assist
		final ControlDecoration deco = new ControlDecoration(encryptionCombo, SWT.CENTER | SWT.RIGHT);
		Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		deco.setDescriptionText("Sample text");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);
		deco.show();

		Label decryptionLabel = new Label(container, SWT.NONE);
		decryptionLabel.setText("Select the decryption method:   ");

		Combo decryptionCombo = new Combo(container, SWT.NONE);
		decryptionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		decryptionCombo.add(this.javaFile.getName());

//		Class cls;
//		try {
//			cls = loadClass(this.javaFile.getPath());
//			decryptionCombo.add(cls.getName());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		

	}
//	public Class loadClass( String ClassFolder) throws Exception {
//		return null;
//		URLClassLoader loader = new URLClassLoader(new URL []{
//			new URL("file://"+this.javaFile)
//		});
//		  String className = je.getName().substring(0,je.getName().length()-6);
//		    className = className.replace('/', '.');
//		    Class c = cl.loadClass(className);
//		return loader.getClass();
//	}
}
