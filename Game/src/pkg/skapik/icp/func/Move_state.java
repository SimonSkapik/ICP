package pkg.skapik.icp.func;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import pkg.skapik.icp.assets.Player;

public class Move_state {

	private enum State {Stopped, Walking, Jumping};
	private State state;
	private Vector wanted_direction;
	private Vector gravity_vector;
	private Vector front;
	private Player player;
	private float angle_deviation;
	
	private ArrayList<Walk_Key> Walk_keys;
	
	public Move_state (Player player){
		this.player = player;
		this.init();
	}
	
	private void init(){
		this.state = State.Stopped;
		this.wanted_direction = new Vector(0,0,0);
		this.gravity_vector = new Vector(0,-1,0);
		this.front =  new Vector(0,0,-1);
		this.angle_deviation = 0;
		this.register_keys();
	}
	
	private void register_keys() {
		this.Walk_keys = new ArrayList<Walk_Key>();
		
		this.Walk_keys.add(new Walk_Key("Forward", 87, new Vector(0,0,-1)));
		this.Walk_keys.add(new Walk_Key("Backward", 83, new Vector(0,0,1)));
		this.Walk_keys.add(new Walk_Key("Left", 65, new Vector(-1,0,0)));
		this.Walk_keys.add(new Walk_Key("Right", 68, new Vector(1,0,0)));
		//this.Walk_keys.add(new Walk_Key("Jump", 32, new Vector(0,1,0)));
		//this.Walk_keys.add(new Walk_Key("Crouch", 17, new Vector(0,-1,0)));
	}

	public void Move(int key_code, int action){
		Vector new_direction = new Vector(0, 0, 0);
		if(key_code == 32){
			this.Jump();
		}else{
		for(Walk_Key key : this.Walk_keys){
			if(key.get_code() == key_code){
				if(action == KeyEvent.KEY_PRESSED){
					key.set_key_state(true);
				}else{
					key.set_key_state(false);
				}
			}
			if(key.is_pressd()){
				new_direction.add(key.get_walk_direction());
			}
		}
		if(!new_direction.is_zero()){
			new_direction.normalize();
			this.state = State.Walking;
		}else{
			this.state = State.Stopped;
		}
		wanted_direction = new_direction;
		angle_deviation = wanted_direction.get_angle_to(front);
		}
		/*switch(this.current_state){
			case Stopped:{
				
			}break;
			case Walking:{
				
			}break;
			default:{
				this.init();
			}
		}*/
	}
	
	public void Jump(){
		if(this.gravity_vector.getY_D() == 0 || player.is_in_water())  
			this.gravity_vector.setY(1);
	}

	public Vector get_move_direction() {
		return this.wanted_direction;
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

	public float get_angle_deviation() {
		return angle_deviation;
	}

	public void update_gravity(int collision) {
		float k = 1;
		if(player.is_in_water()){
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
