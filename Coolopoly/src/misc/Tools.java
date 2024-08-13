package misc;

import java.awt.Color;
import java.awt.Graphics;

public class Tools {

	public static float absMod(float a, float mod) {
	    return a >= 0
	            ? a % mod
	            : (mod+a) % mod;
	}
	
	public static Color rgbWithOpaque(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	public static int getFontPixelHeight(Graphics g, String str) {
		return (int) Math.round(g.getFontMetrics().getStringBounds(str, g).getHeight());
	}

}
