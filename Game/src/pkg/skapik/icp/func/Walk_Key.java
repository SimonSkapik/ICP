package pkg.skapik.icp.func;

public class Walk_Key {

	private int code;
	private boolean pressed;
	private Vector walk_direction;
	
	public Walk_Key(String name, int key_code, Vector dir) {
		this.code = key_code;
		this.walk_direction = dir;
		this.pressed = false;
	}
	
	public void set_key_state(boolean state) {
		this.pressed = state;
	}
	
	public Vector SET_walk_direction(Vector v) {
		return this.walk_direction;
	}
	
	public Vector get_walk_direction() {
		return this.walk_direction;
	}

	public boolean is_pressd() {
		return this.pressed;
	}

	public int get_code() {
		return this.code;
	}
	
	
}
