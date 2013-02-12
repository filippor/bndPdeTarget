package it.filippor.bndtools.pde.target.location;

import it.filippor.bndtools.pde.target.Activator;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetLocationFactory;

import aQute.bnd.build.Workspace;

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
	
	public static IStatus validate(IPath workspaceDir) {
		try {
			if (!workspaceDir.toFile().exists())
				throw new FileNotFoundException("Directory " + workspaceDir
						+ " not exist");
			if (Workspace.getWorkspace(workspaceDir.toFile()) != null)
				return Status.OK_STATUS;
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"path is not valid bnd workspace");
			return status;
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IStatus.OK, e.getMessage(), e);
			;
			// Activator.log(status);
			return status;
		}
	}

}
