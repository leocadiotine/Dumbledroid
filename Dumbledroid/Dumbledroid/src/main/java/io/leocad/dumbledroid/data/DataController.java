package io.leocad.dumbledroid.data;

import io.leocad.dumbledroid.data.cache.DiskCache;
import io.leocad.dumbledroid.data.cache.MemoryCache;
import io.leocad.dumbledroid.data.cache.ModelHolder;
import io.leocad.dumbledroid.data.cache.ObjectCopier;
import io.leocad.dumbledroid.data.xml.Node;
import io.leocad.dumbledroid.data.xml.SaxParser;
import io.leocad.dumbledroid.net.HttpLoader;
import io.leocad.dumbledroid.net.HttpMethod;
import io.leocad.dumbledroid.net.NoConnectionException;
import io.leocad.dumbledroid.net.TimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONException;
import org.xml.sax.SAXException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class DataController {

	public static void load(Context ctx, AbstractModel receiver, DataType dataType, List<NameValuePair> params, HttpMethod method) throws Exception {

		final String cacheKey = getKey(receiver, params);
		HttpResponse httpResponse = null;
		boolean checkedConnection = false;
		ModelHolder modelHolder = null;

		//Get cached version
		if (receiver.cacheDuration > 0) {

			//In memory
			modelHolder = MemoryCache.getInstance().getCached(cacheKey);
			if (modelHolder != null && !modelHolder.isExpired() && ObjectCopier.copy(modelHolder.model, receiver)) {
				return;
			}

			//In disk
			if(modelHolder == null) {
				modelHolder = DiskCache.getInstance(ctx).getCached(cacheKey);
				if (modelHolder != null && !modelHolder.isExpired() && ObjectCopier.copy(modelHolder.model, receiver)) {
					MemoryCache.getInstance().cache(cacheKey, modelHolder);
					return;
				}
			}

			// Check also if it was modified on the server before downloading it
			if(modelHolder != null) {
				try {
					checkConnection(ctx);
					checkedConnection = true;
					httpResponse = HttpLoader.getHttpResponse(receiver.url, receiver.encoding, params, method);
					final Header lastModHeader = httpResponse.getFirstHeader("Last-Modified");

					if (lastModHeader != null) {
						final String lastMod = lastModHeader.getValue();
						long lastModTimeMillis = Long.MAX_VALUE;
						try {
							lastModTimeMillis = DateUtils.parseDate(lastMod).getTime();
						} catch (DateParseException e) {}

						if (modelHolder != null && lastModTimeMillis <= modelHolder.timestamp && ObjectCopier.copy(modelHolder.model, receiver)) {
							//Discard the connection and return the cached version renewing the timestamp
							modelHolder.timestamp = System.currentTimeMillis();
							MemoryCache.getInstance().cache(cacheKey, modelHolder);
							DiskCache.getInstance(ctx).cache(cacheKey, modelHolder);
							return;
						}
					}

				//If there is some error on the connection, return the last cached version
				} catch (TimeoutException e) {
					if(modelHolder !=  null && ObjectCopier.copy(modelHolder.model, receiver)) {
						return;
					}
				} catch (NoConnectionException e) {
					if(modelHolder !=  null && ObjectCopier.copy(modelHolder.model, receiver)) {
						return;
					}
				} catch (IOException e) {
					if(modelHolder !=  null && ObjectCopier.copy(modelHolder.model, receiver)) {
						return;
					}
				}
			}
		}

		if (!checkedConnection) {
			checkConnection(ctx);
		}

		if (httpResponse == null) {
			httpResponse = HttpLoader.getHttpResponse(receiver.url, receiver.encoding, params, method);
		}
		final InputStream is = HttpLoader.getHttpContent(httpResponse);

		switch (dataType) {
		case JSON:
			processJson(receiver, is);
			break;

		case XML:
			processXml(receiver, is);
			break;
		}

		//Cache
		if (receiver.cacheDuration > 0) {
			modelHolder = new ModelHolder(receiver, System.currentTimeMillis());
			MemoryCache.getInstance().cache(cacheKey, modelHolder);
			DiskCache.getInstance(ctx).cache(cacheKey, modelHolder);
		}
	}

	private static void checkConnection(Context ctx) throws NoConnectionException {

		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			throw new NoConnectionException();
		}
	}

	private static void processJson(AbstractModel receiver, InputStream is)
			throws IOException, JSONException, InstantiationException {

		String content = HttpLoader.streamToString(is);
		JsonReflector.reflectJsonString(receiver, content);
	}

	private static void processXml(AbstractModel receiver, InputStream is) throws SAXException, IOException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InstantiationException {

		Node parsedNode = SaxParser.parse(is, receiver.encoding);
		XmlReflector.reflectXmlRootNode(receiver, parsedNode);
	}

	private static String getKey(AbstractModel model, List<NameValuePair> params) {

		StringBuffer sb = new StringBuffer(model.url);

		if (params != null) {
			for (NameValuePair nameValuePair : params) {
				sb.append(nameValuePair.getName())
				.append('-')
				.append(nameValuePair.getValue());
			}
		}

		return sb.toString();
	}
}