package pkg.skapik.icp.assets;

import java.util.Random;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Vector;

public class Cloud {
	private float speed, drift_angle, size_x, size_y, size_z;
	private Position position;
	
	public Cloud(Position pos){
		this.position = pos;
		Random rnd = new Random();
		size_x = rnd.nextFloat()*20+2;
		size_y = rnd.nextFloat()*4+1;
		size_z = rnd.nextFloat()*20+2;
		speed = (float)((((3916-(size_x*size_y*size_z-4))*1.5)/3916.0)+0.5)*0.2f;
		drift_angle = (rnd.nextFloat()-0.5f)*0.3f;
	}
	
	public void Update(Vector dir){
		Vector direction = new Vector(dir);
		direction.rotate(Vector.Y_AXIS,drift_angle);
		this.position.move_by(direction, speed);
	}
	
	public void Draw(GL2 gl, GLUT glut){
		gl.glPushMatrix();
		gl.glEnable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white", 0.6f), 0);
		gl.glScalef(size_x, size_y, size_z);
		gl.glScalef(4,2,4);
		glut.glutSolidCube(1);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_BLEND);
		gl.glPopMatrix();
	}

	public Position get_position() {
		return this.position;
	}
	
}
