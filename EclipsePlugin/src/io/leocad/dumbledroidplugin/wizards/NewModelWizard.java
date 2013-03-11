package io.leocad.dumbledroidplugin.wizards;

import io.leocad.dumbledroidplugin.exceptions.InvalidUrlException;
import io.leocad.dumbledroidplugin.exceptions.UnsupportedContentTypeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewModelWizard extends Wizard implements INewWizard {

	private ISelection mSelection;

	public NewModelWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mSelection = selection;
	}

	@Override
	public void addPages() {
		UrlInputPage page = new UrlInputPage(mSelection);
		addPage(page);
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		
		UrlInputPage page = (UrlInputPage) getPage(UrlInputPage.PAGE_NAME);
		final String url = page.getUrl();
		final boolean isPojo = page.getIsPojo();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				
				try {
					doFinish(url, isPojo, monitor);
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

	private void doFinish(String urlAddress, boolean isPojo, IProgressMonitor monitor) throws CoreException, UnsupportedContentTypeException, InvalidUrlException {
		
		try {
		monitor.beginTask("Validating URL…", 4);
		
		URL url = null;
		try {
			url = new URL(urlAddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//Will never happen. The URL was already validated on UrlInputPage
		}
		
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)  url.openConnection();
			connection.connect();
		} catch (IOException e) {
			//e.printStackTrace();
			throw new InvalidUrlException();
		}
		
		boolean isJson = isJson(connection);
		monitor.worked(1);
		
		monitor.setTaskName("Fetching URL contents…");
		
		if (isJson) {
			
		} else {
			
		}
		
		
		monitor.worked(1);
		monitor.setTaskName("Writing to file…");
		Thread.sleep(2000);
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		IResource resource = root.findMember(new Path(containerName));
//		if (!resource.exists() || !(resource instanceof IContainer)) {
//			throwCoreException("Container \"" + containerName
//					+ "\" does not exist.");
//		}
//		IContainer container = (IContainer) resource;
//		final IFile file = container.getFile(new Path(fileName));
//		try {
//			InputStream stream = openContentStream();
//			if (file.exists()) {
//				file.setContents(stream, true, true, monitor);
//			} else {
//				file.create(stream, true, monitor);
//			}
//			stream.close();
//		} catch (IOException e) {
//		}
		
//		getShell().getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				try {
//					IDE.openEditor(page, file, true);
//				} catch (PartInitException e) {
//				}
//			}
//		});
		monitor.worked(1);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

	private boolean isJson(HttpURLConnection connection) throws UnsupportedContentTypeException {
		
		String contentType = connection.getContentType();
		
		if (contentType.contains("json")) {
			return true;
		} else if (contentType.contains("xml")) {
			return false;
		}
		
		throw new UnsupportedContentTypeException(contentType);
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