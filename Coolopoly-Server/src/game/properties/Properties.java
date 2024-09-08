package game.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import launcher.Console;

public class Properties {
	
	public static final Property[] properties;
	public static final JSONObject propertiesAsJSON;
	public static final int[] railwayRents;
	static {
		InputStream in = Properties.class.getResourceAsStream("properties.json");
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		
		String content = "";
		JSONObject o = null;
		
		try {
			for(String line; (line = r.readLine()) != null; content += line + "\n");
			o = new JSONObject(content);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch(JSONException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		propertiesAsJSON = o;
		
		properties = new Property[o.getInt("fields")];
		
		JSONObject oRailwayRents = o.getJSONObject("railway_rents");
		ArrayList<Integer> railwayRentsArray = new ArrayList<>();
		
		int ri = 1;
		while(oRailwayRents.has(ri + ""))
			railwayRentsArray.add(oRailwayRents.getInt((ri++) + ""));
		
		railwayRents = new int[railwayRentsArray.size()];
		for(int i = 0; i<railwayRents.length; i++)
			railwayRents[i] = railwayRentsArray.get(i);
		
		for(int i = 0; i<properties.length; i++)
			properties[i] = Property.fromJSON(o.getJSONObject(i + ""), i);
		
		Console.log("Loaded properties.json.");
	}
	
	public static Property getProperty(String title) {
		for(Property p : properties) {
			if(title.equals(p.title))
				return p;
		} return null;
	}

}
