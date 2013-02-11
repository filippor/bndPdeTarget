package it.filippor.bndtools.pde.target.util;

import it.filippor.bndtools.pde.target.Activator;
import it.filippor.bndtools.pde.target.location.ui.BndWorkspaceTargetLocationWizardPage;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

public class IPathStringValidator implements IValidator {
	@Override
	public IStatus validate(Object value) {
		if(value == null||value.toString().isEmpty())return new Status(BndWorkspaceTargetLocationWizardPage.ERROR, Activator.PLUGIN_ID, "path can not be null");
		else {
			IPath path = Path.fromPortableString(value.toString());
			if(path == null||path.isEmpty()||!path.isValidPath(value.toString()))
				return new Status(BndWorkspaceTargetLocationWizardPage.ERROR, Activator.PLUGIN_ID, "invalid path " + value.toString());
			return Status.OK_STATUS;
		}
	}
}