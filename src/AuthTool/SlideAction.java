package AuthTool;

public class SlideAction {
	public int ActionCode;
	
	public SlideAction(int code) {
		ActionCode = code;
	}
	
	public String toString() {
		if (ActionCode == 1) {
			return "KEY: Ctrl-F5";
		} else if (ActionCode == 2) {
			return "KEY: Right";
		} else if (ActionCode == 3) {
			return "KEY: Page Down";
		} else if (ActionCode == 4) {
			return "MOUSE: Left Button";
		}
		return "";

	}

}
