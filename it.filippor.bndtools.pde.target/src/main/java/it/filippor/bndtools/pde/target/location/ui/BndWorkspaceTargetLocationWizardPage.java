package it.filippor.bndtools.pde.target.location.ui;

import it.filippor.bndtools.pde.target.Activator;
import it.filippor.bndtools.pde.target.location.BndWorkspaceTargetLocationFactory;
import it.filippor.bndtools.pde.target.location.IBndWorkspaceTargetLocation;
import it.filippor.bndtools.pde.target.util.IPathStringValidator;
import it.filippor.bndtools.pde.target.util.IPathToStringConverter;
import it.filippor.bndtools.pde.target.util.NegateBooleanConverter;
import it.filippor.bndtools.pde.target.util.StringToIPathConverter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationUpdater;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
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

	private IBndWorkspaceTargetLocation targetLocation;

	public BndWorkspaceTargetLocationWizardPage(
			IBndWorkspaceTargetLocation location) {
		super("Configure bnd workspace");
		if(targetLocation == null) {
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
	private Binding binding;
	private Binding binding1;

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
		btnUseDefaultLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnUseDefaultLocation.getSelection()) {
					IPath defaultWorkspaceDir = BndWorkspaceTargetLocationFactory
							.getDefaultWorkspaceDir();
					txtPath.setText(defaultWorkspaceDir.toPortableString());
					binding.updateTargetToModel();
				}
			}
		});

		btnUseDefaultLocation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		btnUseDefaultLocation.setText("Use Default Location");

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
				if (path != null)
					txtPath.setText(path);
				binding.updateTargetToModel();
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
		ControlDecorationUpdater updater = new ControlDecorationUpdater() {
			Map<ControlDecoration, IStatus> statusMap = new HashMap<>();

			@Override
			protected void update(ControlDecoration decoration, IStatus status) {
				super.update(decoration, status);
				if (status == null || status.isOK())
					statusMap.remove(decoration);
				else
					statusMap.put(decoration, status);
				setErrorMessage(null);
				for (IStatus status1 : statusMap.values()) {
					if (!status1.isOK()) {
						setErrorMessage(status1.getMessage());
						break;
					}
				}
			}
		};
		ControlDecorationSupport.create(binding, SWT.LEAD | SWT.TOP, parent,
				updater);
		ControlDecorationSupport.create(binding1, SWT.LEAD | SWT.TOP, parent,
				updater);

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeSelectionBtnIncludeConfigurationBundlesObserveWidget_1 = WidgetProperties
				.selection().observe(btnIncludeConfigurationBundles);
		IObservableValue importCnfGetTargetLocationObserveValue = PojoProperties
				.value("importCnf").observe(getTargetLocation());
		bindingContext.bindValue(
				observeSelectionBtnIncludeConfigurationBundlesObserveWidget_1,
				importCnfGetTargetLocationObserveValue, null, null);
		//
		IObservableValue observeTextTxtPathObserveWidget = WidgetProperties
				.text().observe(txtPath);
		IObservableValue workspaceDirGetTargetLocationObserveValue = PojoProperties
				.value("workspaceDir").observe(getTargetLocation());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToIPathConverter());
		strategy.setAfterGetValidator(new IPathStringValidator());
		IValidator targetPathValidator = new IValidator() {

			public IStatus validate(Object value) {
				if (value instanceof IPath) {
					IStatus status = targetLocation.validate((IPath) value);
					return status;
				}
				return new Status(ERROR, Activator.PLUGIN_ID, "invalid Type");
			}
		};
		strategy.setAfterConvertValidator(targetPathValidator);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IPathToStringConverter());
		binding = bindingContext
				.bindValue(observeTextTxtPathObserveWidget,
						workspaceDirGetTargetLocationObserveValue, strategy,
						strategy_1);
		//
		IObservableValue observeTextTxtPathObserveWidget1 = WidgetProperties
				.text(SWT.Modify).observe(txtPath);
		binding1 = bindingContext
				.bindValue(observeTextTxtPathObserveWidget1,
						workspaceDirGetTargetLocationObserveValue, strategy,
						strategy_1);

		//
		IObservableValue observeEnabledTxtPathObserveWidget = WidgetProperties
				.enabled().observe(txtPath);
		IObservableValue observeSelectionBtnUseDefaultLocationObserveWidget = WidgetProperties
				.selection().observe(btnUseDefaultLocation);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
		IConverter converter = new NegateBooleanConverter();
		modelToTarget.setConverter(converter);
		bindingContext.bindValue(observeEnabledTxtPathObserveWidget,
				observeSelectionBtnUseDefaultLocationObserveWidget, null,
				modelToTarget);
		//
		IObservableValue observeEnabledBtnBrowseObserveWidget = WidgetProperties
				.enabled().observe(btnBrowse);
		IObservableValue observeSelectionBtnUseDefaultLocationObserveWidget_1 = WidgetProperties
				.selection().observe(btnUseDefaultLocation);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new NegateBooleanConverter());
		bindingContext.bindValue(observeEnabledBtnBrowseObserveWidget,
				observeSelectionBtnUseDefaultLocationObserveWidget_1, null,
				strategy_2);
		//
		IObservableValue observeEnabledBtnDownloadBundlesObserveWidget = WidgetProperties
				.enabled().observe(btnDownloadBundles);
		IObservableValue observeSelectionBtnIncludeConfigurationBundlesObserveWidget = WidgetProperties
				.selection().observe(btnIncludeConfigurationBundles);
		bindingContext.bindValue(observeEnabledBtnDownloadBundlesObserveWidget,
				observeSelectionBtnIncludeConfigurationBundlesObserveWidget,
				null, null);
		//
		IObservableValue observeSelectionBtnDownloadBundlesObserveWidget = WidgetProperties
				.selection().observe(btnDownloadBundles);
		IObservableValue downloadAllGetTargetLocationObserveValue = PojoProperties
				.value("downloadAll").observe(getTargetLocation());
		bindingContext.bindValue(
				observeSelectionBtnDownloadBundlesObserveWidget,
				downloadAllGetTargetLocationObserveValue, null, null);
		//
		return bindingContext;
	}
}