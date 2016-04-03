package pkg.skapik.icp.func;

public class Vector {
	
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;
	
	private float x,y,z;

	public Vector(int i, int j, int k) {
		this.x = (float)i;
		this.y = (float)j;
		this.z = (float)k;
	}
	
	public Vector(float i, float j, float k) {
		this.x = i;
		this.y = j;
		this.z = k;

	}
	
	public Vector(double i, double j, double k) {
		this.x = (float)i;
		this.y = (float)j;
		this.z = (float)k;
	}
	
	public Vector(Vector original) {
		this.x = original.getX_F();
		this.y = original.getY_F();
		this.z = original.getZ_F();
	}

	public void add(Vector v){
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	public void add(Position player_pos){
		this.x += player_pos.getX_F();
		this.y += player_pos.getY_F();
		this.z += player_pos.getZ_F();
	}

	public float size(){
		return (float)Math.sqrt(x*x + y*y + z*z);
	}

	public double getX_D(){
		return (double)this.x;
	}

	public double getY_D(){
		return (double)this.y;
	}

	public double getZ_D(){
		return (double)this.z;
	}

	public int getX_I(){
		return (int)this.x;
	}

	public int getY_I(){
		return (int)this.y;
	}

	public int getZ_I(){
		return (int)this.z;
	}

	public float getX_F(){
		return this.x;
	}

	public float getY_F(){
		return this.y;
	}

	public float getZ_F(){
		return this.z;
	}
	
	public void normalize(){
		float k = 0;
		if(this.size() > 0){
		k = 1/this.size();
		}
		this.x *= k;
		this.y *= k;
		this.z *= k;
	}

	public boolean is_zero() {
		if((this.x == 0 && this.y == 0) && this.z == 0){
			return true;
		}
		return false;
	}

	public void setX(double d) {
		this.x = (float)d;
	}

	public void setY(double d) {
		this.y = (float)d;
	}

	public void setZ(double d) {
		this.z = (float)d;
	}

	private void add_angleXZ(float angle) {
		//angle = 
		float new_x = (float)(this.x*Math.cos(angle) - this.z*Math.sin(angle));
		float new_z = (float)(this.x*Math.sin(angle) + this.z*Math.cos(angle));
		//System.out.println("angle: " + angle + " | cos: " + Math.cos(angle) + "," + Math.sin(angle) + " | " + this.x + "," + this.z + " -> " + new_x + "," + new_z);
		this.x = new_x;
		this.z = new_z;
	}

	private void add_angleYZ(float angle) {
		float new_y = (float)(this.y*Math.cos(angle) - this.z*Math.sin(angle));
		float new_z = (float)(this.y*Math.sin(angle) + this.z*Math.cos(angle));
		this.y = new_y;
		this.z = new_z;
	}	

	private void add_angleXY(float angle) {
		float new_x = (float)(this.x*Math.cos(angle) - this.y*Math.sin(angle));
		float new_y = (float)(this.x*Math.sin(angle) + this.y*Math.cos(angle));
		this.x = new_x;
		this.y = new_y;
	}
	
	public void rotate(int axis, float angle){
		switch(axis){
			case 0:{this.add_angleYZ(angle);}break;
			case 1:{this.add_angleXZ(angle);}break;
			case 2:{this.add_angleXY(angle);}break;
		}
	}
	
	public void rotate(Vector axis, float angle){
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		double[][] matrix = new double[3][3];
		double ax = axis.getX_D();
		double ay = axis.getY_D();
		double az = axis.getZ_D();
		matrix[0][0] = cos + ax*ax*(1 - cos);
		matrix[0][1] = ax*ay*(1 - cos) - az*sin;
		matrix[0][2] = ax*az*(1 - cos) + ay*sin;
		matrix[1][0] = ay*ax*(1 - cos) + az*sin;
		matrix[1][1] = cos + ay*ay*(1 - cos);
		matrix[1][2] = ay*az*(1 - cos) - ax*sin;
		matrix[2][0] = az*ax*(1 - cos) - ay*sin;
		matrix[2][1] = az*ay*(1 - cos) + ax*sin;
		matrix[2][2] = cos + az*az*(1 - cos);
		this.multiply_from_left(matrix);
	}

	private void multiply_from_left(double[][] M) {
		// new_vecotr =  M * this_vector
		double new_x = 0;
		double new_y = 0;
		double new_z = 0;
		new_x = M[0][0]*x + M[0][1]*y + M[0][2]*z;
		new_y = M[1][0]*x + M[1][1]*y + M[1][2]*z;
		new_z = M[2][0]*x + M[2][1]*y + M[2][2]*z;
		x = (float)new_x;
		y = (float)new_y;
		z = (float)new_z;
	}

	public float get_angle_to(Vector v) {
		double cinitel = (x*v.getX_D() + z*v.getZ_D());
		double jmenovatel = (this.size()*v.size());
		if(jmenovatel != 0){
			return (float) ((float)sgn(x-v.getX_F())*(Math.acos(((int)(1000*(cinitel/jmenovatel))/1000.0))));
		}
		return 0;
	}
	
	private int sgn(float num){
		return (num<0)?-1:1;
	}
}
