package it.filippor.bndtools.pde.target.listener;

import it.filippor.bndtools.pde.target.Activator;
import it.filippor.bndtools.pde.target.location.BndWorkspaceTargetLocation;
import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import org.bndtools.build.api.BuildListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;

public class BndBuildListener implements BuildListener {
	
	ITargetPlatformService targetService;
	public BndBuildListener() {
		targetService = (ITargetPlatformService) Activator.getDefault().acquireService(ITargetPlatformService.class.getName());
	}
//	private static List<WeakReference<IBndWorkspaceTargetLocation>> listeners = new LinkedList<WeakReference<IBndWorkspaceTargetLocation>>();

	/*public static synchronized void addListener(
			IBndWorkspaceTargetLocation listener) {
		listeners.add(new WeakReference<IBndWorkspaceTargetLocation>(listener));
	}*/

/*	public static synchronized void removeListener(
			IBndWorkspaceTargetLocation listener) {
		System.gc();
		for (Iterator<WeakReference<IBndWorkspaceTargetLocation>> iterator = listeners
				.iterator(); iterator.hasNext();) {
			WeakReference<IBndWorkspaceTargetLocation> location = iterator.next();
			if (location.get() == null || location.get() == listener) {
				iterator.remove();
			}
		}
	}
*/
	@Override
	public void buildStarting(IProject project) {
		// do nothing

	}

	@Override
	public synchronized void builtBundles(IProject project, IPath[] paths) {
		/*System.gc();
		for (Iterator<WeakReference<IBndWorkspaceTargetLocation>> iterator = listeners
				.iterator(); iterator.hasNext();) {
			WeakReference<IBndWorkspaceTargetLocation> location =iterator.next();
			IBndWorkspaceTargetLocation targetLocation = location.get();
			if (targetLocation == null) {
				listeners.remove(location);
			} else {
				targetLocation.update();
				LoadTargetDefinitionJob.load(targetLocation.getTargetDefinition());
//				targetLocation.getTargetDefinition().resolve(new NullProgressMonitor());
//				
			}
		}*/
		
		try {
			
			 ITargetDefinition targetDefinition = targetService.getWorkspaceTargetHandle().getTargetDefinition();
			for(ITargetLocation location: targetDefinition.getTargetLocations()){
				if(BndWorkspaceTargetLocation.TYPE.equals(location.getType())
						&& ((IBndWorkspaceTargetLocation)location).isUseEclipseWorkspace()){
					((IBndWorkspaceTargetLocation)location).update();
					LoadTargetDefinitionJob.load(targetDefinition);
					return;
				}
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

}
