package io.leocad.dumbledroid.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpLoader {

	public static InputStream makeRequest(String url, String encoding,
			List<NameValuePair> params, HttpMethod method)
					throws IOException {

		HttpUriRequest request = getHttpRequest(url, encoding, params, method);

		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			if (null != entity) {
				return entity.getContent();
			}

			return null;


		} catch (ClientProtocolException e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();
			throw e;
		}
	}

	private static HttpUriRequest getHttpRequest(String url, String encoding,
			List<NameValuePair> params, HttpMethod method)
					throws UnsupportedEncodingException {

		HttpUriRequest request;
		
		if (null == method) {
			method = HttpMethod.GET;
		}

		switch (method) {

		case POST:

			HttpPost req = new HttpPost(url);

			if (!params.isEmpty()) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, encoding);
				req.setEntity(entity);
			}

			request = req;
			break;

		case GET:
		default:

			StringBuilder sb = new StringBuilder(url);

			if (null != params && !params.isEmpty()) {
				sb.append("?");

				for (NameValuePair nameValuePair : params) {

					sb.append(nameValuePair.getName())
					.append('=')
					.append( URLEncoder.encode(nameValuePair.getValue(), encoding) )
					.append('&');
				}
			}

			request = new HttpGet(sb.toString());
			break;
		}

		return request;
	}

	public static String streamToString(InputStream is) throws IOException {

		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}

		return total.toString();
	}
}
