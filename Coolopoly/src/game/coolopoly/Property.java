package game.coolopoly;

public class Property {
	
	public final String title;
	public final int price;
	public final int standardTaxes;
	/**
	 * The amount of houses built on this property.
	 * 5 houses are equals to one hotel.
	 */
	public int houses;

	public Property(String title, int price, int standardTaxes) {
		this.title = title;
		this.price = price;
		this.standardTaxes = standardTaxes;
		this.houses = 0;
	}
	
	public int getTaxes() {
		return (int) (standardTaxes * (1 + houses * (houses < 5 ? 0.75 : 1)));
	}

}
