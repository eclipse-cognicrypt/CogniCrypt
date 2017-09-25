package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import crossing.e1.configurator.Constants;


public class CompositeForXsl extends Composite {

	public static Text xslTxtBox;
		public CompositeForXsl(Composite parent, int style) {
			super(parent,SWT.BORDER);
			this.setBounds(0,0,887,500);
			//setLayout(new RowLayout(SWT.HORIZONTAL));
			setLayout(null);
			
			//UI Widgets for xslPage
			xslTxtBox= new Text(this,SWT.MULTI|SWT.V_SCROLL);
			xslTxtBox.setBounds(0,0,887,500);
			xslTxtBox.setCursor(null);
			
			
			/*Combo xslComboVariable = new Combo(this,SWT.None);
			xslComboVariable.setBounds(555,0,100,30);
			xslComboVariable.setItems("Variable","Ravi","Rajiv","Andre");
			xslComboVariable.select(0);
			Button addXslTag = new Button(this, SWT.NONE);
			addXslTag.setText("Add Xsl Tag");
			addXslTag.setBounds(660, 0, 100, 30);*/
			/*addXslTag.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					
				}
			});
			*/
			// TODO Auto-generated constructor stub
		}
		// TODO Auto-generated constructor stub
	}


