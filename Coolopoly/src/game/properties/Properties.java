package game.properties;

import java.util.ArrayList;

import org.json.JSONObject;

import misc.Console;

public class Properties {
	
	private static Property[] properties = new Property[0];
	
	private static int[] railwayRents = new int[0];
	
	public static void loadProperties(JSONObject o) {
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
	
	public static Property[] getProperties() {
		return properties;
	}
	
	public static int[] getRailwayRents() {
		return railwayRents;
	}
	
	public static Property getProperty(String title) {
		for(Property p : properties) {
			if(title.equals(p.title))
				return p;
		} return null;
	}

}
