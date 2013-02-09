package it.filippor.bndtools.pde.target.location;

import java.io.File;

import org.eclipse.pde.core.target.ITargetLocation;

public interface IBndWorkspaceTargetLocation extends ITargetLocation{

	public abstract File getWorkspaceDir();

}