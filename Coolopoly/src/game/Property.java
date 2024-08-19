package game;

public class Property {
	
	public final String title;
	
	public final int price;
	public final int pricePerHouse;
	
	public final int rent[] = new int[6];
	
	public final int mortage;

	public Property(String title, int price, int pricePerHouse, int rent0, int rent1, int rent2, int rent3, int rent4, int rentHotel, int mortage) {
		this.title = title;
		
		this.price = price;
		this.pricePerHouse = pricePerHouse;
		
		rent[0] = rent0;
		rent[1] = rent1;
		rent[2] = rent2;
		rent[3] = rent3;
		rent[4] = rent4;
		rent[5] = rentHotel;
		
		this.mortage = mortage;
	}
	
	public boolean canBeBought() {
		return price > 0;
	}
	
	public int getTaxes(int houses) {
		return rent[houses];
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Property) {
			return ((Property) object).title.equals(title);
		} return super.equals(object);
	}

}
