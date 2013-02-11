package it.filippor.bndtools.pde.target.location;

import it.filippor.bndtools.pde.target.Activator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.core.target.TargetFeature;
import org.eclipse.pde.internal.core.target.AbstractBundleContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import aQute.bnd.build.DownloadBlocker;
import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.version.Version;
import aQute.service.reporter.Reporter;

/**
 * Tmp becouse eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=399942
 * 
 * @author frossoni
 */
@SuppressWarnings("restriction")
public class BndWorkspaceTargetLocationTmp extends AbstractBundleContainer
		implements IBndWorkspaceTargetLocation {// implements

	private boolean importCnf = true;
	private boolean downloadAll = false;

	
	@Override
	public IStatus validate() {
		return validate(workspaceDir);
	}
	@Override
	public IStatus validate(IPath workspaceDir) {
		try {
			if(Workspace.getWorkspace(workspaceDir.toFile())!=null)
				return Status.OK_STATUS;
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"path is not valid bnd workspace");
			return status;
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IStatus.OK, e.getMessage(), e);
			;
//			Activator.log(status);
			return status;
		}
	}

	@Override
	public boolean isImportCnf() {
		return importCnf;
	}

	@Override
	public void setImportCnf(boolean importCnf) {
		changeSupport.firePropertyChange("importCnf", this.importCnf,
				importCnf);
		this.importCnf = importCnf;
		update();
	}

	@Override
	public IStatus update() {
		super.clearResolutionStatus();
		return Status.OK_STATUS;
	}

	@Override
	public boolean isDownloadAll() {
		return downloadAll;
	}

	@Override
	public void setDownloadAll(boolean downloadAll) {
		changeSupport.firePropertyChange("downloadAll",
				this.downloadAll, downloadAll);
		this.downloadAll = downloadAll;
		update();
	}

	@Override
	public void setWorkspaceDir(IPath workspaceDir) {
		changeSupport.firePropertyChange("workspaceDir",
				this.workspaceDir, workspaceDir);
		this.workspaceDir = workspaceDir;
		update();
	}

	private IPath workspaceDir = null;

	

	public BndWorkspaceTargetLocationTmp(IPath workspaceDir) {
		this.workspaceDir = workspaceDir;
	}

	public BndWorkspaceTargetLocationTmp(String serializedXML)
			throws CoreException {
		
		DocumentBuilder parser;
		
			try {
				parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			Document doc = parser.parse(new InputSource(new StringReader(
					serializedXML)));

			Element root = doc.getDocumentElement();
			if (!root.getNodeName().equalsIgnoreCase("location")) {
				throw new CoreException(new Status(0, Activator.PLUGIN_ID,
						"error read target from xml"));
			}
			String nodeValue = root.getAttribute("workspaceDir");
				IPath f = Path.fromPortableString(nodeValue);
				if (f.toFile().exists()) {
					this.workspaceDir = f;
					return;
				}

			} catch (ParserConfigurationException | SAXException | IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "error in parse target location XML",e));
			}
		

		
		
	}

	private Workspace getWorkspace(IPath workspaceDir) throws Exception {
		

		Workspace workspace = Workspace.getWorkspace(workspaceDir.toFile());

		// Initialize projects in synchronized block
		workspace.getBuildOrder();

		return workspace;
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
		Element containerElement;
		Document document;
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			document = docBuilder.newDocument();
		} catch (Exception e) {
			Activator.log(e);
			return null;
		}

		containerElement = document.createElement("location");
		containerElement.setAttribute("type", getType());
		containerElement.setAttribute("workspaceDir",
				workspaceDir.toPortableString());
		document.appendChild(containerElement);

		try {
			StreamResult result = new StreamResult(new StringWriter());
			Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes"); //$NON-NLS-1$
			transformer.transform(new DOMSource(document), result);
			return result.getWriter().toString();
		} catch (Exception e) {
			Activator.log(e);

		}
		return null;
	}

	@Override
	public boolean isContentEqual(AbstractBundleContainer container) {
		return equals(container);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IBndWorkspaceTargetLocation))
			return false;
		BndWorkspaceTargetLocationTmp other = (BndWorkspaceTargetLocationTmp) obj;
		if (downloadAll != other.downloadAll)
			return false;
		if (importCnf != other.importCnf)
			return false;
		if (workspaceDir == null) {
			if (other.workspaceDir != null)
				return false;
		} else if (!workspaceDir.equals(other.workspaceDir))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (downloadAll ? 1231 : 1237);
		result = prime * result + (importCnf ? 1231 : 1237);
		result = prime * result
				+ ((workspaceDir == null) ? 0 : workspaceDir.hashCode());
		return result;
	}

	@Override
	protected TargetBundle[] resolveBundles(ITargetDefinition definition,
			final IProgressMonitor monitor) throws CoreException {

		try {
			monitor.beginTask("resolve bundle", 2);
			Collection<Project> allProjects = getWorkspace(workspaceDir).getAllProjects();
			final List<TargetBundle> bundles = new ArrayList<>(
					allProjects.size());
			IProgressMonitor projectM = new SubProgressMonitor(monitor,
					allProjects.size());
			for (Project project : allProjects) {
				if (monitor.isCanceled())
					return null;
				projectM.subTask("process project " + project.getName());
				for (File file : project.getBuildFiles(true)) {
					bundles.add(new TargetBundle(file));
				}
				projectM.worked(1);
			}
			projectM.done();
			IProgressMonitor cnfM = new SubProgressMonitor(monitor, 1);
			if (importCnf) {
				List<RepositoryPlugin> repositories = getWorkspace(workspaceDir)
						.getRepositories();
				IProgressMonitor repositoriesM = new SubProgressMonitor(cnfM,
						repositories.size());
				for (RepositoryPlugin repositoryPlugin : repositories) {
					if (monitor.isCanceled())
						return null;
					repositoriesM.subTask("process " + repositoryPlugin);
					List<String> list = repositoryPlugin.list(null);
					IProgressMonitor listM = new SubProgressMonitor(
							repositoriesM, list.size());
					for (String string : list) {
						listM.subTask("process" + string);
						if (monitor.isCanceled())
							return null;
						SortedSet<Version> versions = repositoryPlugin
								.versions(string);
						for (Version version : versions) {
							if (monitor.isCanceled())
								return null;
							Reporter reporter = new Processor() {

								@Override
								public void trace(String msg, Object... parms) {
									try {
										bundles.add(new TargetBundle(
												(File) parms[0]));
									} catch (CoreException e) {
										Activator.logException(e);
									}
								}

							};
							DownloadBlocker dwnListener = new DownloadBlocker(
									reporter) {

								@Override
								public boolean progress(File file,
										int percentage) throws Exception {
									if (!downloadAll)
										return false;
									if (monitor.isCanceled())
										return false;
									return super.progress(file, percentage);
								}

							};
							repositoryPlugin.get(string, version, null,
									dwnListener);
							if (downloadAll)
								dwnListener.getReason();
						}
						listM.worked(1);
					}
					listM.done();
					repositoriesM.worked(1);
				}
				repositoriesM.done();
			}
			cnfM.done();
			monitor.done();
			return bundles.toArray(new TargetBundle[bundles.size()]);
		} catch (Exception e) {
			if (e instanceof CoreException)
				throw (CoreException) e;
			Activator.log(e);
		}
		return new TargetBundle[0];
	}

	@Override
	protected TargetFeature[] resolveFeatures(ITargetDefinition definition,
			IProgressMonitor monitor) throws CoreException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation#
	 * getWorkspaceDir()
	 */
	@Override
	public IPath getWorkspaceDir() {
		return workspaceDir;
	}

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
}