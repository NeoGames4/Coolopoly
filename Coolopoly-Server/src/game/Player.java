package game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class Player {
	
	public final String name;
	public final String remoteAddress;
	private int money;
	private int position;
	
	public HashMap<Property, Integer> properties;

	public Player(String name, String remoteAddress) {
		this.name = name;
		this.remoteAddress = remoteAddress;
		this.money = Constants.STARTING_MONEY;
		this.position = 0;
		this.properties = new HashMap<>();
	}
	
	public void setMoney(int amount) {
		money = amount;
	}
	
	public void changeMoney(int amount) throws GameIllegalMoveException {
		if(money + amount < 0)
			throw new GameIllegalMoveException("This action would result in negative money.");
			
		money += amount;
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
				.put("money", money)
				.put("position", position)
				.put("properties", p);
	}

}
