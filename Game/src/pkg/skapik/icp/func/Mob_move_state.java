package pkg.skapik.icp.func;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import pkg.skapik.icp.assets.Zombie;

public class Mob_move_state {

	private enum State {Stopped, Walking, Jumping};
	private State state;
	private Vector wanted_direction;
	private Vector gravity_vector;
	private Zombie mob;
	private float angle_deviation;
	
	public Mob_move_state (Zombie mob){
		this.mob = mob;
		this.init();
	}
	
	private void init(){
		this.gravity_vector = new Vector(0,-1,0);
	}

	public void Jump(){
		if(this.gravity_vector.getY_D() == 0 || mob.is_in_water())  
			this.gravity_vector.setY(1);
	}

	public Vector get_gravity_vector() {
		return this.gravity_vector;
	}
	
	public boolean is_moving() {
		if(state == State.Walking){
			return true;
		} 
		return false;
	}

	public void update_gravity(int collision) {
		float k = 1;
		if(mob.is_in_water()){
			k = 1.0f/3.0f;
		}
		if(collision == 0){
			if(gravity_vector.getY_D() > (-3.0*k)){
				gravity_vector.setY(gravity_vector.getY_D()-(0.051*k));
			}else{
				gravity_vector.setY(-3.0*k);
			}
		}else{
			gravity_vector.setY(0);
		}
	}
	
}
