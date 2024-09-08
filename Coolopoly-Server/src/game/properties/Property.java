package game.properties;

import java.awt.Color;

import org.json.JSONObject;

import game.properties.Property;

public class Property {
	
	public final String	title;
	public final Color	color;
	public final String	street;
	
	public final int position;
	
	public final int price;
	public final int pricePerHouse;
	
	public final int rent[] = new int[6];
	
	public final int mortgagePrice;
	
	public final PropertyEffect effect;

	public Property(String title, int position, Color color, String street, int price, int pricePerHouse, int rent0, int rent1, int rent2, int rent3, int rent4, int rentHotel, int mortgage, PropertyEffect effect) {
		this.title = title;
		this.position = position;
		this.color = color;
		this.street = street;
		
		this.price = price;
		this.pricePerHouse = pricePerHouse;
		
		rent[0] = rent0;
		rent[1] = rent1;
		rent[2] = rent2;
		rent[3] = rent3;
		rent[4] = rent4;
		rent[5] = rentHotel;
		
		this.mortgagePrice = mortgage;
		
		this.effect = effect;
	}
	
	public boolean canBeBought() {
		return price > 0;
	}
	
	public int getTaxes(int houses) {
		return houses < 0 ? 0 : rent[houses];
	}
	
	public int getUnmortgagePrice() {
		return (int) (mortgagePrice * 1.1);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Property) {
			return ((Property) object).title.equals(title);
		} return super.equals(object);
	}
	
	public static Property fromJSON(JSONObject o, int position) {
		JSONObject rent = o.optJSONObject("rent", new JSONObject());
		
		return new Property(
			o.getString("title"),
			position,
			Color.decode(o.getString("color")),
			o.optString("street", null),
			o.optInt("price", -1),
			o.optInt("house_price", -1),
			rent.optInt("0", -1),
			rent.optInt("1", -1),
			rent.optInt("2", -1),
			rent.optInt("3", -1),
			rent.optInt("4", -1),
			rent.optInt("0", -1),
			o.optInt("mortgage"),
			PropertyEffect.fromJSON(o.optJSONObject("effect", new JSONObject()))
		);
	}

}
