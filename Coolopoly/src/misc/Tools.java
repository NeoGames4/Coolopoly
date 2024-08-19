package misc;

import java.awt.Color;

public class Tools {

	public static float absMod(float a, float mod) {
	    return a >= 0
	            ? a % mod
	            : (mod+a) % mod;
	}
	
	public static Color rgbWithOpaque(Color c, int alpha) {
		if(alpha > 255)
			alpha = 255;
		else if(alpha < 0)
			alpha = 0;
		
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	public static String getLastWord(String str) {
		String[] words = str.split(" ");
		
		return words[words.length-1];
	}
	
	public static Color colorLerp(Color a, Color b, float t) {
		return new Color(
			intLerp(a.getRed(),   b.getRed(),   t),
			intLerp(a.getGreen(), b.getGreen(), t),
			intLerp(a.getBlue(),  b.getBlue(),  t),
			intLerp(a.getAlpha(), b.getAlpha(), t)
		);
	}
	
	private static int intLerp(int from, int to, float t) {
		return from + (int) (t * (to-from));
	}
	
	/*public static int getFontPixelHeight(Graphics2D g2, String str) {
		// JLabel label = new JLabel(str);
		// label.setFont(g2.getFont());
		// return label.getPreferredSize().height;
		// return (int) g2.getFontMetrics().getStringBounds(str, g2).getHeight();
		return g2.getFontMetrics().getAcent();
	}*/

}
