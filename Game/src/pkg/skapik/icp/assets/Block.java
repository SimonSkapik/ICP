package pkg.skapik.icp.assets;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import org.opencv.core.Point;

import com.jogamp.common.nio.Buffers;

import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Texture_List;
import pkg.skapik.icp.func.Vertex_manager;
import pkg.skapik.icp.func.Coords_Manager;

public class Block {
	
	public static final int GRASS = 0;
	public static final int STONE = 1;
	public static final int DIRT = 2;
	public static final int PLANKS = 3;
	public static final int STONE_BRICK = 4;
	public static final int BRICKS = 5;
	public static final int TNT = 6;
	public static final int COBBLESTONE = 7;
	public static final int BEDROCK = 8;
	public static final int SAND = 9;
	public static final int GRAVEL = 10;
	public static final int OAK_LOG = 11;
	public static final int BLOCK_OF_IRON = 12;
	public static final int BLOCK_OR_GOLD = 13;
	public static final int BLOCK_OF_DIAMOND = 14;
	public static final int BLOCK_OF_LAPIS = 15;
	public static final int CHEST = 16;
	public static final int ORE_GOLD = 17;
	public static final int ORE_IRON = 18;
	public static final int ORE_COAL = 19;
	public static final int ORE_DIAMOND = 20;
	public static final int ORE_REDSTONE = 21;
	public static final int ORE_LAPIS = 22;
	public static final int OAK_LEAF = 23;
	public static final int COBWEB = 24;
	public static final int WATER = 25;
	public static final int MOSSY_COBBLESTONE = 26;
	public static final int WORKBENCH = 27;
	
	public static final int PICKAXE = 100;
	public static final int SHOVEL = 101;
	public static final int AXE = 102;
	public static final int SWORD = 103;
	
	
	private Coords_Manager CM;
	private Position position;
	private float[] material_color;
	private FloatBuffer texture;
	private FloatBuffer vertices;
	private FloatBuffer normals;
	//private IntBuffer indices;
	private boolean is_visible;
	private boolean[] draw_faces;
	//private boolean[] draw_faces = {true,true,true,true,true,true};
	private int faces_to_draw;
	private int chunk_distance;
	private byte visibility_lvl;
	private byte solidity_lvl;
	private Foliage attached_foliage;
	private Usable usable;
	
	public int block_id;
	
	
	public Block(Coords_Manager coords_manager, int id, int x, int y, int z, boolean[] vis, int distance){
		this.position = new Position(x, y, z);
		CM = coords_manager;
		this.block_id = id;
		this.material_color = Custom_Draw.float_color("white");
		this.is_visible = true;
		this.draw_faces = vis;
		this.chunk_distance = distance;
		this.faces_to_draw = 0;
		this.visibility_lvl = this.set_visibility();
		this.solidity_lvl = set_solidity();
		this.attached_foliage = null;
		for(int i = 0;i < 6; i++){
			if(this.draw_faces[i])
				this.faces_to_draw += 1;
		}
		//draw_faces = new boolean[6];
		//    F--------E   A - 0,1,1
		//   /|       /|   B - 0,0,1
		//  / |      / |   C - 1,0,1
		// A--------D  |   D - 1,1,1
		// |  G-----|--H   E - 1,1,0
		// | /      | /    F - 0,1,0
		// |/       |/     G - 0,0,0
		// B--------C      H - 1,0,0
		// texture coordinaty se tahaj v poradi: 	pro strany	A,B,C,D
		//											pro top    	F,A,D,E
		//											pro bot    	H,C,B,G
		
		// Vertex array se seradi podle toho. protoze je statickej ale textury se pocitaj. tak at mam jasny pocitani a to staticky tam klidne zapisu rozhazeny :-)
		this.Update();
	}
	
	private byte set_visibility() {
		if(this.block_id < 0){
			return 0;
		}else if(this.block_id == Block.WATER){
			return 1;
		}
		return 2;
	}
	
	private byte set_solidity() {
		if(this.block_id < 0 || this.block_id == Block.WATER){
			return 0;
		}
		return 1;
	}

	public Block(int id, int x, int y, int z){
		this.position = new Position(x, y, z);
		CM = null;
		block_id = id;
		material_color = Custom_Draw.float_color("white");
		is_visible = false;
		draw_faces = new boolean[]{false,false,false,false,false,false};
		chunk_distance = 0;
		this.visibility_lvl = this.set_visibility();
		this.solidity_lvl = this.set_solidity();
		faces_to_draw = 0;
		this.attached_foliage = null;
		faces_to_draw = 0;
	}
	
