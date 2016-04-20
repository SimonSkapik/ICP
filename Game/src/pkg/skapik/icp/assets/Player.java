package pkg.skapik.icp.assets;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;

import pkg.skapik.icp.func.Boundery_box;
import pkg.skapik.icp.func.Collision_detector;
import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Move_state;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Renderer;
import pkg.skapik.icp.func.Vector;

public class Player implements Creature{

	private GLUT glut;
	
	private Position position;
	private Vector direction;
	private Renderer renderer;
	private Move_state motion;
	private Boundery_box boundery;
	private int old_x,old_y;
	private Point current_chunk;
	private float speed;
	private float reach;
	private int view_distance;
	
	private int[] hotbar;
	private int[] hotbar_count;
	private int[] inventory;
	private int[] inventory_count;
	private int inventory_holding;
	private int inventory_hover;
	private int hbar_select;
	private int inv_off_x;
	private int inv_off_y;
	private int inv_win_x;
	private int inv_win_y;
	private int inv_slot_x;
	private int inv_slot_y;
	private int inv_item_x;
	private int inv_item_y;
	private boolean in_inventory;
	private int hbar_off_x;
	private int hbar_off_y;
	private int hbar_win_x;
	private int hbar_win_y;
	private int hbar_item_x;
	private int hbar_slot_x;
	private int[] target;
	private int[] target_count;
	private int[] source;
	private int[] source_count;
	private int hotbar_hover;
	private int health;
	private float attack_range;
	private int draw_list;
	private boolean alive;
	private int hand_move;
	private Usable block_in_use;
	private float[] rigor_mortis;
	private boolean using_block;
	private boolean cam_enabled;
	private int hand_dir;
	
	public Player(Renderer renderer){
		this.renderer = renderer;
		this.motion = new Move_state(this);
		this.current_chunk = new Point(0,0);
		this.view_distance = 7;
		this.speed = 0.2f;
		this.reach = 7;
		this.attack_range = 2.5f;
		this.rigor_mortis = new float[12];
		this.old_x = -1;
		this.old_y = -1;
		this.hand_move = 0;
		this.glut = new GLUT();
		this.in_inventory = false;
		this.draw_list = -1;
		this.cam_enabled = false;
		
		this.inventory_holding = -1;
		this.inventory_hover = -1;
		this.hotbar_hover = -1;
		this.boundery = new Boundery_box(0.75f,1.8f,0.75f,0.5f,0.9f,0.5f);
		init();
	}
	
