package de.cognicrypt.codegenerator.wizard;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;

public class CompareWizard extends Wizard {

	private CompareAlgorithmPage compareAlgorithmPage;
	private InstanceListPage instanceListPage;
	private InstanceGenerator instanceGenerator;

	public CompareWizard(InstanceListPage instanceListPage, InstanceGenerator instanceGenerator) {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("Cryptography Task Configurator");
		ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "icons/cognicrypt-medium.png");
		setDefaultPageImageDescriptor(image);

		this.instanceListPage = instanceListPage;
		this.instanceGenerator = instanceGenerator;
	}

	@Override
	public void addPages() {
		this.compareAlgorithmPage = new CompareAlgorithmPage(instanceListPage, instanceGenerator);
		addPage(this.compareAlgorithmPage);
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