	public Block(int id) {
		this.block_id = id;
		this.position = null;
		this.CM = null;
		material_color = Custom_Draw.float_color("white");
		is_visible = false;
		this.visibility_lvl = this.set_visibility();
		this.solidity_lvl = this.set_solidity();
		draw_faces = new boolean[]{false,false,false,false,false,false};
		chunk_distance = 0;
		faces_to_draw = 0;
		this.attached_foliage = null;
		faces_to_draw = 0;
	}
	
	public Block(int id,Coords_Manager coords_manager) {
		this.block_id = id;
		this.position = null;
		this.CM = coords_manager;
		material_color = Custom_Draw.float_color("white");
		is_visible = false;
		this.visibility_lvl = this.set_visibility();
		this.solidity_lvl = this.set_solidity();
		draw_faces = new boolean[]{false,false,false,false,false,false};
		chunk_distance = 0;
		faces_to_draw = 0;
		faces_to_draw = 0;
		this.attached_foliage = null;
	}
	
	public void setup(Coords_Manager coords_manager,int x, int y, int z, boolean[] vis, int distance){
		this.position = new Position(x, y, z);
		CM = coords_manager;
		
		this.material_color = Custom_Draw.float_color("white");
		this.is_visible = true;
		this.draw_faces = vis;
		this.chunk_distance = distance;
		this.visibility_lvl = this.set_visibility();
		this.solidity_lvl = this.set_solidity();
		this.faces_to_draw = 0;
		for(int i = 0;i < 6; i++){
			if(this.draw_faces[i])
				this.faces_to_draw += 1;
		}
		this.Update();
	}

	private void get_Block_vertices(boolean[] draw_faces, int faces_to_draw, Position pos) {
		int x = pos.getX_I();
		int y = pos.getY_I();
		int z = pos.getZ_I();
		int x1 = x+1;
		int y1 = y+1;
		int z1 = z+1;
		
		float vert[] = {x,y1,z1, x,y,z1, x1,y,z1, x1,y1,z1, x1,y1,z1, x1,y,z1, x1,y,z, x1,y1,z,  // front, right       // 6x4x3 of vertex coords
				x1,y1,z, x1,y,z, x,y,z, x,y1,z, x,y1,z, x,y,z, x,y,z1, x,y1,z1,  // back, left
				x,y1,z, x,y1,z1, x1,y1,z1, x1,y1,z, x1,y,z, x1,y,z1, x,y,z1, x,y,z,}; // top, bot
		float norm[] = {0,0,1, 1,0,0, 0,0,-1, -1,0,0, 0,1,0, 0,-1,0};
		
		this.vertices = Buffers.newDirectFloatBuffer(faces_to_draw*12);
		this.normals = Buffers.newDirectFloatBuffer(faces_to_draw*12);

		for (int i = 0; i < 6; i++){
			if(draw_faces[i]){
				for (int j = 0; j < 12; j++){
					this.vertices.put(vert[i*12+j]);
					this.normals.put(norm[i*3+(j%3)]);
				}
			}
		}
		this.vertices.rewind();
		this.normals.rewind();
		
	}
	
	private void Update(){
		if(this.block_id >= 0){
			this.get_Block_vertices(this.draw_faces, this.faces_to_draw, this.position);
			texture = this.set_texture(false);
		}
	}
	
