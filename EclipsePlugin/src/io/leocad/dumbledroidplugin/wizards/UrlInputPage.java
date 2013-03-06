package io.leocad.dumbledroidplugin.wizards;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class UrlInputPage extends WizardPage {
	
	private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"});

	private Text mUrlText;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public UrlInputPage(ISelection selection) {
		super("wizardPage");
		setTitle("Dumbledroid Model Creator");
		setDescription("This wizard creates a new Java class from a JSON/XML to use with the Dumbledroid framework.");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&URL:");

		mUrlText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mUrlText.setLayoutData(gd);
		mUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		setPageComplete(false);
		setControl(container);
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		
		final String urlText = mUrlText.getText();
		
		if (urlText.length() == 0) {
			updateStatus("URL must be specified");
			return;
		}
		
		if (!URL_VALIDATOR.isValid(urlText)) {
			updateStatus("URL must be valid");
			return;
		}
		
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}