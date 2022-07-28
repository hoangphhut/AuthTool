package AuthTool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PreAction { 
	public static String APP = "#APP";
	public static String KEY = "#KEY";
	public static String MOUSE = "#MOUSE";	
	public static String PPT_OPEN = "Mở POWERPOINT";
	public static String PPT_CLOSE = "Đóng POWERPOINT";
	public static String F5 = "F5";
	public static String SHIFT_F5 = "Shift-F5";
	public static String ALT_F4 = "Alt-F4";
	public static String ENTER = "Enter";
	public static String LEFT = "Left";
	public static String RIGHT = "Right";
	public static String PGUP = "PgUp";
	public static String PGDN = "PgDn";
	public static String MOUSE_LEFT = "Bấm phím chuột trái";
	public static String MOUSE_RIGHT = "Bấm phím chuột phải";
	
	public String ActionGroup = "";
	public String Action = "";
	public String ActionPara = "";
	public int Delay = 0;
	
	public void copy(PreAction p) {
		ActionGroup = p.ActionGroup;
		Action = p.Action;
		ActionPara = p.ActionPara;
		Delay = p.Delay;
	}
	public String toString() {
		String s; 
		if (Action.startsWith(PPT_OPEN)) {
			s = ActionGroup + " " + Action + " " + ActionPara;
		} else {
			s = ActionGroup + " " + Action;
		}
		if (Delay > 0) s = s + " && Tạm dừng " + Delay + "(giây)";
		return s;
	}
	
	public String toJson() {
		String s = "";
		s = "{ \"ActionGroup\" : \"" + ActionGroup + "\", ";
		s = s + "\"Action\" : \"" + Action + "\", ";
		s = s + "\"ActionPara\" : \"" + ActionPara.replace("\\", "\\\\") + "\", ";
		s = s + "\"Delay\" : " + Delay + "}";
		return s;
	}
	
	public void fromJson(JsonObject jObj) {
		try {
			ActionGroup = jObj.get("ActionGroup").getAsString();
			Action = jObj.get("Action").getAsString();
			ActionPara = jObj.get("ActionPara").getAsString();
			Delay = jObj.get("Delay").getAsInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
