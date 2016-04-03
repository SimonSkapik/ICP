package pkg.skapik.icp.assets;

import java.util.Random;

public class Environmental_seed {
	
	public static final int NOTHING = 0;
	public static final int TREE = 1;
	public static final int FOLIAGE = 2;
	public static final int ROCK = 3;
	
	private int entity;
	private int x, y, z;
	
	public Environmental_seed(int x, int y, int z, int ground){
		this.x = x;
		this.y = y;
		this.z = z;
		float tree_chance = 3;
		float foliage_chance = 10;
		float rock_chance = 0.5f;
		
		Random rnd = new Random();
		float chance = rnd.nextFloat()*99;
		switch(ground){
			case Block.GRASS:{
				if(chance < tree_chance){
					entity = TREE;
				}else if(chance < (tree_chance+foliage_chance)){
					entity = FOLIAGE;
				}else if(chance < (tree_chance+foliage_chance+rock_chance)){
					entity = ROCK;
				}else{
					entity = NOTHING;
				}
			}break;
			case Block.SAND:{
				if(chance < tree_chance){
					entity = TREE;
				}else if(chance < (tree_chance+rock_chance)){
					entity = ROCK;
				}else{
					entity = NOTHING;
				}
			}break;
			default:{
				entity = NOTHING;
			}
		}
	}

	public boolean is_entity() {
		if(this.entity > 0)
			return true;
		return false;
	}
	

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int get_entity() {
		return this.entity;
	}

}
