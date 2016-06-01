package pkg.skapik.icp.func;

import java.awt.Point;
import java.util.ArrayList;

import com.jogamp.common.nio.Buffers;

import pkg.skapik.icp.assets.Block;
import pkg.skapik.icp.assets.Chunk;
import pkg.skapik.icp.assets.World_generator;

public class Chunk_Loader extends Thread {
	
	private Point default_chunk;
	private World_generator world_gen;
	private Coords_Manager coords_manager;
	private Renderer renderer;
	ArrayList<Chunk> to_load;
	private boolean ready;
	private boolean running;
	//private ArrayList<Chunk> chunk_list;
	
	public Chunk_Loader(Renderer renderer) {
		//radius = 5;
		this.default_chunk = new Point(0, 0);
		this.renderer = renderer;
		this.world_gen = renderer.get_world_generator();
		this.coords_manager = renderer.get_coords_manager();
		this.to_load = new ArrayList<>();
		this.ready = false;
		this.running = false;
	}

	public void change_default_chunk(int x, int y) {
		default_chunk.x = x;
		default_chunk.y = y;
	}

	public void change_default_chunk(Point get_current_chunk) {
		change_default_chunk((int)get_current_chunk.getX(),(int)get_current_chunk.getY());
	}
	
	public void load_chunks(ArrayList<Chunk> chunk_list, int radius) {
		//chunk_list.clear();
		if(!running){
			this.ready = false;
			for(Chunk chunk : chunk_list){
	        	chunk.set_age(Chunk.state.OLD);
	        }
			int d_x = 0;
			int d_y = 0;
			float chunk_distance = 0;
			boolean exists = false;
	        for(int x = (default_chunk.x-radius); x <= (default_chunk.x+radius); x++){
	        	for(int z = (default_chunk.y-radius); z <= (default_chunk.y+radius); z++){
	        		exists = false;
	        		d_x = x - default_chunk.x;
	        		d_y = z - default_chunk.y;
	        		chunk_distance = (float)Math.sqrt(d_x*d_x + d_y*d_y);
	    			for(Chunk chunk : chunk_list){
	    				if(chunk.is_position(x, z)){
	    					chunk.set_age(Chunk.state.CURRENT);
	    					exists = true;
	    					break;
	    				}
	    			}
	    			if(!exists){
	    				if(chunk_distance <= radius){
	        				chunk_list.add(new Chunk(renderer, world_gen, coords_manager, x , z,(int)chunk_distance));
	        			}
	        		}
	        	}
	        }
	        ArrayList<Chunk> to_remove = new ArrayList<>();
	        this.to_load.clear();
	        for(Chunk chunk : chunk_list){
	        	switch(chunk.get_age()){
	        		case OLD:{
	        			to_remove.add(chunk);
	        		}break;
	        		case CURRENT:{
	        			
	        		}break;
	        		case NEW:{
	        			new Thread(chunk).start();
	        		}break;
	    			default:{
	        			
	        		}break;
	        	}
	        }
	        for(Chunk ch : to_remove){
	        	chunk_list.remove(ch);
	        }
	        this.ready = true;
		}
	}
	
	
}
