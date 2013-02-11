package it.filippor.bndtools.pde.target.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationUpdater;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;

public class ControlDecorationAndDialogUpdater extends
		ControlDecorationUpdater {
	/**
	 * 
	 */
	private final DialogPage dialogPage;
	private boolean isWizardPage;

	/**
	 * @param dialogPage
	 */
	public ControlDecorationAndDialogUpdater(
			DialogPage wizardPage) {
		this.dialogPage = wizardPage;
		this.isWizardPage = dialogPage instanceof WizardPage;
 	}
	public ControlDecorationAndDialogUpdater(
			WizardPage wizardPage) {
		this.dialogPage = wizardPage;
		this.isWizardPage = true;
	}

	Map<ControlDecoration, IStatus> statusMap = new HashMap<>();

	@Override
	protected void update(ControlDecoration decoration, IStatus status) {
		super.update(decoration, status);
		if (status == null || status.isOK())
			statusMap.remove(decoration);
		else
			statusMap.put(decoration, status);
		this.dialogPage.setMessage(null);
		if(isWizardPage)
			((WizardPage)dialogPage).setPageComplete(true);
		for (IStatus status1 : statusMap.values()) {
			if (!status1.isOK()) {
				int messageType = getMessageType(status1);
				dialogPage.setMessage(status1.getMessage(), messageType);
				if(isWizardPage && messageType == IMessageProvider.ERROR)
					((WizardPage)dialogPage).setPageComplete(false);
				break;
			}
		}
	}
	int getMessageType(IStatus status) {
		if(status.matches(IStatus.ERROR))
			return IMessageProvider.ERROR;
		if(status.matches(IStatus.WARNING))
			return IMessageProvider.WARNING;
		if(status.matches(IStatus.INFO))
			return IMessageProvider.INFORMATION;
		if(status.matches(IStatus.CANCEL))
			return IMessageProvider.ERROR;
		return IMessageProvider.NONE;
	}
	public boolean hasError() {
		for (IStatus status : statusMap.values()) {
			if(status.matches(IStatus.ERROR)||status.matches(IStatus.CANCEL))
				return true;
		}
		return false;
	}
}