package game;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import game.properties.Properties;
import game.properties.Property;
import misc.design.Design;

public class Player {
	
	public final String name;
	public final BufferedImage image;
	public final String remoteAdress;
	
	private int	money,
				position,
				turnsInPrison;
	
	public Transition	xTransition,
						yTransition,
						aTransition;
	
	public HashMap<Property, Integer> properties;

	public Player(String name, String remoteAdress, String image, int money, int position, int turnsInPrison, HashMap<Property, Integer> properties) {
		this.name = name;
		this.remoteAdress = remoteAdress;
		this.image = Design.getImage(image);
		this.money = money;
		this.position = position;
		this.turnsInPrison = turnsInPrison;
		this.properties = properties;
		
		xTransition = new Transition(0, 0, 10);
		yTransition = new Transition(0, 0, 10);
		aTransition = new Transition(0, 0, 10);
	}
	
	public int getMoney() {
		return money;
	}
	
	public int getPosition() {
		return position;
	}
	
	public int getTotalWorth() {
		int t = money;
		
		Iterator<Entry<Property, Integer>> i = properties.entrySet().iterator();
		while(i.hasNext()) {
			Entry<Property, Integer> e = i.next();
			
			if(e.getKey().price > 0)
				t += e.getKey().price;
			
			if(e.getKey().pricePerHouse > 0)
				t += e.getValue() * e.getKey().pricePerHouse;
		}
		
		return t;
	}
	
	public boolean nameEquals(Player p) {
		if(p == null)
			return false;
		return name.equals(p.name);
	}
	
	public static Player fromJSON(JSONObject o) {
		HashMap<Property, Integer> properties = new HashMap<>();
		JSONArray a = o.getJSONArray("properties");
		for(int i = 0; i<a.length(); i++) {
			properties.put(
				Properties.getProperty(a.getJSONObject(i).getString("title")),
				a.getJSONObject(i).getInt("houses")
			);
		}
		
		return new Player(
			o.getString("name"),
			o.getString("remote_address"),
			o.getString("image"),
			o.getInt("money"),
			o.getInt("position"),
			o.getInt("turns_in_prison"),
			properties
		);
	}

}
