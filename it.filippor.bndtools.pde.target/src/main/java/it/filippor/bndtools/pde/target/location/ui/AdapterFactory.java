package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.ILabelProvider;

public class AdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if(adapterType.equals(ILabelProvider.class))
			return new BndWorkspaceTargetLocationLabelProvider((IBndWorkspaceTargetLocation)adaptableObject);
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{ILabelProvider.class};
	}

}
