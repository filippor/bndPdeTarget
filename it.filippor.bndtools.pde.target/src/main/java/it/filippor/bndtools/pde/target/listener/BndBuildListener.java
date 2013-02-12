package it.filippor.bndtools.pde.target.listener;

import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bndtools.build.api.BuildListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;

public class BndBuildListener implements BuildListener {

	private static List<WeakReference<IBndWorkspaceTargetLocation>> listeners = new LinkedList<WeakReference<IBndWorkspaceTargetLocation>>();

	public static synchronized void addListener(
			IBndWorkspaceTargetLocation listener) {
		listeners.add(new WeakReference<IBndWorkspaceTargetLocation>(listener));
	}

	public static synchronized void removeListener(
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

	@Override
	public void buildStarting(IProject project) {
		// do nothing

	}

	@Override
	public synchronized void builtBundles(IProject project, IPath[] paths) {
		System.gc();
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
		}
	}

}
