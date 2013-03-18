package io.leocad.dumbledroidplugin.wizards;

import java.util.regex.Pattern;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DataInputPage extends WizardPage {

	public static final String PAGE_NAME = "DataInputPage";
	private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"});
	private static final Pattern PATTERN_EXCLUDE_NUMBERS = Pattern.compile(".*[^0-9].*");

	private Text mUrlText;
	private Button mRadioPojo;
	private Text mCacheText;
	private Combo mCacheCombo;

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
				onUrlChanged();
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
		
		Label cacheLabel = new Label(container, SWT.NULL);
		cacheLabel.setText("&Cache duration (optional):");
		FormData cacheLabelFormData = new FormData();
		cacheLabelFormData.top = new FormAttachment(radioGetSet, 20);
		cacheLabel.setLayoutData(cacheLabelFormData);
		
		mCacheText = new Text(container, SWT.BORDER | SWT.SINGLE);
		FormData cacheTextFormData = new FormData();
		cacheTextFormData.left = new FormAttachment(cacheLabel, 10);
		cacheTextFormData.top = new FormAttachment(radioGetSet, 20);
		cacheTextFormData.width = 50;
		mCacheText.setLayoutData(cacheTextFormData);
		
		mCacheText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				onCacheTextChanged();
			}
		});

		mCacheCombo = new Combo (container, SWT.READ_ONLY);
		mCacheCombo.setItems (new String [] {"days", "hours", "minutes", "seconds"});
		FormData cacheComboFormData = new FormData();
		cacheComboFormData.left = new FormAttachment(mCacheText, 5);
		cacheComboFormData.top = new FormAttachment(radioGetSet, 18);
		mCacheCombo.setLayoutData(cacheComboFormData);
		mCacheCombo.select(1);
		
		setPageComplete(false);
		setControl(container);
	}

	private void onUrlChanged() {

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
	
	private void onCacheTextChanged() {
		
		final String cacheText = mCacheText.getText().trim();
		
		if (cacheText.length() > 0 && PATTERN_EXCLUDE_NUMBERS.matcher(cacheText).matches()) {
			showError("Cache duration must be a number.");
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
	
	public long getCacheDuration() {
		
		final String cacheText = mCacheText.getText().trim();
		if (cacheText.equals("")) {
			return 0L;
		}
		
		long cacheDurationFactor = 1L;
		
		switch (mCacheCombo.getSelectionIndex()) {
		
		case 0: //days
			cacheDurationFactor *= 24L;
		case 1: //hours
			cacheDurationFactor *= 60L;
		case 2: //minutes
			cacheDurationFactor *= 60L;
		case 3: //seconds
		default:
			cacheDurationFactor *= 1000L;
		}
		
		return Long.valueOf(cacheText) * cacheDurationFactor;
	}
}