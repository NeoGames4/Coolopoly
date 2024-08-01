package game.coolopoly;

import java.util.ArrayList;

import misc.Constants;

public class Player {
	
	public final String name;
	public int money;
	public int position;
	
	public ArrayList<Property> properties;

	public Player(String name) {
		this.name = name;
		this.money = Constants.STARTING_MONEY;
		this.position = 0;
		this.properties = new ArrayList<>();
	}

}
