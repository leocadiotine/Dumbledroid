package io.leocad.dumbledroidplugin.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class FileCreationPage extends WizardNewFileCreationPage {
	
	public static final String PAGE_NAME = "FileCreationPage";

	public FileCreationPage(IStructuredSelection selection) {
		super(PAGE_NAME, selection);
		setTitle("Dumbledroid Model Creator");
		setDescription("This wizard creates a new Java class from a JSON/XML to use with the Dumbledroid framework.");
	}

	@Override
	public String getFileExtension() {
		return "java";
	}
}
