package pkg.skapik.icp.func;

import pkg.skapik.icp.assets.Creature;

public class Collision_detector {
	
	private Renderer renderer;
	private Creature player;
	
	public Collision_detector(Renderer R,Creature P){
		this.renderer = R;
		this.player = P;
	}
	
	public float[] check_collision(int out){
		float[] collisions = new float[]{0,0,0,0,0,0,0,0};  // data o kolizich [osa_x{0/1/2}, osa_z{0/1/2}, ground{0/1}, ceiling{0/1}, distance_x, distance_y, distance_ground, distance_ceil]
		Vector dir = new Vector(player.get_move_direction());
		int sig_x = (int) Math.signum(dir.getX_D());
		int sig_z = (int) Math.signum(dir.getZ_D());
		//Vector grav = player.get_gravity_vector();
		int current_block_x = (int)Math.floor(player.get_position().getX_D());
		int current_block_y = (int)Math.floor(player.get_position().getY_D());
		int current_block_z = (int)Math.floor(player.get_position().getZ_D());
		int feet_block_y = (int)Math.floor(player.get_position().getY_D() - player.get_boundery().get_bot());
		
		int block_state = 0;
		int tmp_block_state = 0;
		 
		byte position_case = 0;
		int[] pos_blocks = new int[4];
		if(player.get_position().getX_D() - player.get_boundery().get_left() < current_block_x){
			if(out == 1)System.err.println("if 0");
			pos_blocks[0] = current_block_x - 1;
			pos_blocks[1] = current_block_x;
			position_case += 2;
		}else if(player.get_position().getX_D() + player.get_boundery().get_right() >= (current_block_x+1)){
			if(out == 1)System.err.println("if 1");
			pos_blocks[0] = current_block_x;
			pos_blocks[1] = current_block_x + 1;
			position_case += 2;
		}else{
			if(out == 1)System.err.println("if 2");
			pos_blocks[0] = current_block_x;
			pos_blocks[1] = current_block_x;
		}
		
		if(player.get_position().getZ_D() - player.get_boundery().get_back() < current_block_z){
			if(out == 1)System.err.println("if 3");
			pos_blocks[2] = current_block_z - 1;
			pos_blocks[3] = current_block_z;
			position_case += 1;
		}else if(player.get_position().getZ_D() + player.get_boundery().get_front() >= (current_block_z+1)){
			if(out == 1)System.err.println("if 4");
			pos_blocks[2] = current_block_z;
			pos_blocks[3] = current_block_z + 1;
			position_case += 1;
		}else{
			if(out == 1)System.err.println("if 5");
			pos_blocks[2] = current_block_z;
			pos_blocks[3] = current_block_z;
		}
		
		byte y_taken = 0;
		if(player.get_position().getY_D() + player.get_boundery().get_top() > current_block_y+1 ||
			player.get_position().getY_D() - player.get_boundery().get_bot() < current_block_y-1){
			collisions[3] = 0;
			collisions[7] = 1;
			y_taken = 3;
		}else{
			if(renderer.get_block_solidity(current_block_x,current_block_y+1,current_block_z) > 0){
				collisions[3] = 1;
				collisions[7] = (float)(current_block_y+0.95 - player.get_boundery().get_top());//(current_block_y+1-(player.get_position().getY_D() + player.get_boundery().get_top()));
			}else{
				collisions[3] = 0;
				collisions[7] = 1;
			}
			y_taken = 2;			
		}
		
		switch(position_case){
			case 0:{
				if(out == 1)System.err.println("case 0");
				if(renderer.get_block_solidity(pos_blocks[0],feet_block_y-1,pos_blocks[2]) > 0){
					collisions[2] = 1;
					collisions[6] = (float)(feet_block_y+player.get_boundery().get_bot() + 0.05);//((player.get_position().getY_D() - player.get_boundery().get_bot())-(feet_block_y+1));
				}else{
					collisions[2] = 0;
					collisions[6] = 0;
				}
				if(sig_x != 0){
					block_state = 0;
					if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y,pos_blocks[2]) > 0){
						block_state += 1; 
					}
					if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y+1,pos_blocks[2]) > 0){
						block_state += 2; 
					}
					if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y+2,pos_blocks[2]) > 0){
						block_state += 4; 
					}
					collisions[0] = get_collision_state(block_state,collisions[3],y_taken);
					if(sig_x < 0){
						collisions[4] = (float)(pos_blocks[0] + player.get_boundery().get_left() + 0.05);//(player.get_position().getX_D() - player.get_boundery().get_left() - pos_blocks[0]);
					}else{
						collisions[4] = (float)(pos_blocks[0]+1-player.get_boundery().get_right() - 0.05);//(pos_blocks[1] + 1 - (player.get_position().getX_D() + player.get_boundery().get_right()));
					}
				}else{
					collisions[0] = 0;
					collisions[4] = 1;
				}
				if(sig_z != 0){
					block_state = 0;
					if(renderer.get_block_solidity(current_block_x,feet_block_y,current_block_z+sig_z) > 0){
						block_state += 1; 
					}
					if(renderer.get_block_solidity(current_block_x,feet_block_y+1,current_block_z+sig_z) > 0){
						block_state += 2; 
					}
					if(renderer.get_block_solidity(current_block_x,feet_block_y+2,current_block_z+sig_z) > 0){
						block_state += 4; 
					}
					collisions[1] = get_collision_state(block_state,collisions[3],y_taken);
					if(sig_z < 0){
						collisions[5] = (float)(pos_blocks[2] + player.get_boundery().get_back() + 0.05);//(player.get_position().getZ_D() - player.get_boundery().get_back() - pos_blocks[2]);
					}else{
						collisions[5] = (float)(pos_blocks[2]+1 - player.get_boundery().get_front() - 0.05);//(pos_blocks[3] + 1 - (player.get_position().getZ_D() + player.get_boundery().get_front()));
					}
				}else{
					collisions[1] = 0;
					collisions[5] = 1;
				}
				/*if(collisions[0] < 2 && collisions[1] < 2 && sig_z != 0 && sig_x != 0){
					
				}*/
			}break;
			case 1:{
				if(out == 1)System.err.println("case 1");
				if(renderer.get_block_solidity(pos_blocks[0],feet_block_y-1,pos_blocks[2]) > 0 ||
					renderer.get_block_solidity(pos_blocks[0],feet_block_y-1,pos_blocks[3]) > 0 ){
					collisions[2] = 1;
					collisions[6] = (float)(feet_block_y+player.get_boundery().get_bot() + 0.05);//((player.get_position().getY_D() - player.get_boundery().get_bot())-(feet_block_y+1));
				}else{
					collisions[2] = 0;
					collisions[6] = 0;
				}
				collisions[1] = 0;
				collisions[5] = 1;
				if(sig_x != 0){
					block_state = 0;
					if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y,pos_blocks[2]) > 0){
						block_state += 1;
					}
					if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y+1,pos_blocks[2]) > 0){
						block_state += 2; 
					}
					if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y+2,pos_blocks[2]) > 0){
						block_state += 4; 
					}
					collisions[0] = get_collision_state(block_state,collisions[3],y_taken);
					if(sig_x < 0){
						collisions[4] = (float)(pos_blocks[0] + player.get_boundery().get_left() + 0.05);//(player.get_position().getX_D() - player.get_boundery().get_left() - pos_blocks[0]);
					}else{
						collisions[4] = (float)(pos_blocks[0]+1 - player.get_boundery().get_right() - 0.05);//(pos_blocks[1] + 1 - (player.get_position().getX_D() + player.get_boundery().get_right()));
					}
				
					if(collisions[0] < 2){
						block_state = 0;
						if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y,pos_blocks[3]) > 0){
							block_state += 1; 
						}
						if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y+1,pos_blocks[3]) > 0){
							block_state += 2; 
						}
						if(renderer.get_block_solidity(pos_blocks[0]+sig_x,feet_block_y+2,pos_blocks[3]) > 0){
							block_state += 4; 
						}
						tmp_block_state = get_collision_state(block_state,collisions[3],y_taken);
						if(tmp_block_state > 0){
							collisions[0] = tmp_block_state;
						}
					}
				}else{
					collisions[0] = 0;
					collisions[4] = 1;
				}
			}break;
			case 2:{
				if(out == 1)System.err.println("case 2");
				if(renderer.get_block_solidity(pos_blocks[0],feet_block_y-1,pos_blocks[2]) > 0 ||
					renderer.get_block_solidity(pos_blocks[1],feet_block_y-1,pos_blocks[2]) > 0 ){
					collisions[2] = 1;
					collisions[6] = (float)(feet_block_y+player.get_boundery().get_bot() + 0.05);//((player.get_position().getY_D() - player.get_boundery().get_bot())-(feet_block_y+1));
				}else{
					collisions[2] = 0;
					collisions[6] = 0;
				}
				collisions[0] = 0;
				collisions[4] = 1;
				if(sig_z != 0){
					block_state = 0;
					if(renderer.get_block_solidity(pos_blocks[0],feet_block_y,pos_blocks[2]+sig_z) > 0){
						block_state += 1; 
					}
					if(renderer.get_block_solidity(pos_blocks[0],feet_block_y+1,pos_blocks[2]+sig_z) > 0){
						block_state += 2; 
					}
					if(renderer.get_block_solidity(pos_blocks[0],feet_block_y+2,pos_blocks[2]+sig_z) > 0){
						block_state += 4; 
					}
					collisions[1] = get_collision_state(block_state,collisions[3],y_taken);
					if(sig_z < 0){
						collisions[5] = (float)(pos_blocks[2] + player.get_boundery().get_back() + 0.05);//(player.get_position().getZ_D() - player.get_boundery().get_back() - pos_blocks[2]);
					}else{
						collisions[5] = (float)(pos_blocks[2]+1 - player.get_boundery().get_front() - 0.05);//(pos_blocks[3] + 1 - (player.get_position().getZ_D() + player.get_boundery().get_front()));
					}
				
					if(collisions[1] < 2){
						block_state = 0;
						if(renderer.get_block_solidity(pos_blocks[1],feet_block_y,pos_blocks[2]+sig_z) > 0){
							block_state += 1; 
						}
						if(renderer.get_block_solidity(pos_blocks[1],feet_block_y+1,pos_blocks[2]+sig_z) > 0){
							block_state += 2; 
						}
						if(renderer.get_block_solidity(pos_blocks[1],feet_block_y+2,pos_blocks[2]+sig_z) > 0){
							block_state += 4; 
						}
						tmp_block_state = get_collision_state(block_state,collisions[3],y_taken);
						if(tmp_block_state > 0){
							collisions[1] = tmp_block_state;
						}
					}
				}else{
					collisions[1] = 0;
					collisions[5] = 1;
				}
			}break;
			default:{
				if(out == 1)System.err.println("case def");
				if(renderer.get_block_solidity(pos_blocks[0],feet_block_y-1,pos_blocks[2]) > 0 ||
					renderer.get_block_solidity(pos_blocks[0],feet_block_y-1,pos_blocks[3]) > 0 ||
					renderer.get_block_solidity(pos_blocks[1],feet_block_y-1,pos_blocks[2]) > 0 || 
					renderer.get_block_solidity(pos_blocks[1],feet_block_y-1,pos_blocks[3]) > 0 ){
					collisions[2] = 1;
					collisions[6] = (float)(feet_block_y+player.get_boundery().get_bot() + 0.05);//((player.get_position().getY_D() - player.get_boundery().get_bot())-(feet_block_y+1));
				}else{
					collisions[2] = 0;
					collisions[6] = 0;
				}
				collisions[0] = 0;
				collisions[4] = 1;
				collisions[1] = 0;
				collisions[5] = 1;
			}break;
		}
		return collisions;
	}
	
	private int get_collision_state(int block_state, float ceil_collision, byte y_taken){
		switch(block_state){
			case 0:{
				return 0;
			}
			case 1:{
				if(ceil_collision == 1)
					return 2;
				return 1;
			}
			case 4:{
				if(y_taken == 2)
					return 0;
			}break;
		}
		return 2;
	}
	
	
}
