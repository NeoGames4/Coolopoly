package game;

public class Properties {
	
	public static final Property[] properties;
	static {
		properties = new Property[40];
	}
	
	public static Property getProperty(String title) {
		for(Property p : properties) {
			if(title.equals(p.title))
				return p;
		} return null;
	}

}
