package com.iopts.test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class test2 {

	private static JsonParser jp=null;

	public static void main(String[] args) {
		
		JsonParser parser;
		try {
			parser = new JsonFactory().createJsonParser(new File("d:\\t.json"));
			while (parser.nextToken() != JsonToken.NOT_AVAILABLE) {
			    if ("id".equals(parser.getCurrentName())) {
			        parser.nextToken();
			        String value = parser.getText();
			        if (value.equals("b3d888b1-c4f0-4337-87a3-d51961d81c0b")) {
			            System.out.println("id is found: " + value);
			        }
			    }
			}
			parser.close();
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public static void getIds(List<String> ids, JsonParser jp) throws IOException, ParseException {
		
		JSONObject json = new JSONObject();
		JsonToken curr = jp.nextToken();

		if (curr != JsonToken.START_OBJECT) {
			System.out.println("Error: root should be object: quiting.");
			System.exit(1);
		}

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fName = jp.getCurrentName();
			curr = jp.nextToken(); // get field value

			if (fName.equals("children")) {
				if (curr == JsonToken.START_ARRAY) { // deal with array
					int i = 0;
					while (jp.nextToken() != JsonToken.END_ARRAY) {
						JsonNode jn = jp.readValueAsTree();
						System.out.println(jn.get("id").asText());
						Iterator it = ids.iterator();
						while (it.hasNext()) {
							String id = (String) it.next();
							System.out.println("arr id: " + id + ", json id: " + jn.get("id").asText());
							if (jn.get("id").asText().equals(id)) {
								// found id - draw cutted tree
								System.out.println("found: " + jn.get("id").asText());
								it.remove();
							}
						}
						if (jn.has("children")) {
							// jp.readValue11AsTree();
							System.out.println("found child");
							// start recursion
							jp.nextToken();
							getIds(ids, jp);
						}
					}
				}
			}
		}
		// return json;
	}

	// this method never calls - just tried an alternative
	public void findById(JsonNode rootNode) {
		Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			// System.out.println(field.getKey() + " = " + field.getValue());
			if (field.getKey().equals("id") && field.getValue().asText().equals("b3d888b1-c4f0-4337-87a3-d51961d81c0b")) {
				System.out.println("id is found: " + field.getValue().asText());
			} else {
				System.out.println("goin to recursion: " + field.getValue().asText());
				findById(field.getValue());
			}
		}
	}

}
