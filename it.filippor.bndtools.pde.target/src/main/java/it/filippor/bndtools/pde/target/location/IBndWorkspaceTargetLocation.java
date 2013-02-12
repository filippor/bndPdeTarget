package it.filippor.bndtools.pde.target.location;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.pde.core.target.ITargetLocation;

public interface IBndWorkspaceTargetLocation extends ITargetLocation{
	public static final String TYPE = "bndWorkspace";
	
	public abstract IPath getWorkspaceDir();

	public abstract void setWorkspaceDir(IPath workspaceDir);

	public abstract void setDownloadAll(boolean downloadAll);

	public abstract boolean isDownloadAll();

	public abstract void setImportCnf(boolean importCnf);

	public abstract boolean isImportCnf();

	public abstract IStatus update();

	public abstract IStatus validate();

	public abstract void setUseEclipseWorkspace(boolean useEclipseWorkspace);

	public abstract boolean isUseEclipseWorkspace();
	

}