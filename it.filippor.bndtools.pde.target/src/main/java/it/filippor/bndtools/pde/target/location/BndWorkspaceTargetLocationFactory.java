package it.filippor.bndtools.pde.target.location;

import it.filippor.bndtools.pde.target.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetLocationFactory;

public class BndWorkspaceTargetLocationFactory implements ITargetLocationFactory{

	@Override
	public ITargetLocation getTargetLocation(String type, String serializedXML)
			throws CoreException {
		if(BndWorkspaceTargetLocation.TYPE.equals(type))
			return new BndWorkspaceTargetLocationTmp(serializedXML);
		
		return null;
	}

	public static IPath getDefaultWorkspaceDir() {
		IWorkspace eclipseWorkspace = ResourcesPlugin.getWorkspace();
		IProject cnfProject = eclipseWorkspace.getRoot().getProject("bnd");
	
		if (!cnfProject.exists())
			cnfProject = eclipseWorkspace.getRoot().getProject("cnf");
	
		IPath workspaceDir;
		if (cnfProject.exists()) {
			if (!cnfProject.isOpen())
				try {
					cnfProject.open(null);
				} catch (CoreException e) {
					Activator.logException(e);
				}
			IPath cnfDir = cnfProject.getLocation();
			workspaceDir = cnfDir.removeLastSegments(1);
		} else {
			// Have to assume that the eclipse workspace == the bnd workspace,
			// and cnf hasn't been imported yet.
			workspaceDir = eclipseWorkspace.getRoot().getLocation();
		}
		return workspaceDir;
	}

	public static IBndWorkspaceTargetLocation getInstance()  {
		return new BndWorkspaceTargetLocationTmp(getDefaultWorkspaceDir());
	}

}
