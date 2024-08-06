package game;

public class GameIllegalMoveException extends Exception {

	private static final long serialVersionUID = 1L;

	public GameIllegalMoveException(String message) {
		super(message);
	}

}
