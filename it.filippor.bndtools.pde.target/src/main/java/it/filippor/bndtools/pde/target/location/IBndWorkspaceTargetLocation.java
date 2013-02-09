package it.filippor.bndtools.pde.target.location;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.pde.core.target.ITargetLocation;

public interface IBndWorkspaceTargetLocation extends ITargetLocation{
	public static final String TYPE = "bndWorkspace";
	
	public abstract File getWorkspaceDir();

	public abstract void setWorkspaceDir(File workspaceDir);

	public abstract void setDownloadAll(boolean downloadAll);

	public abstract boolean isDownloadAll();

	public abstract void setImportCnf(boolean importCnf);

	public abstract boolean isImportCnf();

	public abstract IStatus update();

}