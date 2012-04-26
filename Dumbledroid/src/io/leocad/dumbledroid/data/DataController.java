package io.leocad.dumbledroid.data;

import io.leocad.dumbledroid.net.HttpLoader;
import io.leocad.dumbledroid.net.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import android.content.Context;


public class DataController {

	public static void load(Context ctx, AbstractModel receiver, DataType dataType, List<NameValuePair> params, HttpMethod method)
			throws Exception {

		//Expand the URL
		if (receiver.id != null) {
			receiver.url = receiver.url.replace("{id}", receiver.id);
		}

		InputStream is = HttpLoader.makeRequest(receiver.url, receiver.encoding, params, method);

		switch (dataType) {
		case JSON:
			processJson(receiver, is);
			break;
		}
	}

	private static void processJson(AbstractModel receiver, InputStream is)
			throws IOException, JSONException, InstantiationException {

		String content = HttpLoader.streamToString(is);
		ModelReflector.reflectJson(receiver, content);
	}
}
