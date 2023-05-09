package com.iopts.test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonStreaming {

	public static void main(String[] args) throws ParseException {

		String JSONString;
		try {
			JSONString = readFile("d:\\t.json");
			/*
			JSONParser parser = new JSONParser();
			JSONObject parsedJson = (JSONObject)parser.parse(JSONString);
*/
			parse(JSONString);
			 
			
			JsonStreaming jsontest = new JsonStreaming();
			Map<String, Object> jsonmap = jsontest.parseComplexJSON(JSONString);
			
			for (String key : jsonmap.keySet()) {
				if (jsonmap.get(key) instanceof ArrayList) {
					ListIterator litr = ((ArrayList) jsonmap.get(key)).listIterator();
					while (litr.hasNext()) {
						System.out.println(":: " + litr.next());
					}
				} else {
					System.out.println(key + " : " + jsonmap.get(key));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<String, Object> parseComplexJSON(String jsonstr) {
		Map<String, Object> respdata = new HashMap<String, Object>();

		JsonFactory jfac = new JsonFactory();
		try {
			JsonParser jParser = jfac.createParser(jsonstr);

			while (jParser.nextToken() != null) {
				if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
					respdata.put("result", readJSONArray(jParser));
				} else if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
					return readJSONObject(jParser);
				} else {
					respdata.put(jParser.getCurrentName(), jParser.getText());
				}
			}
			jParser.close();
		} catch (Exception ex) {
			System.out.println("Excpetion : " + ex);
			;
		}

		return respdata;

	}

	public Map<String, Object> readJSONObject(JsonParser jParser) {
		Map<String, Object> jsonobject = new HashMap<String, Object>();
		int jsoncounter = 1;
		if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
			try {
				while (jParser.nextToken() != JsonToken.END_OBJECT) {
					if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
						Map<String, Object> subjsonobj = readJSONObject(jParser);
						if (jParser.getCurrentName() != null && !jParser.getCurrentName().trim().isEmpty()) {
							jsonobject.put(jParser.getCurrentName(), subjsonobj);
						} else {
							jsonobject.put(jsoncounter + "", subjsonobj);
							jsoncounter++;
						}
					} else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
						List<Object> subjsonarray = readJSONArray(jParser);
						if (jParser.getCurrentName() != null && !jParser.getCurrentName().trim().isEmpty()) {
							jsonobject.put(jParser.getCurrentName(), subjsonarray);
						} else {
							jsonobject.put(jsoncounter + "", subjsonarray);
							jsoncounter++;
						}
					} else {
						jsonobject.put(jParser.getCurrentName(), jParser.getText());
					}
				}
			} catch (Exception ex) {
				System.out.println("Excpetion : " + ex);
				;
			}
		}
		return jsonobject;
	}

	public List<Object> readJSONArray(JsonParser jParser) {
		List<Object> jsonarray = new ArrayList<Object>();
		if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
			try {
				while (jParser.nextToken() != JsonToken.END_ARRAY) {
					if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
						Map<String, Object> subjsonobj = readJSONObject(jParser);
						jsonarray.add(subjsonobj);
					} else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
						List<Object> subjsonarray = readJSONArray(jParser);
						jsonarray.add(subjsonarray);
					} else {
						jsonarray.add(jParser.getText());
					}
				}
			} catch (Exception ex) {
				System.out.println("Excpetion : " + ex);
				;
			}
		}
		return jsonarray;
	}

	private static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	public static void parse(Object obj) {
		
		if (obj instanceof JSONObject) {
			JSONObject object = (JSONObject) obj;
			for (Object fieldName : object.keySet()) {
				System.out.println("Field: " + fieldName + " has value " + object.get((String) fieldName));
			}
		} else if (obj instanceof JSONArray) {
			JSONArray array = (JSONArray) obj;
			for (int i = 0; i < ((Map<String, Object>) array).size(); i++) {
				parse(array.get(i));
			}
		}
	}
}