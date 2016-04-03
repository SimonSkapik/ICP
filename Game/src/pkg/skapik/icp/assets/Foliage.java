package pkg.skapik.icp.assets;

import java.util.Random;

import pkg.skapik.icp.func.Position;

public class Foliage {
	private int type;
	private Position position;
	
	public Foliage(Position pos){
		this.position = pos;
		Random rnd = new Random();
		int i = (int)(rnd.nextFloat()*10);
		if(i < 2 ){
			type = 2;
		}else if(i < 4){
			type = 1;
		}else{
			type = 0;
		}
	}
	
	public Foliage(Position pos,int type){
		this.position = pos;
		this.type = type;
	}
	
	public int get_type(){
		return this.type;
	}

	public Position get_position() {
		return this.position;
	}
}
