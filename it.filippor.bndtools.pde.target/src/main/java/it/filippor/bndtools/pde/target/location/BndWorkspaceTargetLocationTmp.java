package it.filippor.bndtools.pde.target.location;

import it.filippor.bndtools.pde.target.Activator;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.core.target.TargetFeature;
import org.eclipse.pde.internal.core.target.AbstractBundleContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.RepositoryPlugin.DownloadListener;
import aQute.bnd.version.Version;

/**
 * Tmp becouse eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=399942
 * @author frossoni 
 */
public class BndWorkspaceTargetLocationTmp extends AbstractBundleContainer
		implements IBndWorkspaceTargetLocation {// implements
	public static final String TYPE = "bndWorkspace";

	private Workspace workspace;
	private File workspaceDir = null;

	public BndWorkspaceTargetLocationTmp() throws CoreException {
		this(getDefaultWorkspaceDir());
	}

	public BndWorkspaceTargetLocationTmp(File workspaceDir)
			throws CoreException {
		if (workspaceDir == null)
			this.workspaceDir = getDefaultWorkspaceDir();
		else
			this.workspaceDir = workspaceDir;
	}

	public BndWorkspaceTargetLocationTmp(String serializedXML)
			throws CoreException {
		if (serializedXML == null) {
			this.workspaceDir = getDefaultWorkspaceDir();
			return;
		}
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
			try {
				File f = new File(nodeValue);
				if (f.exists()) {
					this.workspaceDir = f;
					return;
				}
			} catch (Exception e) {

			}

		} catch (Exception e) {
			if (e instanceof CoreException)
				throw (CoreException) e;
			Activator.log(e);
		}

		try {
			File f = new File(serializedXML);
			if (f.exists()) {
				this.workspaceDir = f;
				return;
			}

		} catch (Exception e) {

		}

		Activator.logErrorMessage("use default");
		this.workspaceDir = getDefaultWorkspaceDir();
	}

	private Workspace getWorkspace() throws Exception {
		if (workspace != null)
			return workspace;

		workspace = Workspace.getWorkspace(workspaceDir);

		// Initialize projects in synchronized block
		workspace.getBuildOrder();

		return workspace;
	}

	private static File getDefaultWorkspaceDir() throws CoreException {
		IWorkspace eclipseWorkspace = ResourcesPlugin.getWorkspace();
		IProject cnfProject = eclipseWorkspace.getRoot().getProject("bnd");

		if (!cnfProject.exists())
			cnfProject = eclipseWorkspace.getRoot().getProject("cnf");

		File workspaceDir;
		if (cnfProject.exists()) {
			if (!cnfProject.isOpen())
				cnfProject.open(null);
			File cnfDir = cnfProject.getLocation().toFile();
			workspaceDir = cnfDir.getParentFile();
		} else {
			// Have to assume that the eclipse workspace == the bnd workspace,
			// and cnf hasn't been imported yet.
			workspaceDir = eclipseWorkspace.getRoot().getLocation().toFile();
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
		containerElement.setAttribute("workspaceDir", workspaceDir.getPath());
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
		if (container instanceof IBndWorkspaceTargetLocation) {
			return workspaceDir
					.equals(((IBndWorkspaceTargetLocation) container)
							.getWorkspaceDir());
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IBndWorkspaceTargetLocation) {
			IBndWorkspaceTargetLocation target = (IBndWorkspaceTargetLocation) obj;
			return workspaceDir.equals(target.getWorkspaceDir());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return workspaceDir.hashCode();
	}
	
	@Override
	protected TargetBundle[] resolveBundles(ITargetDefinition definition,
			IProgressMonitor monitor) throws CoreException {

		try {
			Collection<Project> allProjects = getWorkspace().getAllProjects();
			final List<TargetBundle> bundles = new ArrayList<>(
					allProjects.size());
			for (Project project : allProjects) {
				monitor.subTask("process project " + project.getName());
				for (File file : project.getBuildFiles(true)) {
					bundles.add(new TargetBundle(file));
				}
			}
			List<RepositoryPlugin> repositories = getWorkspace()
					.getRepositories();
			for (RepositoryPlugin repositoryPlugin : repositories) {
				List<String> list = repositoryPlugin.list(null);
				for (String string : list) {
					SortedSet<Version> versions = repositoryPlugin
							.versions(string);
					for (Version version : versions) {
						DownloadListener dwnListener = new DownloadListener() {

							@Override
							public void success(File file) throws Exception {
								if (file.exists()) {
									bundles.add(new TargetBundle(file));
									Activator.log(new Status(0,
											Activator.PLUGIN_ID, "added  "
													+ file));
								}
							}

							@Override
							public boolean progress(File file, int percentage)
									throws Exception {
								Activator.log(new Status(0,
										Activator.PLUGIN_ID,
										"interrupt download " + file));
								return false;
							}

							@Override
							public void failure(File file, String reason)
									throws Exception {
								Activator.log(new Status(0,
										Activator.PLUGIN_ID, "failed download "
												+ file + "  " + reason));

							}
						};
						repositoryPlugin
								.get(string, version, null, dwnListener);

					}
				}
			}

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
	public File getWorkspaceDir() {
		return workspaceDir;
	}
}