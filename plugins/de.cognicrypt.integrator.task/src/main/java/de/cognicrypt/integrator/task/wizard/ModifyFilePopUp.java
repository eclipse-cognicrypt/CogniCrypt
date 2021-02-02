package de.cognicrypt.integrator.task.wizard;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.widgets.*;


public class ModifyFilePopUp extends Dialog {
	PageForTaskIntegratorWizard pageForTaskIntegratorWizard;
	CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard;
	Dimension windowSize;
	String[] identifier;
	CompositeBrowseForFile compCryslTemplateAtInit;
	
	public ModifyFilePopUp(final Shell parentShell, PageForTaskIntegratorWizard pageForTaskIntegratorWizard, CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard, 
			String[] identifier) {
		super(parentShell);
		this.pageForTaskIntegratorWizard = pageForTaskIntegratorWizard;
		this.compositeChoiceForModeOfWizard = compositeChoiceForModeOfWizard;
		windowSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.identifier = identifier;		
	}

	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		// v2
		final Label reminder = new Label(container, SWT.NONE);
		reminder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		reminder.setText(Constants.IDENTIFIER_NOT_UNIQUE_WARNING);
		
		final Composite compositeAllModes = new Composite(container, SWT.NONE);
		compositeAllModes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeAllModes.setVisible(true);
		compositeAllModes.setLayout(new GridLayout(1, false));
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(compositeAllModes,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth((int) (/*windowSize.getWidth()*0.27)*/ 950));
		scrolledComposite.setMinHeight((int) (/*windowSize.getHeight()*0.1)*/150));

		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(composite);

		final Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		composite_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		final Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout());
		composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		compCryslTemplateAtInit = new CompositeBrowseForFile(composite_2, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE, new String[] { "*.java" },
				Constants.PAGE_DESCRIPTION_FOR_MODIFY, pageForTaskIntegratorWizard, compositeChoiceForModeOfWizard);
		compCryslTemplateAtInit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		compCryslTemplateAtInit.setOptionalText(identifier[0]);
		compCryslTemplateAtInit.setPathText(IntegratorModel.getInstance().getTemplate(identifier[0]).toString());
		
		return container;
	}
	
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Selection dialog");
        
    	
    }

    @Override
    protected Point getInitialSize() {
        return new Point((int) (950/*windowSize.getWidth()*0.3*/), (int) (300/*windowSize.getHeight()*0.2*/)); // 1000, 350
    }
    
   
    @Override 
    protected void okPressed() {
    	
    	// check if id was used already and is in the template list 
    	boolean warningIdAlreadyUsed = false;
    	try {
	    	List<String> listOfIdentifierTemplateList = IntegratorModel.getInstance().getIdentifiers();
	    		for(int k = 0; k < listOfIdentifierTemplateList.size(); k++) {
	    			if(listOfIdentifierTemplateList.get(k).equals(identifier[0])) {
	    				// do nothing as the identifier of same element can be used again
	    			}else if(compCryslTemplateAtInit.getOptionalText().equals(listOfIdentifierTemplateList.get(k))) { // if its identifier of modified id no warning is needed
		    			MessageDialog.openError(getShell(), "Warning", "Because one or more identifier you chose are already in use the chosen file or files could not be added!");
		    			warningIdAlreadyUsed = true;
		    		}
	    		}
    	}catch (Exception e) {}
    	
    	if(warningIdAlreadyUsed == false) {
    		compositeChoiceForModeOfWizard.removeTemplates(identifier);
        	compositeChoiceForModeOfWizard.addTemplate(compCryslTemplateAtInit.getOptionalText(), new File(compCryslTemplateAtInit.getPathText()));
        	
    	}
    	
    	super.okPressed();
    }
    
    
}
