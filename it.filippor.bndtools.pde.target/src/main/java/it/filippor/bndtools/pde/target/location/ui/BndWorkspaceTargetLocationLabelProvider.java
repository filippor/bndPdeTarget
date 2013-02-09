package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.Activator;
import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class BndWorkspaceTargetLocationLabelProvider extends LabelProvider {

	public BndWorkspaceTargetLocationLabelProvider(
			IBndWorkspaceTargetLocation adaptableObject) {
	}

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof IBndWorkspaceTargetLocation)
			return Activator.getDefault().getImageRegistry()
					.get(Activator.BNDTOOLS_LOGO_IMAGE_ID);
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IBndWorkspaceTargetLocation)
			return "Bnd Workspace ("
					+ ((IBndWorkspaceTargetLocation) element).getWorkspaceDir()
					+ ")";
		return null;
	}

}
