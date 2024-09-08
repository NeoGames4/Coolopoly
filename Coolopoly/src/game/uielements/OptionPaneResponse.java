package game.uielements;

import java.util.HashMap;

public class OptionPaneResponse {
	
	public final int result;
	
	public final HashMap<String, String> fields = new HashMap<>();

	public OptionPaneResponse(int result) {
		this.result = result;
	}

}
