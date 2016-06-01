package pkg.skapik.icp.assets;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL2;
import com.jogamp.common.nio.Buffers;
import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Texture_List;
import pkg.skapik.icp.func.Coords_Manager;

public class Block {
	
	public static final int GRASS = 0;
	public static final int STONE = 1;
	public static final int STONE__2 = 222;
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
	public static final int OAK_LEAF = 23;
	public static final int PALM_LOG = 28;
	public static final int PALM_LEAF = 29;
	public static final int SPRUCE_LOG = 30;
	public static final int SPRUCE_LEAF = 31;
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
	public static final int COBWEB = 24;
	public static final int WATER = 25;
	public static final int MOSSY_COBBLESTONE = 26;
	public static final int WORKBENCH = 27;
	
	public static final int WOODEN_PICKAXE = 100;
	public static final int WOODEN_SHOVEL = 101;
	public static final int WOODEN_AXE = 102;
	public static final int WOODEN_SWORD = 103;
	public static final int WOODEN_HAMMER = 104;
	
	public static final int STONE_PICKAXE = 105;
	public static final int STONE_SHOVEL = 106;
	public static final int STONE_AXE = 107;
	public static final int STONE_SWORD = 108;
	public static final int STONE_HAMMER = 109;
	
	public static final int IRON_PICKAXE = 110;
	public static final int IRON_SHOVEL = 111;
	public static final int IRON_AXE = 112;
	public static final int IRON_SWORD = 113;
	public static final int IRON_HAMMER = 114;
	
	public static final int DIAMOND_PICKAXE = 115;
	public static final int DIAMOND_SHOVEL = 116;
	public static final int DIAMOND_AXE = 117;
	public static final int DIAMOND_SWORD = 118;
	public static final int DIAMOND_HAMMER = 119;
	
	public static final int GOLD_PICKAXE = 120;
	public static final int GOLD_SHOVEL = 121;
	public static final int GOLD_AXE = 122;
	public static final int GOLD_SWORD = 123;
	public static final int GOLD_HAMMER = 124;
	
	public static final int ROCK = 200;
	public static final int COAL = 201;
	public static final int IRON_SHARD = 202;
	public static final int GOLD_SHARD = 203;
	public static final int DIAMOND = 204;
	public static final int LAPIS = 205;
	public static final int REDSTONE = 206;
	public static final int WOODEN_ROD = 207;
	
