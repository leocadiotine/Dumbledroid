package io.leocad.dumbledroidplugin.wizards;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UrlInputPage extends WizardPage {

	public static final String PAGE_NAME = "UrlInputPage";
	private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"});

	private Text mUrlText;
	private Button mRadioPojo;

	public UrlInputPage(ISelection selection) {
		super(PAGE_NAME);
		setTitle("Dumbledroid Model Creator");
		setDescription("This wizard creates a new Java class from a JSON/XML to use with the Dumbledroid framework.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&URL:");

		mUrlText = new Text(container, SWT.BORDER | SWT.SINGLE);
		mUrlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				onTextChanged();
			}
		});
		
		new Label(container, SWT.NONE); // Empty space
		
		mRadioPojo = new Button(container, SWT.RADIO);
		mRadioPojo.setText("Use POJO pattern (public fields and no getters/setters)");
		
		GridData gdHorizSpan = new GridData(GridData.VERTICAL_ALIGN_END);
		gdHorizSpan.horizontalSpan = 2;
		gdHorizSpan.horizontalAlignment = GridData.FILL;
		
		mRadioPojo.setLayoutData(gdHorizSpan);
		mRadioPojo.setSelection(true);
		
		Button radioGetSet = new Button(container, SWT.RADIO);
		radioGetSet.setText("Use Accessor pattern (private fields with getters and setters)");
		radioGetSet.setLayoutData(gdHorizSpan);
		radioGetSet.setSelection(false);

		setPageComplete(false);
		setControl(container);
	}

	private void onTextChanged() {

		final String urlText = mUrlText.getText();

		if (urlText.length() == 0) {
			showError("URL must be specified");
			return;
		}

		if (!URL_VALIDATOR.isValid(urlText)) {
			showError("URL must be valid");
			return;
		}
		
		setErrorMessage(null);
		setPageComplete(true);
	}

	private void showError(String message) {
		setErrorMessage(message);
		setPageComplete(false);
	}
	
	public String getUrl() {
		return mUrlText.getText();
	}
	
	public boolean getIsPojo() {
		return mRadioPojo.getSelection();
	}
}