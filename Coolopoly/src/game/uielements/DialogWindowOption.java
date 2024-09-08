package game.uielements;

import java.util.ArrayList;

public class DialogWindowOption {
	
	public final String title;

	public DialogWindowOption(String title) {
		this.title = title;
	}
	
	public void onSelect() {}
	
	public static DialogWindowOption getDiscardOption(String label, ArrayList<DialogWindow> dialogs) {
		return new DialogWindowOption(label) {
			@Override
			public void onSelect() {
				if(!dialogs.isEmpty())
					dialogs.get(0).pop();
			}
		};
	}

}
