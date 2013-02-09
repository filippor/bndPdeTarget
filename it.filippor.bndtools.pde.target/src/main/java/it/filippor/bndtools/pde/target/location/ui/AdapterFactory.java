package it.filippor.bndtools.pde.target.location.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.pde.ui.target.ITargetLocationEditor;
import org.eclipse.pde.ui.target.ITargetLocationUpdater;

public class AdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if(adapterType.equals(ILabelProvider.class))
			return new BndWorkspaceTargetLocationLabelProvider();
		if(adapterType.equals(ITargetLocationEditor.class))
			return new BndWorkspaceTargetLocationEditor();
		if(adapterType.equals(ITargetLocationUpdater.class))
			return new BndWorkspaceTargetLocationUpdater();
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{ILabelProvider.class,ITargetLocationEditor.class,ITargetLocationUpdater.class};
	}

}