	private void init(){
		File f = new File("./World/Player/save.txt");
		if(f.exists()) { 
			try(BufferedReader bufferedReader = new BufferedReader(new FileReader("./World/Player/save.txt"))) {
	            String line;
	            String[] nums;
	            line = bufferedReader.readLine();
                nums = line.split(";");
                this.position = new Position(Double.parseDouble(nums[0]),Double.parseDouble(nums[1]),Double.parseDouble(nums[2]));
                line = bufferedReader.readLine();
                nums = line.split(";");
                this.using_block = false;
                this.block_in_use = null;
                this.health = Integer.parseInt(nums[0]);
                this.hbar_select = Integer.parseInt(nums[1]);
    			this.alive = Boolean.parseBoolean(nums[2]);
    			line = bufferedReader.readLine();
                nums = line.split(";");
                this.hotbar = new int[9];
                this.hotbar_count = new int[9];
                for(int i = 0; i < 9; i++){
                	hotbar[i] = Integer.parseInt(nums[i*2]);
                	hotbar_count[i] = Integer.parseInt(nums[i*2+1]);
                }
                line = bufferedReader.readLine();
                nums = line.split(";");
                this.inventory = new int[36];
                this.inventory_count = new int[36];
                for(int i = 0; i < 36; i++){
                	inventory[i] = Integer.parseInt(nums[i*2]);
                	inventory_count[i] = Integer.parseInt(nums[i*2+1]);
                }
	            bufferedReader.close();     
	        }catch(FileNotFoundException ex) {
	            //System.out.println("Unable to open file '" + "./World/Chunks/"+this.x+"_"+this.z+".txt" + "'");                
	        }catch(IOException ex) {
	            System.out.println("Error reading file '" + "./World/Chunks/Player/save.txt" + "'");                  
	        }
		}else{
			this.position = new Position(1,50,1);
			this.hbar_select = 0;
			this.health = 20;
			this.alive = true;
			this.hotbar = new int[]{Block.SWORD, Block.PICKAXE, Block.AXE, Block.SHOVEL, -1, -1, -1, -1, -1};
			this.hotbar_count = new int[]{1, 1, 1, 1, 0, 0, 0, 0, 0};
			this.inventory = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1,
										-1, -1, -1, -1, -1, -1, -1, -1, -1};
			this.inventory_count = new int[]{0,0,0,0,0,0,0,0,0,
										0,0,0,0,0,0,0,0,0,
										0,0,0,0,0,0,0,0,0,
										0,0,0,0,0,0,0,0,0};
		}
		this.direction = new Vector(0,0,-1);
	}
	
	public void toggle_cam(){
		this.cam_enabled = !this.cam_enabled;
	}
	
	public boolean cam_enabled(){
		return cam_enabled;
	}
	
	public void set_draw_list(int draw_list){
		this.draw_list = draw_list;
	}
	
	public void hud_init(){
		inv_off_x = (int)(renderer.width*0.2395);
		inv_off_y = (int)(renderer.height*0.1824);
		inv_win_x = (int)(renderer.width*0.523);
		inv_win_y = (int)(renderer.height*0.4361);
		inv_slot_x = (int)(renderer.width*0.0589);
		inv_slot_y = (int)(renderer.height*0.1148);
		inv_item_x = (int)(renderer.width*0.05);
		inv_item_y = (int)(renderer.height*0.0926);
		hbar_off_x = (int)(renderer.width*0.3146);
		hbar_off_y = (int)(renderer.height*0.9157);
		hbar_win_x = (int)(renderer.width*0.3716);
		hbar_win_y = (int)(renderer.height*0.0704);
		hbar_slot_x = (int)(renderer.width*0.0417);
		hbar_item_x = (int)(renderer.width*0.038);
	}
	
	public void draw_boundery(GL2 gl){
		float vert[] = {0,0.03f,0, 0,0.03f,0.75f, 0.75f,0.03f,0.75f, 0.75f,0.03f,0};  // back, front 0.37f,0.05f,0.37f, 0.37f,0.05f,0.38f, 0.38f,0.05f,0.38f, 0.38f,0.05f,0.37f
		FloatBuffer vertices = Buffers.newDirectFloatBuffer(vert.length);
		for (int i = 0; i < vert.length; i++){
			vertices.put(vert[i]);
		}
		vertices.rewind();
		
		float vert2[] = {0.37f,0.05f,0.37f, 0.37f,0.05f,0.38f, 0.38f,0.05f,0.38f, 0.38f,0.05f,0.37f};  // back, front 
		FloatBuffer vertices2 = Buffers.newDirectFloatBuffer(vert.length);
		for (int i = 0; i < vert2.length; i++){
			vertices2.put(vert2[i]);
		}
		vertices2.rewind();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
 	    //
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("red"), 0);
 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices2);
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4); 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnable(GL2.GL_TEXTURE_2D);
	}
	
	private int[] get_free_spot(int id){
		int[] spot = new int[]{0,0};
		for(int i = 0; i < 9; i++){
			if(hotbar[i] == id){
				spot[0] = 1;
				spot[1] = i;
				return spot;
			}
		}
		for(int i = 0; i < 36; i++){
			if(inventory[i] == id){
				spot[0] = 2;
				spot[1] = i;
				return spot;
			}
		}
		for(int i = 0; i < 9; i++){
			if(hotbar[i] < 0){
				spot[0] = 1;
				spot[1] = i;
				return spot;
			}
		}
		for(int i = 0; i < 36; i++){
			if(inventory[i] < 0){
				spot[0] = 2;
				spot[1] = i;
				return spot;
			}
		}
		return spot;
	}

	public void Controll(int key_code, int action) {
		if(alive){
			motion.Move(key_code, action);
		}
	}
	
	public void Look(int new_x, int new_y) {
		if(alive){
			float sensitivity = 0.01f;
			int d_x = (new_x - this.old_x);
			int d_y = (new_y - this.old_y);
	
			float angle_x  = 0.0f;				
			float angle_y  = 0.0f;							
			angle_x = (float)( (d_x) ) / 500.0f;
			angle_y = -(float)( (d_y) ) / 500.0f;
	
			Vector direction_normal = new Vector(-this.direction.getZ_F(),0,this.direction.getX_F());
	
			float tmp_y = this.direction.getY_F();
			this.direction.setY(0);
			this.direction.rotate(Vector.Y_AXIS, angle_x);
			this.direction.setY(tmp_y);
			this.direction.normalize();
			if(angle_y < 0){
				if(direction.getY_D() > -0.99){
					this.direction.rotate(direction_normal , angle_y);
				}
			}else{
				if(direction.getY_D() < 0.99){
					this.direction.rotate(direction_normal , angle_y);
				}
			}
		}
	}

	public void Update() {
		if(alive){
			Vector dir;
			if(motion.is_moving()){
				dir = new Vector(this.get_move_direction());
			}else{
				dir = new Vector(0,0,0);
			}
			this.Move(dir, speed);
		}
	}
	
	private void Move(Vector dir, float speed){
		if(alive){
			float env_resist = 1;
			if(is_in_water()){
				env_resist = 1.0f/3.0f;
			}
			double wanted_x = dir.getX_D()*speed*env_resist;
			int sig_x = (int)Math.signum(wanted_x);
			double wanted_z = dir.getZ_D()*speed*env_resist;
			int sig_z = (int)Math.signum(wanted_z);
			double wanted_y = motion.get_gravity_vector().getY_D()*speed*env_resist;
			int sig_y = (int)Math.signum(wanted_y);
			//double new_x = dir.getX_D()*speed;
			
			Collision_detector CD = new Collision_detector(renderer, this);
			float[] collisions = CD.check_collision(0);
			CD = null;
			
			if(sig_y > 0){
				if(collisions[3] == 0){
					position.setY(position.getY_D()+wanted_y);
					motion.update_gravity(0);
				}else{
					if((position.getY_D()+wanted_y) > collisions[7]){
						position.setY(collisions[7]);
						motion.update_gravity(1);
					}else{
						position.setY(position.getY_D()+wanted_y);
						motion.update_gravity(0);
					}
				}
			}else if(sig_y < 0){
				if(collisions[2] == 0){
					position.setY(position.getY_D()+wanted_y);
					motion.update_gravity(0);
				}else{
					if((position.getY_D()+wanted_y) < collisions[6]){
						position.setY(collisions[6]);
						motion.update_gravity(1);
					}else{
						position.setY(position.getY_D()+wanted_y);
						motion.update_gravity(0);
					}
				}
			}else{
				if(collisions[2] == 0){
					motion.update_gravity(0);
				}else{
					if(collisions[6] < position.getY_D()){
						motion.update_gravity(0);
					}
				}
			}
			
			
			if(collisions[0] < 2){
				position.setX(position.getX_D()+wanted_x);
			}else{
				if(sig_x > 0){
					if((position.getX_D()+wanted_x) > collisions[4]){
						position.setX(collisions[4]);
					}else{
						position.setX(position.getX_D()+wanted_x);
					}
				}else{
					if((position.getX_D()+wanted_x) < collisions[4]){
						position.setX(collisions[4]);
					}else{
						position.setX(position.getX_D()+wanted_x);
					}
				}
			}
			
			if(collisions[1] < 2){
				position.setZ(position.getZ_D()+wanted_z);
			}else{
				if(sig_z > 0){
					if((position.getZ_D()+wanted_z) > collisions[5]){
						position.setZ(collisions[5]);
					}else{
						position.setZ(position.getZ_D()+wanted_z);
					}
				}else{
					if((position.getZ_D()+wanted_z) < collisions[5]){
						position.setZ(collisions[5]);
					}else{
						position.setZ(position.getZ_D()+wanted_z);
					}
				}
			}
			
			boolean step = false;
			if(collisions[1] == 1){
				if(sig_z > 0){
					if((position.getZ_D()+wanted_z) > collisions[5]){
						step = true;
					}
				}else if(sig_z < 0){
					if((position.getZ_D()+wanted_z) < collisions[5]){
						step = true;
					}
				}
			}
			if(collisions[0] == 1){
				if(sig_x > 0){
					if((position.getX_D()+wanted_x) > collisions[4]){
						step = true;
					}
				}else if(sig_x < 0){
					if((position.getX_D()+wanted_x) < collisions[4]){
						step = true;
					}
				}
			}
	
			if(step)
				this.position.setY(this.position.getY_D() + 1);
			
			this.check_chunk();
		}
	}
	
	private void check_chunk() {
		int new_ch_x = position.getX_I()/16;
		int new_ch_z = position.getZ_I()/16;
		if(new_ch_x != current_chunk.x || new_ch_z != current_chunk.y){
			current_chunk.x = new_ch_x;
			current_chunk.y = new_ch_z;
			renderer.change_defaut_chunk(new_ch_x,new_ch_z);
		}
	}

	public Position get_position(){
		return this.position;
	}

	public Vector get_view_direction(){
		return this.direction;
	}
	
	public Vector get_move_direction(){
		Vector dir = new Vector(this.direction);
		//dir.setY(0);
		dir.normalize();
		dir.rotate(Vector.Y_AXIS, motion.get_angle_deviation());
		return dir;
	}

	public Vector get_gravity_vector(){
		return motion.get_gravity_vector();
	}

	public void init_view_coords(int x, int y) {
		this.old_x = x;
		this.old_y = y;
	}

	public Point get_current_chunk() {
		return this.current_chunk;
	}

	public int get_view_distance() {
		return this.view_distance;
	}

	public Boundery_box get_boundery() {
		return this.boundery;
	}

	public void Hit() {
		if(alive){
			if(hand_move == 0){
				boolean enemy_hit = false;
				for(Creature C : renderer.get_creatures()){
					if(this.position.distance_to_3d(C.get_position()) <= attack_range){
						Vector to_enemy = new Vector(new Vector(C.get_position().getX_D()-this.position.getX_D(), 0, C.get_position().getZ_D()-this.position.getZ_D()));
						float angle = Math.abs((float) ((this.direction.get_angle_to(to_enemy)*180)/Math.PI));
						if(angle < 60){
							C.Get_hit(Block.dmg(hotbar[hbar_select]),this.direction);
							enemy_hit = true;
							break;
						}
					}
				}
				if(!enemy_hit){
					Position[] blocks = renderer.get_bloc_in_direction(this.position,this.direction,this.reach);
					int id = renderer.get_block_id(blocks[0]);
					if(Block.harvest_lvl(id) == 0 || Block.harvest_lvl(id) == Block.mining_lvl(hotbar[hbar_select]) ){
						renderer.get_chunk(blocks[0]).remove_block(blocks[0]);
						int[] space = this.get_free_spot(id);
						if(space[0] == 1){
							hotbar[space[1]] = id;
							hotbar_count[space[1]] += 1;
						}else if(space[0] == 2){
							inventory[space[1]] = id;
							inventory_count[space[1]] += 1;
						}
						this.save();
					}
				}
				hand_move=1;
				hand_dir=1;
			}
		}else{
			init();
		}
	}

	public void Use() {
		if(alive){
			Position[] blocks = renderer.get_bloc_in_direction(this.position,this.direction,this.reach);
			Block B = renderer.get_block(blocks[0]);
			if(B.block_id >= 0){
				if(Block.is_usable(B.block_id)){
					B.Use(this,renderer.get_chunk(blocks[0]));
				}else{
					if(blocks[1] != null && Block.is_placeable(hotbar[hbar_select])){
						renderer.get_chunk(blocks[1]).put_block(hotbar[hbar_select], blocks[1]);
						hotbar_count[hbar_select] -= 1; 
						if(hotbar_count[hbar_select] == 0){
							hotbar[hbar_select] = -1;
						}
						this.save();
					}
				}
			}
			hand_move=1;
			hand_dir=1;
		}
	}
	
	public void open_block_interface(Usable block){
		if(alive){
			if(this.using_block){
				this.in_inventory = false;
				this.using_block = false;
				this.block_in_use = null;
			}else{
				this.using_block = true;
				this.in_inventory = true;
				this.block_in_use = block;
			}
			
		}
	}

	private void save() {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./World/Player/save.txt", false)))) {
		    out.println(this.position.getX_D()+";"+this.position.getY_D()+";"+this.position.getZ_D());
		    out.println(this.health+";"+this.hbar_select+";"+this.alive);
		    for(int i = 0; i < 9; i++){
		    	out.print(hotbar[i]+";"+hotbar_count[i]);
		    	if(i < 8)
		    		out.print(";");	
		    }
		    out.println();
		    for(int i = 0; i < 36; i++){
		    	out.print(inventory[i]+";"+inventory_count[i]);
		    	if(i < 35)
		    		out.print(";");	
		    }
		}catch (IOException e) {
			System.err.println("Error writing to file '" + "./World/Player/save.txt" + "'");
		}
		
	}

	public void hbar_select(int i) {
		if(alive){
			this.hbar_select = i;
		}
	}
	
	public void draw_HUD(GL2 gl, int frame){
		if(alive){
			// HUD
			gl.glPushMatrix();
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0.0, renderer.width, renderer.height, 0.0, -1.0, 100.0);
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glLoadIdentity();
	
	        gl.glDepthMask(false);  // disable writes to Z-Buffer
	        gl.glDisable(GL2.GL_DEPTH_TEST); 
	        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
	        
	        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
	 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	 		
	        if(this.in_inventory){
	        	if(this.using_block){
	        		if(this.block_in_use != null){
	        			this.block_in_use.Draw_interface(gl, glut, renderer);
	        		}
	        	}else{
		        	int inv_block_id = -1;
					gl.glPushMatrix();												// Inventory:
					renderer.bind_texture(2);
					gl.glTranslatef(renderer.width*0.5f, renderer.height*0.4f, -50.0f);
					gl.glScaled(renderer.width*0.6, renderer.width*0.6, 1);
					gl.glCallList( renderer.inventory_bg );
					gl.glPopMatrix();
					renderer.bind_texture(1);
					gl.glPushMatrix();	
					gl.glTranslatef(renderer.width*0.2646f, renderer.height*0.228f, -50.0f);
					for(int row = 0; row < 4; row++){
						gl.glPushMatrix();	
						for(int block_id = 1; block_id < 10; block_id++){
							inv_block_id = 9*row+(block_id-1); 
							if(inv_block_id == inventory_holding && source == inventory){
								gl.glPushMatrix();
					        	gl.glScaled(renderer.width*0.05, renderer.width*0.052, 1);
								gl.glTranslated(0, 0, 1);
				        		gl.glCallList( renderer.hbar_bg_hold );
				        		gl.glTranslated(0, 0, -1);
				        		gl.glPopMatrix();
				        	}else if(inv_block_id == inventory_hover){
				        		gl.glPushMatrix();
					        	gl.glScaled(renderer.width*0.05, renderer.width*0.052, 1);
								gl.glTranslated(0, 0, 1);
				        		gl.glCallList( renderer.hbar_bg_sel );
				        		gl.glTranslated(0, 0, -1);
				        		gl.glPopMatrix();
				        	}
				        	if(inventory[inv_block_id] >= 0){	
					        	gl.glPushMatrix();
					        	Block B = new Block(inventory[inv_block_id], this.renderer.get_coords_manager());
					        	
					        	if(Block.is_tool(inventory[inv_block_id])){
					        		gl.glRotatef(180, 0, 1, 0);
					        		if(inv_block_id == inventory_hover || (inv_block_id == inventory_holding && source == inventory)){
					        			B.draw(gl,(int)(renderer.width*0.0511f*-0.55));
					        		}else{
						        		B.draw(gl,(int)(renderer.width*0.0511f*-0.45));
						        	}
					        	}else{
						        	gl.glRotatef(-20, 1, 0, 0);
						        	if(inv_block_id == inventory_hover || (inv_block_id == inventory_holding && source == inventory)){
						        		gl.glRotatef((frame%360), 0, 1, 0);
						        		B.draw(gl,(int)(renderer.width*0.0511f*-0.55));
						        	}else{
						        		gl.glRotatef(45, 0, 1, 0);
						        		B.draw(gl,(int)(renderer.width*0.0511f*-0.45));
						        	}
					        	}
						        
						        gl.glPopMatrix();
						        gl.glPushMatrix();
					        	gl.glTranslatef(renderer.width*0.01f, renderer.width*-0.015f, 49);
					        	gl.glRotatef(180, 1, 0, 0);
					        	gl.glScaled(0.15,0.15,1);
					        	gl.glDisable(GL2.GL_TEXTURE_2D);
					        	gl.glPushMatrix();
					        	gl.glColor3f(1,1,1);
					        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
					        	gl.glLineWidth(3);
					        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(inventory_count[9*row+(block_id-1)]));
					        	gl.glPopMatrix();
					        	gl.glColor3f(0,0,0);
					        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("black"), 0);
					        	gl.glLineWidth(1);
					        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(inventory_count[9*row+(block_id-1)]));
					        	gl.glEnable(GL2.GL_TEXTURE_2D);
						        gl.glPopMatrix();
				        	}
					        gl.glTranslatef(renderer.width*0.0589f, 0, 0);
						}
						gl.glPopMatrix();
						gl.glTranslatef(0, renderer.width*0.0619f, 0);
					}
					gl.glPopMatrix();
	        	}
			}else{
				gl.glPushMatrix();												// Crosshair:
				gl.glTranslatef(renderer.width*0.5f, renderer.height*0.5f, -50.0f);
				gl.glScaled(50, 50, 1);
				gl.glCallList(renderer.crosshair);
		        gl.glPopMatrix();
			}
	        
	
	        gl.glPushMatrix();												// Hotbar:
	        gl.glTranslatef(renderer.width/3.0f, renderer.height*0.95f, -50.0f);
	        float d_x = (renderer.width/3.0f)/8.0f;
	        for(int block_id = 1; block_id < 10; block_id++){
	        	gl.glPushMatrix();
	        	gl.glScaled(d_x*0.9, d_x*0.9, 1);
	        	if(block_id-1 == inventory_holding && source == hotbar){
	        		gl.glCallList( renderer.hbar_bg_hold );
	        	}else if(block_id-1 == hbar_select || block_id-1 == hotbar_hover){
	        		gl.glCallList( renderer.hbar_bg_sel );
	        	}else{
	        		gl.glCallList( renderer.hbar_bg );
	        	}
	        	gl.glPopMatrix();
	
	        	if(hotbar[block_id-1] >= 0){	
		        	gl.glPushMatrix();
		        	Block B = new Block(hotbar[block_id-1], this.renderer.get_coords_manager());
		        	if(Block.is_tool(hotbar[block_id-1])){
		        		gl.glRotatef(180, 0, 1, 0);
		        		if(block_id-1 == hbar_select || block_id-1 == hotbar_hover || (block_id-1 == inventory_holding && source == hotbar)){
			        		B.draw(gl,(int)(d_x*-0.55));
			        	}else{
			        		B.draw(gl,(int)(d_x*-0.45));
			        	}
		        	}else{
			        	gl.glRotatef(-20, 1, 0, 0);
			        	if(block_id-1 == hbar_select || block_id-1 == hotbar_hover || (block_id-1 == inventory_holding && source == hotbar)){
			        		gl.glRotatef((frame%360), 0, 1, 0);
			        		B.draw(gl,(int)(d_x*-0.55));
			        	}else{
			        		gl.glRotatef(45, 0, 1, 0);
			        		B.draw(gl,(int)(d_x*-0.45));
			        	}
		        	}
			        gl.glPopMatrix();
			        if(hotbar_count[block_id-1] > 1){
				        gl.glPushMatrix();
			        	gl.glTranslatef(renderer.width*0.003f, renderer.width*-0.009f, 49);
			        	gl.glRotatef(180, 1, 0, 0);
			        	gl.glScaled(0.13,0.13,1);
			        	gl.glDisable(GL2.GL_TEXTURE_2D);
			        	gl.glPushMatrix();
			        	gl.glColor3f(1,1,1);
			        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
			        	gl.glLineWidth(3);
			        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(hotbar_count[block_id-1]));
			        	gl.glPopMatrix();
			        	gl.glColor3f(0,0,0);
			        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("black"), 0);
			        	gl.glLineWidth(1);
			        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(hotbar_count[block_id-1]));
			        	gl.glEnable(GL2.GL_TEXTURE_2D);
				        gl.glPopMatrix();
			        }
	        	}
		        gl.glTranslatef(d_x, 0, 0);
	        }
	        gl.glPopMatrix();
	       
	        gl.glPushMatrix();// HP bar
	        gl.glTranslatef(renderer.width*0.99f, renderer.width*0.05f, -5.0f);
	        gl.glScaled(35,35,1);
	        for(int i = 20;i >= 2;i-=2){
				if(this.health >= i){
					gl.glCallList( renderer.hp_2 );
				}else if((i-this.health) == 1){
					gl.glCallList( renderer.hp_1 );
				}else{
					gl.glCallList( renderer.hp_0 );
				}
				gl.glTranslatef(0, 1.1f, 0);
				gl.glScaled(0.93,0.93,1);
			}
	        gl.glPopMatrix();
	        
	    	gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	 		
	        gl.glDepthMask(true);  // enables writes to Z-Buffer
	        gl.glEnable(GL2.GL_DEPTH_TEST); 
	        
	        gl.glMatrixMode(GL2.GL_PROJECTION);
	        gl.glPopMatrix();
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glPopMatrix();

		
		}else{
			// Draw Death Screen
		}
	}

	public void Scroll(int wheelRotation) {
		if(alive){
			if(wheelRotation < 0){ // scroll down
				if(hbar_select > 0){
					hbar_select--;
				}else{
					hbar_select = 8;
				}
			}else if(wheelRotation > 0){ // scroll up
				if(hbar_select < 8){
					hbar_select++;
				}else{
					hbar_select = 0;
				}
			}
		}
	}

	public void open_inventory() {
		if(alive){
			if(using_block){
				this.open_block_interface(null);
			}else{
				inventory_holding = -1;
				inventory_hover = -1;
				if(this.in_inventory){
					this.in_inventory = false;
				}else{
					this.in_inventory = true;
				}
			}
		}
	}

	public boolean is_in_inventory() {
		return this.in_inventory;
	}

	public void write_collisions() {
		Collision_detector CD = new Collision_detector(renderer, this);
		float[] collisions = CD.check_collision(1);
		CD = null;
		System.out.println("X: "+collisions[0]+" | Z: "+collisions[1]+" | ground: "+collisions[2]+" | ceil: "+collisions[3]+" | X_L: "+collisions[4]+" | Z_L: "+collisions[5]+" | ground_L: "+collisions[6]+" | ceil_L: "+collisions[7]);
	}

	public boolean is_in_water() {
		if(renderer.get_block_id(position)==Block.WATER)
			return true;
		return false;
	}

	public void inventory_click(MouseEvent m) {
		Point point = m.getPoint();
		int btn = m.getButton();
		if(alive){
			int x = point.x;
			int y = point.y;
			int index = -1;
			int target_item = 0;
			int target_item_count = 0;
			int half_stack = 0;
			if(y < renderer.height*0.8){
				if(this.using_block){
					if(x < renderer.width*0.51){// recept
						index = this.block_in_use.get_index(x,y,0);
						if(index >= 0){
							if(inventory_holding >= 0){
								target = this.block_in_use.get_inventory();
								target_count = this.block_in_use.get_inventory_count();
							}else{
								if(this.block_in_use.get_inventory()[index] >= 0){
									source = this.block_in_use.get_inventory();
									source_count = this.block_in_use.get_inventory_count();
									//inventory_holding = index;
								}
							}
						}
					}else{
						if(inventory_holding < 0){
							index = this.block_in_use.get_index(x,y,1);
							if(index >= 0){
								if(this.block_in_use.get_result()[index] >= 0){
									source = this.block_in_use.get_result();
									source_count = this.block_in_use.get_result_count();
									//inventory_holding = index;
								}
							}
						}
					}
	        	}else{
					x -= inv_off_x;
					y -= inv_off_y;
					if(x >= 0 && x <= inv_win_x && y >=0 && y <= inv_win_y){
						if(x % inv_slot_x < inv_item_x){
							index = (int)(x/inv_slot_x);
						}
						if((index >= 0) && ((y % inv_slot_y) < inv_item_y)){
							index += 9*(int)(y/inv_slot_y);
						}else{
							index = -1;
						}
					}
					if(index >= 0){
						if(inventory_holding >= 0){
							target = inventory;
							target_count = inventory_count;
						}else{
							if(inventory[index] >= 0){
								source = inventory;
								source_count = inventory_count;
								//inventory_holding = index;
							}
						}
					}
	        	}
			}else{
				x -= hbar_off_x;
				y -= hbar_off_y;
				if(x >= 0 && x <= hbar_win_x && y >=0 && y <= hbar_win_y){
					if(x % hbar_slot_x < hbar_item_x){
						index = (int)(x/hbar_slot_x);
					}else{
						index = -1;
					}
				}
				if(index >= 0){
					if(inventory_holding >= 0){
						target = hotbar;
						target_count = hotbar_count;
					}else{
						if(hotbar[index] >= 0){
							source = hotbar;
							source_count = hotbar_count;
							//inventory_holding = index;
						}
					}
				}
			}
			
			if(index >= 0){
				if(inventory_holding >= 0){
					if(btn == 3 && target_count[index] == 0){
						target[index] = source[inventory_holding];
						half_stack = (int)(source_count[inventory_holding]/2);
						source_count[inventory_holding] -= half_stack;
						target_count[index] = half_stack;
					}else{
						if(target[index] == source[inventory_holding] && !(target == source && index == inventory_holding)){
							target_item = target[index];
							target_item_count = target_count[index] + source_count[inventory_holding];
							source[inventory_holding] = -1;
							source_count[inventory_holding] = 0;
							target[index] = target_item;
							target_count[index] = target_item_count;
						}else{
							target_item = target[index];
							target_item_count = target_count[index];
							target[index] = source[inventory_holding];
							source[inventory_holding] = target_item;
							target_count[index] = source_count[inventory_holding];
							source_count[inventory_holding] = target_item_count;
						}
					}
					target_item = -1;
					target_item_count = 0;
					half_stack = 0;
					inventory_holding = -1;
					if(this.using_block){
						if(this.block_in_use.get_result() == source){
							this.block_in_use.Take();
						}else{
							this.block_in_use.Use();
						}
					}
				}else{
					if(source[index] >= 0){
						inventory_holding = index;
					}
				}
			}
		}
	}
	
	
	@Override
	public void Get_hit(int dmg, Vector dmg_direction) {
		if(alive){
			if(this.health > 0){
				this.health = Math.max((this.health - dmg),0);
			}
			this.Move(dmg_direction, 1f);
			if(this.health <= 0){
				this.Die();
			}
		}
	}
	

	private void Die() {
		Random rnd = new Random();
		this.alive = false;
		this.position.addY(5);
		this.direction = new Vector(.01, -1, .01);
		File f = new File("./World/Player/save.txt");
		f.delete();
		for(int i = 0; i < 12; i++){
			rigor_mortis[i] = rnd.nextFloat()*220-110;
		}
	}

	public void inventory_move(int m_x, int m_y) {
		if(alive){
			int x = m_x;
			int y = m_y;
			int index = -1;
			if(y < renderer.height*0.8){
				x -= inv_off_x;
				y -= inv_off_y;
				if(x >= 0 && x <= inv_win_x && y >=0 && y <= inv_win_y){
					if(x % inv_slot_x < inv_item_x){
						index = (int)(x/inv_slot_x);
					}
					if((index >= 0) && ((y % inv_slot_y) < inv_item_y)){
						index += 9*(int)(y/inv_slot_y);
					}else{
						index = -1;
					}
				}
				inventory_hover = index;
			}else{
				x -= hbar_off_x;
				y -= hbar_off_y;
				if(x >= 0 && x <= hbar_win_x && y >=0 && y <= hbar_win_y){
					if(x % hbar_slot_x < hbar_item_x){
						index = (int)(x/hbar_slot_x);
					}else{
						index = -1;
					}
				}
				hotbar_hover = index;
			}
		}
	}

	public float get_angle_dev() {
		return motion.get_angle_deviation();
	}

	public Renderer get_renderer() {
		return renderer;
	}

	@Override
	public void draw(GL2 gl) {
		if(draw_list >= 0){
			if(alive){
				Vector dir = new Vector(this.direction.getX_D(),0,this.direction.getZ_D());
				dir.normalize();
				gl.glRotated(-(dir.get_angle_to(new Vector(0,0,-1))*180)/Math.PI, 0, 1, 0);
				// R hand
				gl.glPushMatrix();
				float hand_angle = this.direction.getY_F()*90+90;
				if(hand_move > 0){
					hand_angle-=hand_move*10;
					if(hand_dir > 0){
						hand_move++;
					}else{
						hand_move--;
					}
					if(hand_move > 3){
						hand_dir = 0;
					}
				}
				gl.glRotatef(hand_angle, 1, 0, 0);
				gl.glTranslatef(0.375f, 0, 0.3f);
				gl.glTranslatef(0, -0.25f, 0);
				gl.glScaled(0.6, 0.6, 0.6);
				gl.glCallList( draw_list+2 );
				gl.glScaled(10/6.0, 10/6.0, 10/6.0);
				renderer.bind_texture(1);
				if(hotbar[hbar_select] >= 0){
					if(Block.is_tool(hotbar[hbar_select])){
						if(hotbar[hbar_select] != Block.SHOVEL){
							gl.glTranslatef(-0.03f, -0.18f, -0.17f);
						}else{
							gl.glTranslatef(-0.05f, -0.23f, -0.17f);
						}
						gl.glRotatef(-90, 1, 0, 0);
						gl.glRotatef(-75, 0, 1, 0);
						if(hotbar[hbar_select] != Block.SHOVEL){
							gl.glRotatef(45, 0, 0, 1);
						}
						Block B = new Block(hotbar[hbar_select], this.renderer.get_coords_manager());
						B.draw(gl,0.4f);
					}else{
						gl.glTranslatef(-0.07f, -0.2f, -0.07f);
						gl.glRotatef(-30, 1, 1, 1);
						Block B = new Block(hotbar[hbar_select], this.renderer.get_coords_manager());
						B.draw(gl,0.15f);
					}
				}
				gl.glPopMatrix();
				
			}else{
				gl.glTranslatef(0, -6.5f, 0);
				gl.glRotated(90, 1, 0, 0);
				// head
				gl.glTranslatef(0, 0.13f, 0);
				gl.glCallList( draw_list );
				
				// body
				gl.glPushMatrix();
				gl.glTranslatef(0, -0.625f, 0);
				gl.glCallList( draw_list+1 );
				gl.glPopMatrix();
				
				// L hand
				gl.glPushMatrix();
				gl.glTranslatef(-0.375f, -0.375f, 0);
				gl.glRotatef(rigor_mortis[0], 1, 0, 0);
				gl.glRotatef(rigor_mortis[1], 0, 1, 0);
				gl.glRotatef(rigor_mortis[2], 0, 0, 1);
				gl.glTranslatef(0, -0.25f, 0);
				gl.glCallList( draw_list+2 );
				gl.glPopMatrix();
				
				// R hand
				gl.glPushMatrix();
				gl.glTranslatef(0.375f, -0.375f, 0);
				gl.glRotatef(rigor_mortis[3], 1, 0, 0);
				gl.glRotatef(rigor_mortis[4], 0, 1, 0);
				gl.glRotatef(rigor_mortis[5], 0, 0, 1);
				gl.glTranslatef(0, -0.25f, 0);
				gl.glCallList( draw_list+2 );
				gl.glPopMatrix();
				
				// L leg
				gl.glPushMatrix();
				gl.glTranslatef(-0.125f, -1, 0);
				gl.glRotatef(rigor_mortis[6], 1, 0, 0);
				gl.glRotatef(rigor_mortis[7], 0, 1, 0);
				gl.glRotatef(rigor_mortis[8], 0, 0, 1);
				gl.glTranslatef(0, -0.375f, 0);
				gl.glCallList( draw_list+3 );
				gl.glPopMatrix();
	
				// R leg
				gl.glPushMatrix();
				gl.glTranslatef(0.125f, -1, 0);
				gl.glRotatef(rigor_mortis[9], 1, 0, 0);
				gl.glRotatef(rigor_mortis[10], 0, 1, 0);
				gl.glRotatef(rigor_mortis[11], 0, 0, 1);
				gl.glTranslatef(0, -0.375f, 0);
				gl.glCallList( draw_list+3 );
				gl.glPopMatrix();		
			}
		}
	}

	public void draw_CAM(GL2 gl) {
		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0.0, renderer.width, renderer.height, 0.0, -1.0, 10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glDepthMask(false);  // disable writes to Z-Buffer
        gl.glDisable(GL2.GL_DEPTH_TEST); 
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
        
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		
 		gl.glPushMatrix();	
 		gl.glTranslatef(renderer.width*0.04f, renderer.height*0.06f, -5.0f);
 		gl.glScaled(240, 180, 1);
 		FloatBuffer vertices;
 		vertices = Buffers.newDirectFloatBuffer(8);
		vertices.put(0.05f);
		vertices.put(0.05f);
		vertices.put(0);
		vertices.put(1);
		vertices.put(0.95f);
		vertices.put(0.95f);
		vertices.put(1);
		vertices.put(0.05f);
		vertices.rewind();

		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, vertices);
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
 		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);     // texture application method - modulation
 		gl.glEnable(GL2.GL_BLEND);
 		
 		gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
 		
 		gl.glDisable(GL2.GL_BLEND);
 		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);     // texture application method - modulation
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY); 		
        
 		gl.glPopMatrix();
 		
        gl.glDepthMask(true);  // enables writes to Z-Buffer
        gl.glEnable(GL2.GL_DEPTH_TEST); 
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();

	
	}

	public int get_block_in_hand() {
		return hotbar[hbar_select];
	}

	
}
