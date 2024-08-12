package game;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Player {
	
	public final String name;
	public final String remoteAdress;
	private int money;
	private int position;
	
	public HashMap<Property, Integer> properties;

	public Player(String name, String remoteAdress, int money, int position, HashMap<Property, Integer> properties) {
		this.name = name;
		this.remoteAdress = remoteAdress;
		this.money = money;
		this.position = position;
		this.properties = properties;
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
			o.getInt("money"),
			o.getInt("position"),
			properties
		);
	}

}
