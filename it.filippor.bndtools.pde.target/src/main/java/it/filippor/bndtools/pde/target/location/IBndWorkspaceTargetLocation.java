package it.filippor.bndtools.pde.target.location;

import java.io.File;

import org.eclipse.pde.core.target.ITargetLocation;

public interface IBndWorkspaceTargetLocation extends ITargetLocation{
	public static final String TYPE = "bndWorkspace";
	
	public abstract File getWorkspaceDir();

}