	private FloatBuffer set_texture(boolean complete){
		int num = 0;
		boolean[] faces;
		int cutoff = 0;
		
		if(complete){
			num = 6;
			faces = new boolean[]{true,true,true,true,true,true};
			cutoff = 1;
		}else{
			num = faces_to_draw;
			faces = draw_faces;
			cutoff =  chunk_distance*2;
		}
		
		if(this.block_id >= 0){
			switch(block_id){
				case GRASS:{
					return CM.get_texture_coords(faces, num, Texture_List.GRASS_SIDE, Texture_List.GRASS, Texture_List.DIRT, cutoff);
				}
				case STONE:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE, cutoff);
				}
				case DIRT:{
					return CM.get_texture_coords(faces, num, Texture_List.DIRT, cutoff);
				}
				case PLANKS:{
					return CM.get_texture_coords(faces, num, Texture_List.PLANKS, cutoff);
				}
				case STONE_BRICK:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE_BRICK, cutoff);
				}
				case BRICKS:{
					return CM.get_texture_coords(faces, num, Texture_List.BRICKS, cutoff);
				}
				case TNT:{
					return CM.get_texture_coords(faces, num, Texture_List.TNT_SIDE,Texture_List.TNT_TOP,Texture_List.TNT_BOT, cutoff);
				}
				case COBBLESTONE:{
					return CM.get_texture_coords(faces, num, Texture_List.COBBLESTONE, cutoff);
				}
				case BEDROCK:{
					return CM.get_texture_coords(faces, num, Texture_List.BEDROCK, cutoff);
				}
				case SAND:{
					return CM.get_texture_coords(faces, num, Texture_List.SAND, cutoff);
				}
				case GRAVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.GRAVEL, cutoff);
				}
				case OAK_LOG:{
					return CM.get_texture_coords(faces, num, Texture_List.OAK_LOG_SIDE, Texture_List.OAK_LOG_CAP, cutoff);
				}
				case OAK_LEAF:{
					return CM.get_texture_coords(faces, num, Texture_List.OAK_LEAFS, cutoff);
				}
				case ORE_GOLD:{
					return CM.get_texture_coords(faces, num, Texture_List.ORE_GOLD, cutoff);
				}
				case ORE_IRON:{
					return CM.get_texture_coords(faces, num, Texture_List.ORE_IRON, cutoff);
				}
				case ORE_COAL:{
					return CM.get_texture_coords(faces, num, Texture_List.ORE_COAL, cutoff);
				}
				case ORE_DIAMOND:{
					return CM.get_texture_coords(faces, num, Texture_List.ORE_DIAMOND, cutoff);
				}
				case ORE_REDSTONE:{
					return CM.get_texture_coords(faces, num, Texture_List.ORE_REDSTONE, cutoff);
				}
				case ORE_LAPIS:{
					return CM.get_texture_coords(faces, num, Texture_List.ORE_LAPIS, cutoff);
				}
				case COBWEB:{
					return CM.get_texture_coords(faces, num, Texture_List.COBWEB, cutoff);
				}
				case WATER:{
					return CM.get_texture_coords(faces, num, Texture_List.WATER, cutoff);
				}
				case MOSSY_COBBLESTONE:{
					return CM.get_texture_coords(faces, num, Texture_List.MOSSY_COBBLESTONE, cutoff);
				}
				case WORKBENCH:{
					return CM.get_texture_coords(faces, num, Texture_List.WORKBENCH_SIDE, Texture_List.WORKBENCH_TOP, Texture_List.WORKBENCH_BOT, cutoff);
				}
				case SWORD:{
					return CM.get_texture_coords(faces, num, Texture_List.SWORD, cutoff);
				}
				case PICKAXE:{
					return CM.get_texture_coords(faces, num, Texture_List.PICKAXE, cutoff);
				}
				case AXE:{
					return CM.get_texture_coords(faces, num, Texture_List.AXE, cutoff);
				}
				case SHOVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.SHOVEL, cutoff);
				}
				default:{
					return CM.get_texture_coords(faces, num, Texture_List.UNKNOWN, cutoff);
				}
			}
		}
		return null;
	}
	
	public FloatBuffer get_vertices() {
		return vertices;
	}

	public FloatBuffer get_coords() {
		return texture;
	}
	
	public FloatBuffer get_normals() {
		return normals;
	}
	
	public void Use(Player player,Chunk Ch){
		switch(this.block_id){
			case OAK_LOG:{
				Ch.put_block_in_chunk(Block.WORKBENCH, this.position);
			} break;
			case WORKBENCH:{
				player.open_block_interface(this.usable);
			}break;
			default:{
				
			}
		}
	}

	public void draw(GL2 gl, float size){
		int x = 0;
		int y = 0;
		int z = 0;
		float x1 = size;
		float y1 = size;
		float z1 = size;
		
		if(Block.is_tool(this.block_id)){
			float vert[] = {x,y1,z1, x,y,z1, x1,y,z1, x1,y1,z1}; // front
			FloatBuffer vertices = Buffers.newDirectFloatBuffer(12);
			for (int j = 0; j < 12; j++){
				vertices.put(vert[j]);
			}
			vertices.rewind();
			FloatBuffer texture = this.set_texture(true);

			gl.glTranslatef(-size/2.0f,-size/2.0f,-size);
			
			gl.glEnable(GL2.GL_BLEND);
			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
			gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texture);
			gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, material_color, 0);
			gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
			gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL2.GL_BLEND);			
		}else{
			float vert[] = {x,y1,z1, x,y,z1, x1,y,z1, x1,y1,z1, x1,y1,z1, x1,y,z1, x1,y,z, x1,y1,z,  // front, right       // 6x4x3 of vertex coords
					x1,y1,z, x1,y,z, x,y,z, x,y1,z, x,y1,z, x,y,z, x,y,z1, x,y1,z1,  // back, left
					x,y1,z, x,y1,z1, x1,y1,z1, x1,y1,z, x1,y,z, x1,y,z1, x,y,z1, x,y,z}; // top, bot
			FloatBuffer vertices = Buffers.newDirectFloatBuffer(72);
			for (int i = 0; i < 6; i++){
				for (int j = 0; j < 12; j++){
					vertices.put(vert[i*12+j]);
				}
			}
			vertices.rewind();
			FloatBuffer texture = this.set_texture(true);
			if(Block.is_transparent(block_id)){
				gl.glEnable(GL2.GL_BLEND);
			}
			gl.glTranslatef(-size/2.0f,-size/2.0f,-size/2.0f);
			
			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
			gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texture);
			gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, material_color, 0);
			gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
			gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL2.GL_BLEND);
		}
	}

	public int get_draw_faces_num() {
		return faces_to_draw;
	}

	public byte get_visibility() {
		return this.visibility_lvl;
	}
	
	public byte get_solidity() {
		return this.solidity_lvl;
	}
	
	public void attach_floiage(Foliage f){
		this.attached_foliage = f;
	}
	
	public Foliage get_floiage(){
		return this.attached_foliage;
	}
	
	public static int harvest_lvl(int id){
		/*
		 * -1 non.harvestabe
		 *  0 general - any or none tool
		 *  1 stone - pickaxe
		 *  2 dirt - shovel
		 *  3 wood - axe
		 */
		switch(id){
			case GRASS:{
				return 2;
			}
			case STONE:{
				return 1;
			}
			case DIRT:{
				return 2;
			}
			case PLANKS:{
				return 3;
			}
			case STONE_BRICK:{
				return 1;
			}
			case BRICKS:{
				return 1;
			}
			case TNT:{
				return 0;
			}
			case COBBLESTONE:{
				return 1;
			}
			case BEDROCK:{
				return -1;
			}
			case SAND:{
				return 2;
			}
			case GRAVEL:{
				return 2;
			}
			case OAK_LOG:{
				return 3;
			}
			case OAK_LEAF:{
				return 0;
			}
			case ORE_GOLD:{
				return 1;
			}
			case ORE_IRON:{
				return 1;
			}
			case ORE_COAL:{
				return 1;
			}
			case ORE_DIAMOND:{
				return 1;
			}
			case ORE_REDSTONE:{
				return 1;
			}
			case ORE_LAPIS:{
				return 1;
			}
			case COBWEB:{
				return 0;
			}
			case WATER:{
				return -1;
			}
			case MOSSY_COBBLESTONE:{
				return 1;
			}
			case WORKBENCH:{
				return 3;
			}
			case -1:{
				return -1;
			}
			default:{
				return 0;
			}
		}
	}

	public static int mining_lvl(int id){
		/*
		 * -1 non.harvestabe
		 *  0 general - any or none tool
		 *  1 stone - pickaxe
		 *  2 dirt - shovel
		 *  3 wood - axe
		 */
		switch(id){
			case PICKAXE:{
				return 1;
			}
			case SHOVEL:{
				return 2;
			}
			case AXE:{
				return 3;
			}
			default:{
				return 0;
			}
		}
	}
	
	public static boolean is_placeable(int id){
		switch(id){
			case PICKAXE:{
				return false;
			}
			case SHOVEL:{
				return false;
			}
			case AXE:{
				return false;
			}
			case SWORD:{
				return false;
			}
			case WATER:{
				return false;
			}
			case -1:{
				return false;
			}
			default:{
				return true;
			}
		}
	}

	public static int dmg(int id){
		switch(id){
			case SWORD:{
				return 5;
			}
			case SHOVEL:{
				return 2;
			}
			case AXE:{
				return 3;
			}
			case PICKAXE:{
				return 2;
			}
			default:{
				return 1;
			}
		}
	}
	
	public static boolean is_transparent(int id){
		switch(id){
			case SWORD:{
				return true;
			}
			case SHOVEL:{
				return true;
			}
			case AXE:{
				return true;
			}
			case PICKAXE:{
				return true;
			}
			case WATER:{
				return true;
			}
			case COBWEB:{
				return true;
			}
			default:{
				return false;
			}
		}
	}
	

	public static boolean is_tool(int id){
		switch(id){
			case SWORD:{
				return true;
			}
			case SHOVEL:{
				return true;
			}
			case AXE:{
				return true;
			}
			case PICKAXE:{
				return true;
			}
			default:{
				return false;
			}
		}
	}
	
	public static boolean is_usable(int id){
		switch(id){
			case OAK_LOG:{
				return true;
			}
			case WORKBENCH:{
				return true;
			}
			default:{
				return false;
			}
		}
	}

	public void setup_usable() {
		switch(this.block_id){
			case WORKBENCH:{
				this.usable = new Workbench();
			}break;
			default:{
				this.usable = null;
			}
		}
	}
}
