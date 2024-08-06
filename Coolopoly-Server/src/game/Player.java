package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class Player {
	
	public final String name;
	public int money;
	public int position;
	
	public HashMap<Property, Integer> properties;

	public Player(String name) {
		this.name = name;
		this.money = Constants.STARTING_MONEY;
		this.position = 0;
		this.properties = new HashMap<>();
	}
	
	public JSONObject toJSON() {
		JSONArray p = new JSONArray();
		
		Iterator<Entry<Property, Integer>> pIterator = properties.entrySet().iterator();
		while(pIterator.hasNext()) {
			Entry<Property, Integer> property = pIterator.next();
			p.put(new JSONObject()
					.put("title", property.getKey().title)
					.put("houses", property.getValue()));
		}
		
		return new JSONObject()
				.put("name", name)
				.put("money", money)
				.put("position", position)
				.put("properties", properties);
	}
	
	public static Player fromJSON(JSONObject o) {
		Player p = new Player(o.getString("name"));
		
		p.money = o.getInt("money");
		p.position = o.getInt("position");
		
		HashMap<Property, Integer> properties = new HashMap<>();
		JSONArray a = o.getJSONArray("properties");
		for(int i = 0; i<a.length(); i++) {
			properties.put(
				Properties.getProperty(a.getJSONObject(i).getString("title")),
				a.getJSONObject(i).getInt("houses")
			);
		}
		p.properties = properties;
		
		return p;
	}

}