	private Coords_Manager CM;
	private Position position;
	private float[] material_color;
	private FloatBuffer texture;
	private FloatBuffer vertices;
	private FloatBuffer normals;
	//private IntBuffer indices;
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
				case PALM_LOG:{
					return CM.get_texture_coords(faces, num, Texture_List.PALM_LOG_SIDE, Texture_List.PALM_LOG_CAP, cutoff);
				}
				case SPRUCE_LOG:{
					return CM.get_texture_coords(faces, num, Texture_List.SPRUCE_LOG_SIDE, Texture_List.SPRUCE_LOG_CAP, cutoff);
				}
				case SPRUCE_LEAF:{
					return CM.get_texture_coords(faces, num, Texture_List.SPRUCE_LEAFS, cutoff);
				}
				case PALM_LEAF:{
					return CM.get_texture_coords(faces, num, Texture_List.PALM_LEAFS, cutoff);
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
				case WOODEN_SWORD:{
					return CM.get_texture_coords(faces, num, Texture_List.WOODEN_SWORD, cutoff);
				}
				case WOODEN_PICKAXE:{
					return CM.get_texture_coords(faces, num, Texture_List.WOODEN_PICKAXE, cutoff);
				}
				case WOODEN_AXE:{
					return CM.get_texture_coords(faces, num, Texture_List.WOODEN_AXE, cutoff);
				}
				case WOODEN_SHOVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.WOODEN_SHOVEL, cutoff);
				}
				case WOODEN_HAMMER:{
					return CM.get_texture_coords(faces, num, Texture_List.WOODEN_HAMMER, cutoff);
				}
				case STONE_SWORD:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE_SWORD, cutoff);
				}
				case STONE_PICKAXE:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE_PICKAXE, cutoff);
				}
				case STONE_AXE:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE_AXE, cutoff);
				}
				case STONE_SHOVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE_SHOVEL, cutoff);
				}
				case STONE_HAMMER:{
					return CM.get_texture_coords(faces, num, Texture_List.STONE_HAMMER, cutoff);
				}
				case IRON_SWORD:{
					return CM.get_texture_coords(faces, num, Texture_List.IRON_SWORD, cutoff);
				}
				case IRON_PICKAXE:{
					return CM.get_texture_coords(faces, num, Texture_List.IRON_PICKAXE, cutoff);
				}
				case IRON_AXE:{
					return CM.get_texture_coords(faces, num, Texture_List.IRON_AXE, cutoff);
				}
				case IRON_SHOVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.IRON_SHOVEL, cutoff);
				}
				case IRON_HAMMER:{
					return CM.get_texture_coords(faces, num, Texture_List.IRON_HAMMER, cutoff);
				}
				case DIAMOND_SWORD:{
					return CM.get_texture_coords(faces, num, Texture_List.DIAMOND_SWORD, cutoff);
				}
				case DIAMOND_PICKAXE:{
					return CM.get_texture_coords(faces, num, Texture_List.DIAMOND_PICKAXE, cutoff);
				}
				case DIAMOND_AXE:{
					return CM.get_texture_coords(faces, num, Texture_List.DIAMOND_AXE, cutoff);
				}
				case DIAMOND_SHOVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.DIAMOND_SHOVEL, cutoff);
				}
				case DIAMOND_HAMMER:{
					return CM.get_texture_coords(faces, num, Texture_List.DIAMOND_HAMMER, cutoff);
				}
				case GOLD_SWORD:{
					return CM.get_texture_coords(faces, num, Texture_List.GOLD_SWORD, cutoff);
				}
				case GOLD_PICKAXE:{
					return CM.get_texture_coords(faces, num, Texture_List.GOLD_PICKAXE, cutoff);
				}
				case GOLD_AXE:{
					return CM.get_texture_coords(faces, num, Texture_List.GOLD_AXE, cutoff);
				}
				case GOLD_SHOVEL:{
					return CM.get_texture_coords(faces, num, Texture_List.GOLD_SHOVEL, cutoff);
				}
				case GOLD_HAMMER:{
					return CM.get_texture_coords(faces, num, Texture_List.GOLD_HAMMER, cutoff);
				}
				case ROCK:{
					return CM.get_texture_coords(faces, num, Texture_List.ROCK, cutoff);
				}
				case COAL:{
					return CM.get_texture_coords(faces, num, Texture_List.COAL, cutoff);
				}
				case IRON_SHARD:{
					return CM.get_texture_coords(faces, num, Texture_List.IRON_SHARD, cutoff);
				}
				case GOLD_SHARD:{
					return CM.get_texture_coords(faces, num, Texture_List.GOLD_SHARD, cutoff);
				}
				case DIAMOND:{
					return CM.get_texture_coords(faces, num, Texture_List.DIAMOND, cutoff);
				}
				case LAPIS:{
					return CM.get_texture_coords(faces, num, Texture_List.LAPIS, cutoff);
				}
				case REDSTONE:{
					return CM.get_texture_coords(faces, num, Texture_List.REDSTONE, cutoff);
				}
				case WOODEN_ROD:{
					return CM.get_texture_coords(faces, num, Texture_List.WOODEN_ROD, cutoff);
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
			case PALM_LOG:{
				return 0;
			}
			case PALM_LEAF:{
				return 0;
			}
			case SPRUCE_LOG:{
				return 3;
			}
			case SPRUCE_LEAF:{
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
			case WOODEN_PICKAXE:
			case STONE_PICKAXE:
			case IRON_PICKAXE:
			case DIAMOND_PICKAXE:{
				return 1;
			}
			case WOODEN_SHOVEL:
			case STONE_SHOVEL:
			case IRON_SHOVEL:
			case DIAMOND_SHOVEL:{
				return 2;
			}
			case WOODEN_AXE:
			case STONE_AXE:
			case IRON_AXE:
			case DIAMOND_AXE:{
				return 3;
			}
			default:{
				return 0;
			}
		}
	}
	
	public static int crushing_lvl(int id){
		/*
		 * -1 non-crushable
		 *  0 wooden hammer
		 *  1 stone hammer
		 *  2 iron hammer
		 *  3 diamond hammer
		 */
		switch(id){
			case WOODEN_HAMMER:{
				return 0;
			}
			case STONE_HAMMER:{
				return 1;
			}
			case IRON_HAMMER:{
				return 2;
			}
			case DIAMOND_HAMMER:{
				return 3;
			}
			default:{
				return -1;
			}
		}
	}
	
	public static int crush_lvl(int id){
		/*
		 * -1 non-crushable
		 *  0 wooden hammer
		 *  1 stone hammer
		 *  2 iron hammer
		 *  3 diamond hammer
		 */
		switch(id){
			case STONE:
			case ORE_COAL:
			case COBBLESTONE:
			case MOSSY_COBBLESTONE:{
				return 0;
			}
			case ORE_IRON:{
				return 1;
			}
			case ORE_GOLD:
			case ORE_DIAMOND:{
				return 2;
			}
			case ORE_LAPIS:
			case ORE_REDSTONE:{
				return 3;
			}
			default:{
				return -1;
			}
		}
	}
	
	public static boolean is_placeable(int id){
		switch(id){
			case WOODEN_PICKAXE:
			case WOODEN_SHOVEL:
			case WOODEN_AXE:
			case WOODEN_SWORD:
			case WOODEN_HAMMER:
			case STONE_PICKAXE:
			case STONE_SHOVEL:
			case STONE_AXE:
			case STONE_SWORD:
			case STONE_HAMMER:
			case IRON_PICKAXE:
			case IRON_SHOVEL:
			case IRON_AXE:
			case IRON_SWORD:
			case IRON_HAMMER:
			case DIAMOND_PICKAXE:
			case DIAMOND_SHOVEL:
			case DIAMOND_AXE:
			case DIAMOND_SWORD:
			case DIAMOND_HAMMER:
			case GOLD_PICKAXE:
			case GOLD_SHOVEL:
			case GOLD_AXE:
			case GOLD_SWORD:
			case GOLD_HAMMER:
			case WATER:
			case ROCK:
			case COAL:
			case IRON_SHARD:
			case GOLD_SHARD:
			case DIAMOND:
			case LAPIS:
			case REDSTONE:
			case WOODEN_ROD:
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
			case DIAMOND_SWORD:{
				return 10;
			}
			case DIAMOND_PICKAXE:
			case DIAMOND_SHOVEL:
			case DIAMOND_AXE:
			case DIAMOND_HAMMER:
			case IRON_SWORD:{
				return 5;
			}
			case IRON_AXE:
			case IRON_HAMMER:{
				return 4;
			}
			case WOODEN_SWORD:
			case IRON_SHOVEL:
			case IRON_PICKAXE:
			case STONE_SWORD:{
				return 3;
			}
			case WOODEN_AXE:
			case STONE_AXE:
			case STONE_SHOVEL:
			case STONE_HAMMER:
			case STONE_PICKAXE:{
				return 2;
			}
			default:{
				return 1;
			}
		}
	}
	
	public static boolean is_transparent(int id){
		switch(id){
			case WOODEN_PICKAXE:
			case WOODEN_SHOVEL:
			case WOODEN_AXE:
			case WOODEN_SWORD:
			case WOODEN_HAMMER:
			case STONE_PICKAXE:
			case STONE_SHOVEL:
			case STONE_AXE:
			case STONE_SWORD:
			case STONE_HAMMER:
			case IRON_PICKAXE:
			case IRON_SHOVEL:
			case IRON_AXE:
			case IRON_SWORD:
			case IRON_HAMMER:
			case DIAMOND_PICKAXE:
			case DIAMOND_SHOVEL:
			case DIAMOND_AXE:
			case DIAMOND_SWORD:
			case DIAMOND_HAMMER:
			case GOLD_PICKAXE:
			case GOLD_SHOVEL:
			case GOLD_AXE:
			case GOLD_SWORD:
			case GOLD_HAMMER:
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
			case WOODEN_PICKAXE:
			case WOODEN_SHOVEL:
			case WOODEN_AXE:
			case WOODEN_SWORD:
			case WOODEN_HAMMER:
			case STONE_PICKAXE:
			case STONE_SHOVEL:
			case STONE_AXE:
			case STONE_SWORD:
			case STONE_HAMMER:
			case IRON_PICKAXE:
			case IRON_SHOVEL:
			case IRON_AXE:
			case IRON_SWORD:
			case IRON_HAMMER:
			case DIAMOND_PICKAXE:
			case DIAMOND_SHOVEL:
			case DIAMOND_AXE:
			case DIAMOND_SWORD:
			case DIAMOND_HAMMER:
			case GOLD_PICKAXE:
			case GOLD_SHOVEL:
			case GOLD_AXE:
			case GOLD_SWORD:
			case GOLD_HAMMER:
			case ROCK:
			case COAL:
			case IRON_SHARD:
			case GOLD_SHARD:
			case DIAMOND:
			case LAPIS:
			case REDSTONE:
			case WOODEN_ROD:{
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

	public static int[] get_crush_drop(int id) {
		Random rnd = new Random();
		int[] drop;
		switch(id){
			case STONE:
			case COBBLESTONE:
			case MOSSY_COBBLESTONE:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1)};
			}break;
			case ORE_COAL:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1),COAL,(rnd.nextInt(3)+1)};
			}break;
			case ORE_IRON:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1),IRON_SHARD,(rnd.nextInt(3)+1)};
			}break;
			case ORE_GOLD:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1),GOLD_SHARD,(rnd.nextInt(3)+1)};
			}break;
			case ORE_DIAMOND:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1),DIAMOND,1};
			}break;
			case ORE_LAPIS:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1),LAPIS,(rnd.nextInt(1)+1)};
			}break;
			case ORE_REDSTONE:{
				drop = new int[]{ROCK,(rnd.nextInt(4)+1),REDSTONE,(rnd.nextInt(1)+1)};
			}break;
			default:{
				drop = null;
			}
		}
		rnd = null;
		return drop;
	}
}
