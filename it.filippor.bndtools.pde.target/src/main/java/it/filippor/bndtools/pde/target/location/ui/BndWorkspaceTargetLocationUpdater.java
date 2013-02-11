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
		return targetLocation instanceof IBndWorkspaceTargetLocation;
	}

	@Override
	public IStatus update(ITargetDefinition target,
			ITargetLocation targetLocation, IProgressMonitor monitor) {
		return ((IBndWorkspaceTargetLocation)targetLocation).update();
	}

}
