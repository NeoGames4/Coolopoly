package game;

import org.json.JSONObject;

public class DiceState {
	
	public final int diceA, diceB;

	public DiceState(int diceA, int diceB) {
		this.diceA = diceA;
		this.diceB = diceB;
	}
	
	public int sum() {
		return diceA + diceB;
	}
	
	public JSONObject toJSON() {
		return new JSONObject()
				.put("diceA", diceA)
				.put("diceB", diceB);
	}
	
	public static DiceState fromJSON(JSONObject o) {
		if(o == null)
			return null;
		return new DiceState(o.getInt("diceA"), o.getInt("diceB"));
	}

}
