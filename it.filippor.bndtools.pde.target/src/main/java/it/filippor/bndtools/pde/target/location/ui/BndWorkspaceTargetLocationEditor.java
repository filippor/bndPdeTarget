package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import org.eclipse.jface.wizard.IWizard;
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
	public IWizard getEditWizard(ITargetDefinition target,
			ITargetLocation targetLocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
