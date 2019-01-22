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
import org.eclipse.jface.preference.IPreferencePageContainer;
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

	@Override
	public void init(IWorkbench CogniWorkbench) {
		// TODO Auto-generated method stub		
	}

	@Override
	protected Control createContents(org.eclipse.swt.widgets.Composite parent) {
		//return parent;
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout(SWT.VERTICAL));

		final Group group1 = new Group(container, SWT.SHADOW_IN);
		group1.setText("Source of CrySL rules ");
		group1.setLayout(new RowLayout(SWT.VERTICAL));

	    String[] choices = {"Default JCA Rules", "Default JSSE rules","Default Tink rules"};
	    final Combo combo = new Combo(group1, SWT.DROP_DOWN);
	    combo.setItems(choices);
	    //Default Item
	    combo.select(0);
	    combo.addSelectionListener(new SelectionAdapter() {
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
	    });
	    
	    Button checkBox3 = new Button(group1,SWT.CHECK);
	    checkBox3.setText("En-/Disable automated analysis when saving");
	    
	    Button checkBox2 = new Button(group1,SWT.CHECK);
	    checkBox2.setText("En-/Disable automatic analysis of dependencies on change");
	    
	    Button checkBox1 = new Button(group1,SWT.CHECK);
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
		final Combo advCombo1 = new Combo(group2, SWT.DROP_DOWN);
		advCombo1.setItems(choices2);
		advCombo1.select(0);
		advCombo1.setVisibleItemCount(100);
		
		final Label label2 = new Label(group2, SWT.SHADOW_IN);
		label2.setText("Entry point");
		
		String[] choices3 = {"getImageDescriptor","copyClaferHeader","printClafer"};
		final Combo advCombo2 = new Combo(group2, SWT.DROP_DOWN);
		advCombo2.setItems(choices3);
		advCombo2.select(0);
		
		final Label label3 = new Label(group2, SWT.SHADOW_IN);
		label3.setText("Error-marker types");
		
		String[] choices4 = {"Error", "Warning", "Info", "None"};
		final Combo advCombo3 = new Combo(group2, SWT.DROP_DOWN);
		advCombo3.setItems(choices4);
		advCombo3.select(0);
		
		return parent;
	}
	
 }
