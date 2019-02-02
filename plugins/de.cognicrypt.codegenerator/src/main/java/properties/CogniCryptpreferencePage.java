package properties;

//import java.awt.Component;
//import java.awt.Composite;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.cognicrypt.codegenerator.Activator;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;



public class CogniCryptpreferencePage extends PreferencePage implements IWorkbenchPreferencePage{
	 


	public CogniCryptpreferencePage() {}
	
	Combo combo;
	Button checkBox1;
	Button checkBox2;
	Button checkBox3;
	Combo advCombo1;
	//Button advCombo2;
	Combo advCombo3;
	
	
	
	@Override
	public void init(IWorkbench CogniWorkbench) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	protected Control createContents(org.eclipse.swt.widgets.Composite parent) {
		
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout(SWT.VERTICAL));

		final Group group1 = new Group(container, SWT.SHADOW_IN);
		group1.setText("Source of CrySL rules ");
		group1.setLayout(new RowLayout(SWT.VERTICAL));

	    String[] choices = {"Default JCA Rules", "Default JSSE rules","Default Tink rules"};
	    combo = new Combo(group1, SWT.DROP_DOWN);
	    combo.setItems(choices);

	   /* combo.addSelectionListener(new SelectionAdapter() {
	    	@Override
	        public void widgetSelected(SelectionEvent event) {
	    		if(combo.getSelectionIndex() == 0) {
	    			
	    			System.out.println("yaaaaaaay");
	    		}else if(combo.getSelectionIndex() == 1) {
	    			System.out.println("naaaaaaay");
	    		}else if(combo.getSelectionIndex() == 2) {
	    			System.out.println("laaaaaaay");
	    		}else if(combo.getSelectionIndex() == 3) {
	    			System.out.println("gaaaaaaay");
	    		}
	        }
	    });*/
	    
	    checkBox3 = new Button(group1,SWT.CHECK);
	    checkBox3.setText("En-/Disable automated analysis when saving");
	    
	    checkBox2 = new Button(group1,SWT.CHECK);
	    checkBox2.setText("En-/Disable automatic analysis of dependencies on change");
	    
	    checkBox1 = new Button(group1,SWT.CHECK);
	    checkBox1.setText("En-/Disable automatic analysis of dependencies");
	    checkBox1.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	        	
	        	checkBox2.setSelection(true);	    	   
	        }
	    });
	    
	    final Group group2 = new Group(container, SWT.SHADOW_ETCHED_IN);
	    group2.setText("Advance Options");
	    group2.setLayout(new RowLayout(SWT.VERTICAL));
		
		final Label label1 = new Label(group2, SWT.SHADOW_IN);
		label1.setText("Call-graph construction algorithm");
	
		String[] choices2 = {"CHA","Spark"};
		advCombo1 = new Combo(group2, SWT.DROP_DOWN);
		advCombo1.setItems(choices2);
		
		/*final Label label2 = new Label(group2, SWT.SHADOW_IN);
		label2.setText("Entry point");
		String[] choices3 = {"getImageDescriptor","copyClaferHeader","printClafer"};
		advCombo2 = new Combo(group2, SWT.DROP_DOWN);
		advCombo2.setItems(choices3);
		advCombo2.select(0);*/
		
		final Label label3 = new Label(group2, SWT.SHADOW_IN);
		label3.setText("Error-marker types");
		
		String[] choices4 = {"Error", "Warning", "Info", "None"};
		advCombo3 = new Combo(group2, SWT.DROP_DOWN);
		advCombo3.setItems(choices4);
		
		initializeValues();
		return parent;
	}
	
	@Override
	public boolean performOk() {
	       storeValues();
	       Activator.getDefault().savePluginPreferences();
	       return true;
	}
	
    private void storeValues() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(ICogniCryptConstants.PRE_CHECKBOX1, checkBox1.getSelection());
        store.setValue(ICogniCryptConstants.PRE_CHECKBOX2, checkBox2.getSelection());
        store.setValue(ICogniCryptConstants.PRE_CHECKBOX3, checkBox3.getSelection());
        store.setValue(ICogniCryptConstants.PRE_COMBO, combo.getSelectionIndex());
        store.setValue(ICogniCryptConstants.PRE_ADV_COMBO1, advCombo1.getSelectionIndex());
        store.setValue(ICogniCryptConstants.PRE_ADV_COMBO3, advCombo3.getSelectionIndex());
    }
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
	    return Activator.getDefault().getPreferenceStore();
	}
	
	 private void initializeDefaults() {
		
	        IPreferenceStore store = getPreferenceStore();
	        checkBox1.setSelection(store
	                .getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX1));
	        checkBox2.setSelection(store
	                .getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX2));
	        checkBox3.setSelection(store
	                .getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX3));
	        combo.select(store.getDefaultInt(ICogniCryptConstants.PRE_COMBO));
	        advCombo1.select(store.getDefaultInt(ICogniCryptConstants.PRE_ADV_COMBO1));
	        advCombo3.select(store.getDefaultInt(ICogniCryptConstants.PRE_ADV_COMBO3));


	    }

	private void initializeValues() {

		IPreferenceStore store = getPreferenceStore();
		combo.select(store.getInt(ICogniCryptConstants.PRE_COMBO));
		checkBox1.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX1));
		checkBox2.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX2));
		checkBox3.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX3));
		advCombo1.select(store.getInt(ICogniCryptConstants.PRE_ADV_COMBO1));
//		advCombo2.setSelection(store.getBoolean(ICogniCryptconstants.PRE_ADV_COMBO2));
		advCombo3.select(store.getInt(ICogniCryptConstants.PRE_ADV_COMBO3));
	}
	
    @Override
	protected void performDefaults() {
        super.performDefaults();
        initializeDefaults();
    }
	
 }

