package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.ui.target.ITargetLocationEditor;

public class BndWorkspaceTargetLocationEditor implements ITargetLocationEditor {

	@Override
	public boolean canEdit(ITargetDefinition target,
			ITargetLocation targetLocation) {
		return targetLocation instanceof IBndWorkspaceTargetLocation;
	}

	@Override
	public IWizard getEditWizard(final ITargetDefinition target,
			final ITargetLocation targetLocation) {
		return new Wizard() {
			IBndWorkspaceTargetLocation targetLoc = (IBndWorkspaceTargetLocation) targetLocation;
			@Override
			public void addPages() {
				BndWorkspaceTargetLocationWizardPage page = new BndWorkspaceTargetLocationWizardPage(targetLoc);
				super.addPages();
				addPage(page);
			}
			@Override
			public boolean performFinish() {
				return targetLoc.validate().isOK();
			}
		};
	}

}
