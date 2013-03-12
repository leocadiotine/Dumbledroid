package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidUrlException;
import io.leocad.dumbledroidplugin.exceptions.UnsupportedContentTypeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;

public class DumbledroidClassCreator {

	public static void create(String urlAddress, boolean isPojo, IProgressMonitor monitor) throws UnsupportedContentTypeException, InvalidUrlException {

		try {
			monitor.beginTask("Validating URL…", 4);

			HttpURLConnection connection = validateAndOpenConnection(urlAddress);

			boolean isJson = isJson(connection);
			monitor.worked(1);

			monitor.setTaskName("Fetching URL contents…");

			if (isJson) {

				String jsonString = null;
				try {
					InputStream is = connection.getInputStream();
					jsonString = streamToString(is);

				} catch (IOException e) {
					//e.printStackTrace();
					throw new InvalidUrlException();
				}

				
				

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

	private static HttpURLConnection validateAndOpenConnection(String urlAddress) throws InvalidUrlException {
		
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
		
		return connection;
	}

	private static boolean isJson(HttpURLConnection connection) throws UnsupportedContentTypeException {

		String contentType = connection.getContentType();

		if (contentType.contains("json")) {
			return true;
		} else if (contentType.contains("xml")) {
			return false;
		}

		throw new UnsupportedContentTypeException(contentType);
	}

	private static String streamToString(InputStream is) throws IOException {

		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}

		return total.toString();
	}


}
