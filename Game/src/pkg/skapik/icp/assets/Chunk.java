package pkg.skapik.icp.assets;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import pkg.skapik.icp.func.Coords_Manager;
import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Renderer;

public class Chunk implements Runnable{
	public static enum state {OLD, CURRENT, NEW};
	
	//private int[][][] chunk_map;
	private Block[][][] chunk_map;
	private int x,z,highest_block;
	private Coords_Manager CM;
	private state age;	
	private Renderer renderer;
	private ArrayList<Block> visible_blocks;
	private ArrayList<Foliage> foliage;
	
	private FloatBuffer vertices;
	private FloatBuffer coords;
	private FloatBuffer normals;
	private boolean is_ready;
	private int faces_to_draw;
	private int distance_from_player;
	private boolean runnig;
	
	public Chunk(Renderer renderer, World_generator world, Coords_Manager cm, int x, int z, int distance){
		this.x = x;
		this.z = z;
		this.CM = cm;
		this.renderer = renderer;
		distance_from_player = distance;
		this.faces_to_draw = 0;
		this.visible_blocks = new ArrayList<>();
		this.foliage = new ArrayList<>();
		//this.chunk_map = new int[16][World_generator.MAX_WORLD_HEIGHT][16];
		this.chunk_map = new Block[16][World_generator.MAX_WORLD_HEIGHT][16];
		this.highest_block = world.generate_chunk(this.x, this.z, chunk_map, foliage);
		try {
            FileReader fileReader = new FileReader("./World/Chunks/"+this.x+"_"+this.z+".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] nums = new String[4];
            while((line = bufferedReader.readLine()) != null) {
            	if(line.compareTo("") != 0){
	                nums = line.split(";");
	                chunk_map[Integer.parseInt(nums[0])][Integer.parseInt(nums[1])][Integer.parseInt(nums[2])] = new Block(Integer.parseInt(nums[3]));
	                this.highest_block = Math.max(this.highest_block, Integer.parseInt(nums[1]));
                }
            }
            bufferedReader.close();     
        }catch(FileNotFoundException ex) {
            //System.out.println("Unable to open file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");                
        }catch(IOException ex) {
            System.out.println("Error reading file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");                  
        }
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader("./World/Chunks/"+this.x+"_"+this.z+"_f.txt"))) {
            String line;
            String[] nums = new String[4];
            while((line = bufferedReader.readLine()) != null) {
            	if(line.compareTo("") != 0){
	                nums = line.split(";");
	                Foliage f = new Foliage(new Position(Double.parseDouble(nums[0]),Double.parseDouble(nums[1]),Double.parseDouble(nums[2])),Integer.parseInt(nums[3]));
	                this.foliage.add(f);
	                chunk_map[(int)Double.parseDouble(nums[0])][(int)Double.parseDouble(nums[1])][(int)Double.parseDouble(nums[2])].attach_floiage(f);
                }
            }
            bufferedReader.close();     
        }catch(FileNotFoundException ex) {
            //System.out.println("Unable to open file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");                
        }catch(IOException ex) {
            System.out.println("Error reading file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");                  
        }
		save_foliage();
		this.age = state.NEW;
		this.is_ready = false;
		this.runnig = false;
	}
	
	public void Init__(){
		
	}
	
	private void add_draw_data(Block b) {
		FloatBuffer buff = b.get_vertices();
		for (int j = 0; j < buff.capacity(); j++){
			vertices.put(buff.get(j));
		}
		buff = b.get_coords();
		for (int j = 0; j < buff.capacity(); j++){
			coords.put(buff.get(j));
		}
		buff = b.get_normals();
		for (int j = 0; j < buff.capacity(); j++){
			normals.put(buff.get(j));
		}
		

	}
	
	private byte get_visibility(int block_id) {
		if(block_id < 0){
			return 0;
		}else if(block_id == Block.WATER){
			return 1;
		}
		return 2;
	}


	private boolean[] check_if_visible(int x, int y, int z) {
		boolean[] faces = {false,false,false,false,false,false};
		boolean visible = false;
		byte vis = chunk_map[x][y][z].get_visibility();
		int x2,y2,z2;
		
		x2 = x; // blok pred front face?
		y2 = y;
		z2 = z+1;
		if(z2 <= 15){
			if(get_visibility(chunk_map[x2][y2][z2].block_id) < vis){
				faces[0] = true;
				visible = true;
			}
		}else{
			if(get_visibility(renderer.get_block_id(this.x*16+x2,y2,this.z*16+z2)) < vis){
				faces[0] = true;
				visible = true;
			}
		}
				
		z2 = z-1; // blok pred back face? (x2 a y2 se nemeni)
		if(z2 >= 0){
			if(get_visibility(chunk_map[x2][y2][z2].block_id) < vis){
				faces[2] = true;
				visible = true;
			}
		}else{
			if(get_visibility(renderer.get_block_id(this.x*16+x2,y2,this.z*16+z2)) < vis){
				faces[2] = true;
				visible = true;
			}
		}
		
		x2 = x+1; // blok pred right face? (y2 se nemeni)
		z2 = z;
		if(x2 <= 15){
			if(get_visibility(chunk_map[x2][y2][z2].block_id) < vis){
				faces[1] = true;
				visible = true;
			}
		}else{
			if(get_visibility(renderer.get_block_id(this.x*16+x2,y2,this.z*16+z2)) < vis){
				faces[1] = true;
				visible = true;
			}
		}
		
		x2 = x-1; // blok pred left face? (y2 a z2 se nemeni)
		if(x2 >= 0){
			if(get_visibility(chunk_map[x2][y2][z2].block_id) < vis){
				faces[3] = true;
				visible = true;
			}
		}else{
			if(get_visibility(renderer.get_block_id(this.x*16+x2,y2,this.z*16+z2)) < vis){
				faces[3] = true;
				visible = true;
			}
		}
		
		x2 = x; // blok pred top face? (z2 se nemeni)
		y2 = y+1;
		if(y2 < World_generator.MAX_WORLD_HEIGHT){
			if(get_visibility(chunk_map[x2][y2][z2].block_id) < vis){
				faces[4] = true;
				visible = true;
			}
		}else{
			faces[4] = true;
			visible = true;
		}

		y2 = y-1;// blok pred top face? (x2 a z2 se nemeni)
		if(y2 >= 0){
			if(get_visibility(chunk_map[x2][y2][z2].block_id) < vis){
				faces[5] = true;
				visible = true;
			}
		}else{
			faces[5] = true;
			visible = true;
		}
		
		if(visible){
			return faces;
		}
		boolean[] res = {false};
		return res;
	}

	public void Update(){
		
	}
	
	public void draw(GL2 gl, float detail) {
		if(this.is_ready){
			gl.glTranslatef(x*16, 0, z*16);
	
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
	 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, coords);
	 		gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
			gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("grey"), 0);
			gl.glDrawArrays(GL2.GL_QUADS, 0, faces_to_draw*4);
		}else{
			if(!this.runnig){
				this.run();
			}
		}
	}
	
	public void draw_foliage(GL2 gl){
		gl.glDepthMask(false);
		for(Foliage F : foliage){
    		gl.glPushMatrix();
            gl.glTranslatef(F.get_position().getX_F(), F.get_position().getY_F(), F.get_position().getZ_F());
            gl.glCallList(renderer.foliage_0+F.get_type());
            gl.glPopMatrix();
    		
    	}
		gl.glDepthMask(true);
	}

	public boolean is_position(int ch_x, int ch_z) {
		if(this.x == ch_x && this.z == ch_z){
			return true; 
		}
		return false;
	}

	public int get_block_id(int x, int y, int z) {
		return chunk_map[x][y][z].block_id;
	}
	
	public Block get_block(int x, int y, int z) {
		return chunk_map[x][y][z];
	}

	public int get_block_solidity(int x, int y, int z) {
		return chunk_map[x][y][z].get_solidity();
	}

	public void set_age(state s) {
		this.age = s;
	}

	public state get_age() {
		return this.age;
	}

	/*public int[][][] get_map() {
		return this.chunk_map;
	}*/

	@Override
	public void run() {
		if(!this.runnig){
			this.runnig = true;
			this.is_ready = false;
			visible_blocks.clear();
			this.faces_to_draw = 0;
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					for(int y = 0; y <= this.highest_block; y++){
						if(chunk_map[x][y][z].block_id >= 0){
							boolean[] vis = this.check_if_visible(x,y,z);
							if(vis.length > 1){
						    	chunk_map[x][y][z].setup(CM, x, y, z, vis, distance_from_player);
						    	if(Block.is_usable(chunk_map[x][y][z].block_id)){
						    		chunk_map[x][y][z].setup_usable();
						    	}
						    	faces_to_draw += chunk_map[x][y][z].get_draw_faces_num();
								visible_blocks.add(chunk_map[x][y][z]);
							}
						}
						/*if(chunk_map[x][y][z] >= 0){
							boolean[] draw_faces = {true,true,true,true,true,true};
							visible_blocks.add(new Block(CM, chunk_map[x][y][z], x, y, z, draw_faces));
						}*/
					}
				}
			}
			
			
			//System.out.println("faces: " + faces_to_draw);
			vertices = Buffers.newDirectFloatBuffer(faces_to_draw*12);
			coords = Buffers.newDirectFloatBuffer(faces_to_draw*8);
			normals = Buffers.newDirectFloatBuffer(faces_to_draw*12);
			for(Block b : visible_blocks){
				this.add_draw_data(b);
			}
			vertices.rewind();
			coords.rewind();
			normals.rewind();
			this.is_ready = true;
			this.runnig = false;
		}
	}

	
	public void put_block(int id, Position pos){
		int x = pos.getX_I()-this.x*16;
		int z = pos.getZ_I()-this.z*16;
		
		this.put_block_in_chunk(id,new Position(x,pos.getY_I(),z));
		/*chunk_map[x][y][z] = new Block(id, this.renderer.get_coords_manager());
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./World/Chunks/"+this.x+"_"+this.z+".txt", true)))) {
		    out.println(x+";"+y+";"+z+";"+id);
		}catch (IOException e) {
			System.err.println("Error writing to file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");
		}
		if(y > highest_block)
			highest_block = y;
		if(!this.runnig){
			this.run();
		}*/
	}
	
	public void put_block_in_chunk(int id, Position pos){
		int x = pos.getX_I();
		int y = pos.getY_I();
		int z = pos.getZ_I();

		Block B = new Block(id, this.renderer.get_coords_manager());
		if(Block.is_usable(B.block_id)){
			B.setup_usable();
		}
		chunk_map[x][y][z] = B;
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./World/Chunks/"+this.x+"_"+this.z+".txt", true)))) {
		    out.println(x+";"+y+";"+z+";"+id);
		}catch (IOException e) {
			System.err.println("Error writing to file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");
		}
		if(y > highest_block)
			highest_block = y;
		if(!this.runnig){
			this.run();
		}
	}

	
	public void remove_block(Position pos) {
		int x = pos.getX_I()-this.x*16;
		int y = pos.getY_I();
		int z = pos.getZ_I()-this.z*16;
		
		if(chunk_map[x][y][z].get_floiage() != null){
			this.foliage.remove(chunk_map[x][y][z].get_floiage());
			save_foliage();
		}
		
		chunk_map[x][y][z] = new Block(-1, this.renderer.get_coords_manager());
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./World/Chunks/"+this.x+"_"+this.z+".txt", true)))) {
		    out.println(x+";"+y+";"+z+";"+"-1");
		}catch (IOException e) {
			System.err.println("Error writing to file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");
		}
		
		if(!this.runnig){
			this.run();
		}
		if(x == 0){
			renderer.get_chunk(new Point(this.x-1,this.z)).run();
		}
		if(x == 15){
			renderer.get_chunk(new Point(this.x+1,this.z)).run();
		}
		if(z == 0){
			renderer.get_chunk(new Point(this.x,this.z-1)).run();
		}
		if(z == 15){
			renderer.get_chunk(new Point(this.x,this.z+1)).run();
		}
	}
	
	public void save_foliage(){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./World/Chunks/"+this.x+"_"+this.z+"_f.txt", false)))) {
			for(Foliage F : foliage){
				out.println(F.get_position().getX_D()+";"+F.get_position().getY_D()+";"+F.get_position().getZ_D()+";"+F.get_type());
		    }
		}catch (IOException e) {
			System.err.println("Error writing to file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");
		}
	}
	
	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}
}
