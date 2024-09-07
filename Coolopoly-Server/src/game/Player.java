package game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import game.properties.Properties;
import game.properties.Property;

public class Player {
	
	public final String name;
	public final String remoteAddress;
	public final String image;
	private int money;
	private int position;
	
	public HashMap<Property, Integer> properties;

	public Player(String name, String remoteAddress, String image) {
		this.name = name;
		this.remoteAddress = remoteAddress;
		this.image = image;
		
		this.money = Constants.STARTING_MONEY;
		this.position = 0;
		this.properties = new HashMap<>();
	}
	
	public void setMoney(int amount) {
		money = amount;
	}
	
	public void changeMoney(int amount, GameState state) {
		money += amount;
		
		if(money < 0 && money - amount >= 0) {
			state.newNotifications.add(new Notification("507 Insufficient Storage! " + name + " does not have any free memory left!", 5000));
			state.newNotifications.add(new Notification(name + "’" + (name.endsWith("s") ? "" : "") + " fields are up to sale!", 4900));
			state.removePlayer(name);
		}
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void changePosition(int fields) {
		int total = Properties.properties.length;
		
		position += fields;
		
		while(position < 0)
			position += total;
		
		position %= total;
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
		return p != null && name.equals(p.name);
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
				.put("remote_address", remoteAddress)
				.put("image", image)
				.put("money", money)
				.put("position", position)
				.put("properties", p);
	}

}
