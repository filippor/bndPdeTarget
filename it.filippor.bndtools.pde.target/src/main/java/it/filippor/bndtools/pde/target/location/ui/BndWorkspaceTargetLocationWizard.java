package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.location.BndWorkspaceTargetLocationFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.ui.target.ITargetLocationWizard;

public class BndWorkspaceTargetLocationWizard extends Wizard implements ITargetLocationWizard {
	
	ITargetDefinition target;
	ITargetLocation targetLocation;
	private BndWorkspaceTargetLocationWizardPage targetLocationWizardPage;
	public BndWorkspaceTargetLocationWizard(){
		setWindowTitle("bndtools workspace ");
	}
	@Override
	public void setTarget(ITargetDefinition target) {
		this.target = target;
	}

	@Override
	public void addPages() {
		super.addPages();
		targetLocationWizardPage = new BndWorkspaceTargetLocationWizardPage();
		addPage(targetLocationWizardPage);
	}
	
	
	
	@Override
	public ITargetLocation[] getLocations() { 
		return new ITargetLocation[] {targetLocation};//target.getTargetLocations();
	}

	@Override
	public boolean performFinish() {
		try {
			targetLocation = BndWorkspaceTargetLocationFactory.getInstance(targetLocationWizardPage.getWorkspaceDir());
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}
}