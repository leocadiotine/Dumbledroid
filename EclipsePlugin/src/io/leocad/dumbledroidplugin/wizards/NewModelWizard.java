package io.leocad.dumbledroidplugin.wizards;

import io.leocad.dumbledroidplugin.core.DumbledroidClassCreator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewModelWizard extends Wizard implements INewWizard {

	private IStructuredSelection mSelection;

	public NewModelWizard() {
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mSelection = selection;
	}

	@Override
	public void addPages() {
		addPage(new DataInputPage(mSelection));
		addPage(new FileCreationPage(mSelection));
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		
		DataInputPage dataPage = (DataInputPage) getPage(DataInputPage.PAGE_NAME);
		final String url = dataPage.getUrl();
		final boolean isPojo = dataPage.getIsPojo();
		final long cacheDuration = dataPage.getCacheDuration();
		
		FileCreationPage filePage = (FileCreationPage) getPage(FileCreationPage.PAGE_NAME);
		final IFile newFile = filePage.createNewFile();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				
				try {
					DumbledroidClassCreator.create(url, isPojo, cacheDuration, newFile, monitor);
					
					monitor.worked(1);
					monitor.setTaskName("Opening file…");

					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							try {
								IDE.openEditor(page, newFile, true);
							} catch (PartInitException e) {
							}
						}
					});
					
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		
		try {
			getContainer().run(true, false, op);
			
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		
		return true;
	}

	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents = "This is the initial file contents for *.mpe file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "io.leocad.dumbledroidplugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}

}