package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controls implements KeyListener {
	
	public static boolean W_DOWN = false,
			A_DOWN = false,
			S_DOWN = false,
			D_DOWN = false,
			X_DOWN = false,
			Y_DOWN = false,
			Q_DOWN = false,
			E_DOWN = false,
			F_DOWN = false,
			TAB_DOWN = false;

	public Controls() { }

	@Override
	public void keyPressed(KeyEvent e) {
		update(e, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		update(e, false);
	}
	
	public void update(KeyEvent e, boolean state) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_W:
				W_DOWN = state;
				break;
			case KeyEvent.VK_A:
				A_DOWN = state;
				break;
			case KeyEvent.VK_S:
				S_DOWN = state;
				break;
			case KeyEvent.VK_D:
				D_DOWN = state;
				break;
			case KeyEvent.VK_X:
				X_DOWN = state;
				break;
			case KeyEvent.VK_Y:
				Y_DOWN = state;
				break;
			case KeyEvent.VK_Q:
				Q_DOWN = state;
				break;
			case KeyEvent.VK_E:
				E_DOWN = state;
				break;
			case KeyEvent.VK_F:
				F_DOWN = state;
				break;
			case KeyEvent.VK_TAB:
				TAB_DOWN = state;
				break;
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }

}
