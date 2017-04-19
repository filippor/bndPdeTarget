package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.Activator;
import it.filippor.bndtools.pde.target.location.BndWorkspaceTargetLocationFactory;
import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;
import it.filippor.eclipse.databinding.util.ControlDecorationAndDialogUpdater;
import it.filippor.eclipse.databinding.util.IPathToStringConverter;
import it.filippor.eclipse.databinding.util.NegateBooleanConverter;
import it.filippor.eclipse.databinding.util.StringToIPathConverter;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BndWorkspaceTargetLocationWizardPage extends WizardPage {
	private Binding bndDefaultLoc;

	public final static class BNDLocationValidator implements IValidator {
		public IStatus validate(Object value) {
			if (value instanceof IPath) {
				IStatus status = BndWorkspaceTargetLocationFactory.validate((IPath) value);
				return status;
			}
			return new Status(ERROR, Activator.PLUGIN_ID, "invalid Type");
		}
	}
	private IBndWorkspaceTargetLocation targetLocation;

	public BndWorkspaceTargetLocationWizardPage(
			IBndWorkspaceTargetLocation location) {
		super("Configure bnd workspace");
		if(location == null) {
			isNew = true;
			this.targetLocation = BndWorkspaceTargetLocationFactory.getInstance();
		}else {
			isNew=false;
			this.targetLocation = location;
		}
	}

	private boolean isNew;
	
	private Text txtPath;
	private Button btnUseDefaultLocation;
	private Button btnBrowse;
	private Button btnDownloadBundles;
	private Button btnIncludeConfigurationBundles;
	private Binding bndPath;

	private ControlDecorationAndDialogUpdater updater;

	public IBndWorkspaceTargetLocation getTargetLocation() {
		return targetLocation;
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Configure Bnd Workspace Target Location");

		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(3, false));

		btnUseDefaultLocation = new Button(composite, SWT.CHECK);
		btnUseDefaultLocation.setSelection(isNew);
		btnUseDefaultLocation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		btnUseDefaultLocation.setText("Use Default Location");
		btnUseDefaultLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bndPath.updateModelToTarget();
			}
		});

		Label lblLocation = new Label(composite, SWT.NONE);
		lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblLocation.setText("Location:");

		txtPath = new Text(composite, SWT.BORDER);
		txtPath.setEnabled(false);
		txtPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(getShell(), SWT.OPEN);
				dd.setFilterPath(txtPath.getText());
				String path = dd.open();
				if (path != null) {
					IStatus status = BndWorkspaceTargetLocationFactory.validate(Path.fromOSString(path));
					if(status.isOK()){
						txtPath.setText(path);
						bndPath.updateTargetToModel();
						bndDefaultLoc.updateModelToTarget();
					}else {
						new ErrorDialog(getShell(), "Invalid directory", status.getMessage(), status,IStatus.ERROR).open();
					}
				}
			}
		});
		btnBrowse.setText("Browse");

		btnIncludeConfigurationBundles = new Button(composite, SWT.CHECK);
		btnIncludeConfigurationBundles.setLayoutData(new GridData(SWT.LEAD,
				SWT.CENTER, false, false, 3, 1));
		btnIncludeConfigurationBundles
				.setText("Include configuration  bundles");

		btnDownloadBundles = new Button(composite, SWT.CHECK);
		btnDownloadBundles.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER,
				false, false, 3, 1));
		btnDownloadBundles.setText("Download bundles");
		setPageComplete(true);
		initDataBindings();
		updater = new ControlDecorationAndDialogUpdater(this);
		ControlDecorationSupport.create(bndPath, SWT.LEAD | SWT.TOP, parent,
				updater);
		if(targetLocation.isUseEclipseWorkspace()) {
			txtPath.setEnabled(false);
//			btnBrowse.setEnabled(false);
		}

	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtPathObserveWidget1 = WidgetProperties.text(SWT.Modify).observe(txtPath);
		IObservableValue workspaceDirGetTargetLocationObserveValue = PojoProperties.value("workspaceDir").observe(targetLocation);
		UpdateValueStrategy targetToModel1 = new UpdateValueStrategy();
		targetToModel1.setConverter(new StringToIPathConverter());
		IValidator targetPathValidator = new BNDLocationValidator();
		targetToModel1.setAfterConvertValidator(targetPathValidator);
		UpdateValueStrategy modelToTarget1 = new UpdateValueStrategy();
		modelToTarget1.setConverter(new IPathToStringConverter());
		IValidator targetPathValidator1 = new BNDLocationValidator();
		modelToTarget1.setAfterGetValidator(targetPathValidator1);
		bndPath = bindingContext.bindValue(observeTextTxtPathObserveWidget1, workspaceDirGetTargetLocationObserveValue, targetToModel1, modelToTarget1);
		//
		IObservableValue observeSelectionBtnUseDefaultLocationBundlesObserveWidget = WidgetProperties.selection().observe(btnUseDefaultLocation);
		IObservableValue useEclipseWorkspaceTargetLocationObserveValue = PojoProperties.value("useEclipseWorkspace").observe(targetLocation);
		bndDefaultLoc = bindingContext.bindValue(observeSelectionBtnUseDefaultLocationBundlesObserveWidget, useEclipseWorkspaceTargetLocationObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnIncludeConfigurationBundlesObserveWidget_1 = WidgetProperties.selection().observe(btnIncludeConfigurationBundles);
		IObservableValue importCnfGetTargetLocationObserveValue = PojoProperties.value("importCnf").observe(targetLocation);
		bindingContext.bindValue(observeSelectionBtnIncludeConfigurationBundlesObserveWidget_1, importCnfGetTargetLocationObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnDownloadBundlesObserveWidget = WidgetProperties.selection().observe(btnDownloadBundles);
		IObservableValue downloadAllGetTargetLocationObserveValue = PojoProperties.value("downloadAll").observe(targetLocation);
		bindingContext.bindValue(observeSelectionBtnDownloadBundlesObserveWidget, downloadAllGetTargetLocationObserveValue, null, null);
		//
		IObservableValue observeEnabledTxtPathObserveWidget = WidgetProperties.enabled().observe(txtPath);
		IObservableValue observeSelectionBtnUseDefaultLocationObserveWidget = WidgetProperties.selection().observe(btnUseDefaultLocation);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
		modelToTarget.setConverter(new NegateBooleanConverter());
		bindingContext.bindValue(observeEnabledTxtPathObserveWidget, observeSelectionBtnUseDefaultLocationObserveWidget, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), modelToTarget);
		//
		IObservableValue observeEnabledBtnDownloadBundlesObserveWidget = WidgetProperties.enabled().observe(btnDownloadBundles);
		IObservableValue observeSelectionBtnIncludeConfigurationBundlesObserveWidget = WidgetProperties.selection().observe(btnIncludeConfigurationBundles);
		bindingContext.bindValue(observeEnabledBtnDownloadBundlesObserveWidget, observeSelectionBtnIncludeConfigurationBundlesObserveWidget, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		return bindingContext;
	}
}