package pkg.skapik.icp.func;

public class Boundery_box {
	
	private float width,height,depth;
	private Position origin;
	
	public Boundery_box(float w, float h, float d, float o_x, float o_y, float o_z) {
		this.width = w;
		this.height = h;
		this.depth = d;
		this.origin = new Position(o_x*w, o_y*h, o_z*d);
	}

	public double get_left() {
		return this.origin.getX_D();
	}
	
	public double get_right() {
		return this.width - this.origin.getX_D();
	}
	
	public double get_bot() {
		return this.origin.getY_D();
	}	
	
	public double get_top() {
		return this.height - this.origin.getY_D();
	}

	public double get_back() {
		return this.origin.getZ_D();
	}	
	
	public double get_front() {
		return this.depth - this.origin.getZ_D();
	}	
}
