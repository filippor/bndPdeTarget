package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.ui.target.ITargetLocationUpdater;

public class BndWorkspaceTargetLocationUpdater implements
		ITargetLocationUpdater {

	@Override
	public boolean canUpdate(ITargetDefinition target,
			ITargetLocation targetLocation) {
		// TODO Auto-generated method stub
		return targetLocation instanceof IBndWorkspaceTargetLocation;
	}

	@Override
	public IStatus update(ITargetDefinition target,
			ITargetLocation targetLocation, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return ((IBndWorkspaceTargetLocation)targetLocation).update();
	}

}
