package misc;

public class Tools {

	public static float absMod(float a, float mod) {
	    return a >= 0
	            ? a % mod
	            : (mod+a) % mod;
	}

}
