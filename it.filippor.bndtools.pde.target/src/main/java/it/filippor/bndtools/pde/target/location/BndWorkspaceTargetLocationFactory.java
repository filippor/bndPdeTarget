package it.filippor.bndtools.pde.target.location;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
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

	public static ITargetLocation getInstance(File workspaceDir) throws CoreException {
		return new BndWorkspaceTargetLocationTmp(workspaceDir);
	}

}
