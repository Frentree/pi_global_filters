package com.iopts.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonQuestion {

	public static void main(String[] args) {
		/*
		 * String input = "{\"Verbs\":[{\n" +
		 * "    \"aaaa\":\"30d\", \"type\":\"ed\", \"rel\":1.0, \"id\":\"80\", \"spoken\":\"en\", \"ct\":\"on\", \"sps\":null\n"
		 * + "},{\n" +
		 * "    \"aaaa\":\"31\", \"type\":\"cc\", \"rel\":3.0, \"id\":\"10\", \"spoken\":\"en\", \"ct\":\"off\", \"sps\":null\n"
		 * + "},{\n" +
		 * "    \"aaaa\":\"81\", \"type\":\"nn\", \"rel\":3.0, \"id\":\"60\", \"spoken\":\"en\", \"ct\":\"on\", \"sps\":null\n"
		 * + "}]}";
		 */

		String JSONString;
		try {
			JSONString = readFile("d:\\t.json");

			JSONArray jsonarray = new JSONArray(JSONString);

			
			for (int i = 0; i < jsonarray.length(); i++) {
				
				JSONObject obj = jsonarray.getJSONObject(i);
				
				String name = obj.getString("owner");
				String url = obj.getString("path");
				
//				if(obj !=null) {
//					System.out.print("     -->"+obj.get("subpaths"));
//				}
			}

			/*
			 * ObjectMapper objectMapper = new ObjectMapper();
			 * 
			 * JsonNode node = objectMapper.readTree(JSONString); processNode(node);
			 */

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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

	private static void processNode(JsonNode n) {
		if (n.isContainerNode()) {
			processJsonContainer(n.iterator());
		} else if (n.isNull() || n.isNumber() || n.isTextual()) {
			// System.out.println(" Node :"+n.toString());
		}
	}

	private static void processJsonContainer(Iterator<JsonNode> iterator) {
		while (iterator.hasNext()) {
			processNode(iterator.next());
		}
	}
}