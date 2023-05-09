package com.iopts.test;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App {
	public static void main(String[] args) {
		String str = "{\"a\":\"1\", \"b\":\"2\", \"c\":[{\"d\":\"4\"},{\"e\":\"5\"},{\"f\":[{\"g\":\"6\"},{\"h\":\"7\"}]}], \"i\":8}";
		try {
			loopThroughJson(new JSONObject(str));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void loopThroughJson(Object input) throws JSONException {
		if (input instanceof JSONObject) {
			Iterator<?> keys = ((JSONObject) input).keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (!(((JSONObject) input).get(key) instanceof JSONArray))
					System.out.println(key + "=" + ((JSONObject) input).get(key));
				else
					loopThroughJson(new JSONArray(((JSONObject) input).get(key).toString()));
			}
		}
		if (input instanceof JSONArray) {
			for (int i = 0; i < ((JSONArray) input).length(); i++) {
				JSONObject a = ((JSONArray) input).getJSONObject(i);
				Object key = a.keys().next().toString();
				if (!(a.opt(key.toString()) instanceof JSONArray))
					System.out.println(key + "=" + a.opt(key.toString()));
				else
					loopThroughJson(a.opt(key.toString()));
			}
		}

	}
}
