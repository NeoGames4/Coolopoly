package game.uielements;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class OptionPane {
	
	public static final Color	BACKGROUND_COLOR		= new Color(50, 50, 75),
								BACKGROUND_ACCENT_COLOR	= new Color(60, 85, 125),
								FOREGROUND_COLOR		= Color.WHITE;
	
	static {
		UIManager.put("OptionPane.background",			BACKGROUND_COLOR);
		UIManager.put("Panel.background",				BACKGROUND_COLOR);
		
		UIManager.put("OptionPane.messageForeground",	FOREGROUND_COLOR);
		
		UIManager.put("OptionPane.textFieldBackground",	BACKGROUND_ACCENT_COLOR);
		UIManager.put("OptionPane.textFieldForeground",	FOREGROUND_COLOR);
	}
	
	private static JTextField createTextField() {
		JTextField textField = new JTextField();
		textField.setBackground(BACKGROUND_ACCENT_COLOR);
		textField.setForeground(FOREGROUND_COLOR);
		textField.setBorder(new LineBorder(FOREGROUND_COLOR, 1));
		textField.setCaretColor(FOREGROUND_COLOR);
		textField.setSelectedTextColor(Color.black);
		return textField;
	}
	
	private static JCheckBox createCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setBackground(BACKGROUND_ACCENT_COLOR);
		checkBox.setForeground(FOREGROUND_COLOR);
		checkBox.setBorder(new LineBorder(FOREGROUND_COLOR, 1));
		return checkBox;
	}

	public static OptionPaneResponse sendLoginPanel(String initName, String initAddress) {
		JTextField name		= createTextField();
		name.setText(initName);
		JTextField address	= createTextField();
		address.setText(initAddress);
		JCheckBox watching	= createCheckBox();
		
		Object[] panel = {
				"Welcome to Coolopoly!",
				"Unique username:", name,
				"Server address:", address,
				"Just watching:", watching
		};
		
		int result = JOptionPane.showConfirmDialog(null, panel, "Coolopoly Client", JOptionPane.OK_CANCEL_OPTION);
		
		OptionPaneResponse response = new OptionPaneResponse(result);
		response.fields.put("username",	name.getText());
		response.fields.put("address",	address.getText());
		response.fields.put("just_watching", watching.isSelected() ? "yes" : "no");
		
		return response;
	}
	
	public static void sendExceptionPanel(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

}
