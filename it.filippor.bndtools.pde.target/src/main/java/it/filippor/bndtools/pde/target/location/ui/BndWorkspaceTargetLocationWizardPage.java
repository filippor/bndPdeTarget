package it.filippor.bndtools.pde.target.location.ui;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class BndWorkspaceTargetLocationWizardPage extends WizardPage {

	protected BndWorkspaceTargetLocationWizardPage() {
		super("new bndtools");
		
	}

	public File getWorkspaceDir(){
		return null;
	}
	
	@Override
	public void createControl(Composite parent) {
		setTitle("Add Files to Repository");
		
		// TODO interface to read workspace path
		
        Composite composite = new Composite(parent, SWT.NONE);
        
        new Label(composite, SWT.NONE).setText("Selected files:");
        setControl(composite);
		setPageComplete(true);
		
	}

}