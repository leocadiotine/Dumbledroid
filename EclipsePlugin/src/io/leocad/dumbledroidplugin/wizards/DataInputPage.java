package io.leocad.dumbledroidplugin.wizards;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DataInputPage extends WizardPage {

	public static final String PAGE_NAME = "DataInputPage";
	private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"});

	private Text mUrlText;
	private Text mClassNameText;
	private Button mRadioPojo;

	public DataInputPage(ISelection selection) {
		super(PAGE_NAME);
		setTitle("Dumbledroid Model Creator");
		setDescription("This wizard creates a new Java class from a JSON/XML to use with the Dumbledroid framework.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		FormLayout layout = new FormLayout();
		container.setLayout(layout);
		layout.marginHeight = 15;
		layout.marginWidth = 15;
		
		Label urlLabel = new Label(container, SWT.NULL);
		urlLabel.setText("&URL:");

		mUrlText = new Text(container, SWT.BORDER | SWT.SINGLE);
		FormData urlFormData = new FormData();
		urlFormData.left = new FormAttachment(urlLabel, 10);
		urlFormData.right = new FormAttachment(100);
		mUrlText.setLayoutData(urlFormData);
		
		mUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				onTextChanged();
			}
		});
		
		mRadioPojo = new Button(container, SWT.RADIO);
		mRadioPojo.setText("Use POJO pattern (public fields and no getters/setters)");
		FormData radioPojoFormData = new FormData();
		radioPojoFormData.top = new FormAttachment(mUrlText, 20);
		mRadioPojo.setLayoutData(radioPojoFormData);
		mRadioPojo.setSelection(true);
		
		Button radioGetSet = new Button(container, SWT.RADIO);
		radioGetSet.setText("Use Accessor pattern (private fields with getters and setters)");
		FormData radioGetSetFormData = new FormData();
		radioGetSetFormData.top = new FormAttachment(mRadioPojo, 10);
		radioGetSet.setLayoutData(radioGetSetFormData);
		radioGetSet.setSelection(false);
		
		Label labelClassName = new Label(container, SWT.NULL);
		labelClassName.setText("&Class name (optional):");
		FormData labelClassNameFormData = new FormData();
		labelClassNameFormData.top = new FormAttachment(radioGetSet, 20);
		labelClassName.setLayoutData(labelClassNameFormData);

		mClassNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		FormData classNameTextFormData = new FormData();
		classNameTextFormData.left = new FormAttachment(labelClassName, 10);
		classNameTextFormData.right = new FormAttachment(100);
		classNameTextFormData.top = new FormAttachment(radioGetSet, 20);
		mClassNameText.setLayoutData(classNameTextFormData);

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
	
	public String getClassName() {
		return mClassNameText.getText();
	}
}