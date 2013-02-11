package it.filippor.bndtools.pde.target.location;

import it.filippor.bndtools.pde.target.Activator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.core.target.TargetFeature;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.version.Version;

/**
 * @author frossoni 
 */
public class BndWorkspaceTargetLocation  extends PlatformObject implements IBndWorkspaceTargetLocation{

	private Workspace workspace ;
	private IPath workspaceDir = null;

	public BndWorkspaceTargetLocation() throws CoreException {
		this(getDefaultWorkppaceDir());
	}
	public BndWorkspaceTargetLocation(IPath workspaceDir) throws CoreException {
		if(workspaceDir == null)
			this.workspaceDir = getDefaultWorkppaceDir();
		else
			this.workspaceDir = workspaceDir;
	}
	public BndWorkspaceTargetLocation(String serializedXML) throws CoreException {
		// TODO read and persist workspace location
		this();
	}

	private  Workspace getWorkspace() throws Exception  {
		if (workspace != null)
			return workspace;

		workspace = Workspace.getWorkspace(workspaceDir.toFile());

		// Initialize projects in synchronized block
		workspace.getBuildOrder();
		

		return workspace;
	}

	private static IPath getDefaultWorkppaceDir() throws CoreException {
		IWorkspace eclipseWorkspace = ResourcesPlugin.getWorkspace();
		IProject cnfProject = eclipseWorkspace.getRoot().getProject("bnd");

		if (!cnfProject.exists())
			cnfProject = eclipseWorkspace.getRoot().getProject("cnf");
		
		IPath workspaceDir;
		if (cnfProject.exists()) {
			if (!cnfProject.isOpen())
				cnfProject.open(null);
			IPath cnfDir = cnfProject.getLocation();
			workspaceDir = cnfDir.removeLastSegments(1);
		} else {
			// Have to assume that the eclipse workspace == the bnd workspace,
			// and cnf hasn't been imported yet.
			workspaceDir = eclipseWorkspace.getRoot().getLocation();
		}
		return workspaceDir;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getLocation(boolean resolve) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize() {
		return "<location>bndtools</location>";
	}

	

	protected TargetBundle[] resolveBundles(ITargetDefinition definition,
			IProgressMonitor monitor) throws CoreException {
		
		try {
			Collection<Project> allProjects = getWorkspace().getAllProjects();
			List<RepositoryPlugin> repositories = getWorkspace().getRepositories();
			List<TargetBundle> bundles = new ArrayList<>(allProjects.size());
			for (RepositoryPlugin repositoryPlugin : repositories) {
				List<String> list = repositoryPlugin.list(null);
				for (String string : list) {
					SortedSet<Version> versions = repositoryPlugin.versions(string);
					for (Version version : versions) {
						File file = repositoryPlugin.get(string, version, null);
						bundles.add(createBundle(file));
					}
				}
			}
			for (Project project : allProjects) {
//				monitor.subTask("process project " + project.getName());
				for (File file : project.getBuildFiles(true)) {
					bundles.add(createBundle(file,project));
				}
			}
			return bundles.toArray(new TargetBundle[bundles.size()]);
		} catch (Exception e) {
			if(e instanceof CoreException) throw (CoreException)e;
			e.printStackTrace();
		}
		return new TargetBundle[0];
	}
	private TargetBundle createBundle(File file, Project project) throws CoreException {
		TargetBundle bundle = createBundle(file);
		//TODO: add source
		return bundle;
	}
	private TargetBundle createBundle(File file) throws CoreException {
		return new TargetBundle(file);
	}
	protected TargetFeature[] resolveFeatures(ITargetDefinition definition,
			IProgressMonitor monitor) throws CoreException {
		return null;
	}
	
	@Override
	public IStatus resolve(ITargetDefinition definition,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}
	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public IStatus getStatus() {
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}
	@Override
	public TargetBundle[] getBundles() {
		try {
			return resolveBundles(null, null);
		} catch (CoreException e) {
			Activator.logException(e);
		}
		return null;
	}
	@Override
	public TargetFeature[] getFeatures() {
		return null;
	}
	@Override
	public String[] getVMArguments() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IPath getWorkspaceDir() {
		return workspaceDir;
	}
	public void setWorkspaceDir(IPath workspaceDir) {
	}
	public void setDownloadAll(boolean downloadAll) {
	}
	public boolean isDownloadAll() {
		return false;
	}
	public void setImportCnf(boolean importCnf) {
	}
	public boolean isImportCnf() {
		return false;
	}
	@Override
	public IStatus update() {
		// TODO Auto-generated method stub
		return null;
	}
	public IStatus validate() {
		return null;
	}
	@Override
	public IStatus validate(IPath workspaceDir1) {
		// TODO Auto-generated method stub
		return null;
	}
}