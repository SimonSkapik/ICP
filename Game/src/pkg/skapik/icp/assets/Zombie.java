package pkg.skapik.icp.assets;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import pkg.skapik.icp.func.Boundery_box;
import pkg.skapik.icp.func.Collision_detector;
import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Mob_move_state;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Renderer;
import pkg.skapik.icp.func.Vector;

public class Zombie implements Creature{

	private Position position;
	private Vector direction;
	private Renderer renderer;
	private Boundery_box boundery;
	private Mob_move_state motion;
	private float speed;
	private float reach;
	private float notice_radius;
	private float attack_radius;
	private int health;
	private int draw_list;
	private float world_rotation;
	private boolean aware, moving;
	private float l_hand_off, r_hand_off;
	private float leg_state;
	private boolean alive;
	private float[] rigor_mortis;
	
	
	public Zombie(Position pos, Renderer renderer, int draw_list){
		this.position = pos;
		this.renderer = renderer;
		Random rnd = new Random();
		direction = new Vector(rnd.nextFloat(), 0, rnd.nextFloat());
		this.motion = new Mob_move_state(this);
		direction.normalize();
		this.health = 10;
		this.aware = false;
		this.moving = false;
		this.draw_list = draw_list;
		this.reach = 1;
		this.rigor_mortis = new float[12];
		this.notice_radius = 30;
		this.attack_radius = 15;
		this.boundery = new Boundery_box(0.5f,1.8f,0.5f,0.5f,0.9f,0.5f);
		this.l_hand_off = rnd.nextFloat()*20 - 10;
		this.r_hand_off = rnd.nextFloat()*20 - 10;
		this.leg_state = rnd.nextFloat()*4;
		if(this.leg_state < 2){
			this.speed = 0.04f;
		}else{
			this.speed = 0.12f;
		}
		this.alive = true;
	}
	
	@Override
	public void Update() {
		if(alive){
			Player p = renderer.get_player(); 
			Vector to_player = new Vector(p.get_position().getX_D()-this.position.getX_D(), 0, p.get_position().getZ_D()-this.position.getZ_D());
	
			to_player.normalize();
			this.direction.normalize();
			float angle_to_player = this.direction.get_angle_to(to_player);
			float dist_to_player = this.position.distance_to_2d(p.get_position());
			if(dist_to_player > notice_radius){
				//*   direction = turn a little random
				aware = false;
			}else if(dist_to_player > attack_radius){
				aware = true;
				this.direction = new Vector(to_player);
				this.world_rotation = (float) ((-this.direction.get_angle_to(new Vector(0,0,-1))*180)/Math.PI);
			}else{
				aware = true;
				if(Math.abs((angle_to_player*180)/Math.PI) < 20){
					if(this.position.distance_to_3d(p.get_position()) <= this.reach){
						moving = false;
						this.stop();
						this.attack(p);
					}else{
						this.direction = new Vector(to_player);
						this.world_rotation = (float) ((-this.direction.get_angle_to(new Vector(0,0,-1))*180)/Math.PI);
						moving = true;
					}
				}else{
					this.direction = new Vector(to_player);
					this.world_rotation = (float) ((-this.direction.get_angle_to(new Vector(0,0,-1))*180)/Math.PI);
				}
			}
			if(moving){
				this.go(new Vector(this.direction),speed);
			}else{
				this.go(new Vector(0,0,0),speed);
			}
		}
	}
	
	
	
	public static void pre_draw(GL2 gl, int list){
		/*
		 * DRAW BOUDERY
		float L = (float)-this.boundery.get_left();
		float R = (float)this.boundery.get_right();
		float T = (float)this.boundery.get_top();
		float Bo = (float)-this.boundery.get_bot();
		float F = (float)-this.boundery.get_front();
		float Ba = (float)this.boundery.get_back();
		
		float vert[] = {L,T,Ba, L,Bo,Ba, R,Bo,Ba, R,T,Ba, R,T,Ba, R,Bo,Ba, R,Bo,F, R,T,F,  // front, right       // 6L4L3 of verteL coords
				R,T,F, R,Bo,F, L,Bo,F, L,T,F, L,T,F, L,Bo,F, L,Bo,Ba, L,T,Ba,  // back, left
				L,T,F, L,T,Ba, R,T,Ba, R,T,F, R,Bo,F, R,Bo,Ba, L,Bo,Ba, L,Bo,F}; // top, bot
		FloatBuffer vertices = Buffers.newDirectFloatBuffer(72);
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 12; j++){
				vertices.put(vert[i*12+j]);
			}
		}
		vertices.rewind();
		*/
		// draw 1x1 box from center

		float m = -0.5f;
		float p = 0.5f;
		float f1 = 1/16.0f, f2 = 2/16.0f, f3 = 3/16.0f, f4 = 4/16.0f, f5 = 5/16.0f, f6 = 6/16.0f, f7 = 7/16.0f, f8 = 8/16.0f, f9 = 9/16.0f, fA = 10/16.0f, fB = 11/16.0f, fC = 12/16.0f, fD = 13/16.0f, fE = 14/16.0f;
		
		float vert[] = {m,p,p, m,m,p, p,m,p, p,p,p, p,p,p, p,m,p, p,m,m, p,p,m,  // front, right       // 6x4x3 of vertex coords
				p,p,m, p,m,m, m,m,m, m,p,m, m,p,m, m,m,m, m,m,p, m,p,p,  // back, left
				m,p,m, m,p,p, p,p,p, p,p,m, p,m,m, p,m,p, m,m,p, m,m,m}; // top, bot
		float norm[] = {0,0,1, 1,0,0, 0,0,-1, -1,0,0, 0,1,0, 0,-1,0};
		
		//head
	  	float tex_head[] = {f6,f4, f6,f8, f8,f8, f8,f4, 0,f4, 0,f8, f2,f8, f2,f4,    // back, front     //  6x4x2 texture coordinates
	  				   f2,f4, f2,f8, f4,f8, f4,f4, f4,f4, f4,f8, f6,f8, f6,f4,     // left, right
	  				   f4,f4, f4,0, f2,0, f2,f4,  f4,0, f4,f4, f6,f4, f6,0};  // top, bot
	  	//body				   
	  	float tex_body[] = {f8,fA, f8,1, fA,1, fA,fA, f5,fA, f5,1, f7,1, f7,fA,    // back, front     //  6x4x2 texture coordinates
	  				   f4,fA, f4,1, f5,1, f5,fA, f7,fA, f7,1, f8,1, f8,fA,   // left, right
	  				   f5,f8, f5,fA, f7,fA, f7,f8, f7,f8, f7,fA, f9,fA, f9,f8};  // top, bot			   
	  	//hand
	  	float tex_hand[] = {fD,fA, fD,1, fE,1, fE,fA, fB,fA, fB,1, fC,1, fC,fA,    // back, front     //  6x4x2 texture coordinates
	  				   fA,fA, fA,1, fB,1, fB,fA, fC,fA, fC,1, fD,1, fD,fA,   // left, right
	  				   fB,f8, fB,fA, fC,fA, fC,f8, fC,f8, fC,fA, fD,fA, fD,f8};  // top, bot
	  	//leg
	  	float tex_leg[] = {f3,fA, f3,1, f4,1, f4,fA, f1,fA, f1,1, f2,1, f2,fA,    // back, front     //  6x4x2 texture coordinates
	  				   0,fA, 0,1, f1,1, f1,fA, f2,fA, f2,1, f3,1, f3,fA,   // left, right
	  				   f1,f8, f1,fA, f2,fA, f2,f8, f2,f8, f2,fA, f3,fA, f3,f8};  // top, bot
	  	
		FloatBuffer vertices = Buffers.newDirectFloatBuffer(72);
		FloatBuffer normals = Buffers.newDirectFloatBuffer(72);
		FloatBuffer tex_coords = Buffers.newDirectFloatBuffer(72);
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 12; j++){
				vertices.put(vert[i*12+j]);
				normals.put(norm[i*3+(j%3)]);
			}
		}
		vertices.rewind();
		normals.rewind();
		
		//Draw head
		tex_coords.clear();
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 8; j++){
				tex_coords.put(tex_head[i*8+j]);
			}
		}
		tex_coords.rewind();
	    gl.glNewList( list, GL2.GL_COMPILE );

		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, tex_coords);
 		gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
		
		gl.glPushMatrix();
		gl.glScaled(0.5, 0.5, 0.5);
		gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
		gl.glPopMatrix();
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		
		gl.glEndList();
		
		// Draw body
		tex_coords.clear();
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 8; j++){
				tex_coords.put(tex_body[i*8+j]);
			}
		}
		tex_coords.rewind();
		gl.glNewList( list+1, GL2.GL_COMPILE );
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, tex_coords);
 		gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);

		gl.glPushMatrix();
		gl.glScaled(0.5, 0.75, 0.25);
		gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
		gl.glPopMatrix();

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		
		gl.glEndList();
		
		// Draw hand		
		tex_coords.clear();
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 8; j++){
				tex_coords.put(tex_hand[i*8+j]);
			}
		}
		tex_coords.rewind();
		gl.glNewList( list+2, GL2.GL_COMPILE );
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, tex_coords);
 		gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);

		gl.glPushMatrix();
		gl.glScaled(0.25, 0.75, 0.25);
		gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
		gl.glPopMatrix();

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		
		gl.glEndList();
		
		// Draw leg		
		tex_coords.clear();
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 8; j++){
				tex_coords.put(tex_leg[i*8+j]);
			}
		}
		tex_coords.rewind();
		gl.glNewList( list+3, GL2.GL_COMPILE );

		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, tex_coords);
 		gl.glMaterialfv ( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);

		gl.glPushMatrix();
		gl.glScaled(0.25, 0.75, 0.25);
		gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
		gl.glPopMatrix();

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		
		gl.glEndList();
	    
	}
	
	@Override
	public void draw(GL2 gl){
		gl.glRotated(this.world_rotation, 0, 1, 0);
		if(!alive){
			gl.glRotated(90, 1, 0, 0);
		}
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
		if(alive){
			if(aware){
				gl.glRotatef(90+l_hand_off, 1, 0, 0);
			}
		}else{
			gl.glRotatef(rigor_mortis[0], 1, 0, 0);
			gl.glRotatef(rigor_mortis[1], 0, 1, 0);
			gl.glRotatef(rigor_mortis[2], 0, 0, 1);
		}
		gl.glTranslatef(0, -0.25f, 0);
		gl.glCallList( draw_list+2 );
		gl.glPopMatrix();
		
		// R hand
		gl.glPushMatrix();
		gl.glTranslatef(0.375f, -0.375f, 0);
		if(alive){
			if(aware){
				gl.glRotatef(90+r_hand_off, 1, 0, 0);
			}
		}else{
			gl.glRotatef(rigor_mortis[3], 1, 0, 0);
			gl.glRotatef(rigor_mortis[4], 0, 1, 0);
			gl.glRotatef(rigor_mortis[5], 0, 0, 1);
		}
		gl.glTranslatef(0, -0.25f, 0);
		gl.glCallList( draw_list+2 );
		gl.glPopMatrix();
		
		// L leg
		gl.glPushMatrix();
		gl.glTranslatef(-0.125f, -1, 0);
		if(alive){
			if(this.leg_state <= 1){
				gl.glRotated(-20, 0, 1, 0);
				gl.glRotated(-25, 1, 0, 0);
			}else{
				if(moving){
					gl.glRotated(30*Math.sin((renderer.get_frame_count()/5.0)%(Math.PI*2)), 1, 0, 0);
				}
			}
		}else{
			gl.glRotatef(rigor_mortis[6], 1, 0, 0);
			gl.glRotatef(rigor_mortis[7], 0, 1, 0);
			gl.glRotatef(rigor_mortis[8], 0, 0, 1);
		}
		gl.glTranslatef(0, -0.375f, 0);
		gl.glCallList( draw_list+3 );
		gl.glPopMatrix();

		// R leg
		gl.glPushMatrix();
		gl.glTranslatef(0.125f, -1, 0);
		if(alive){
			if(this.leg_state > 1 && this.leg_state < 2){
				gl.glRotated(20, 0, 1, 0);
				gl.glRotated(-25, 1, 0, 0);
			}else{
				if(moving){
					gl.glRotated(30*Math.sin((renderer.get_frame_count()/5.0)%(Math.PI*2)+Math.PI), 1, 0, 0);
				}
			}
		}else{
			gl.glRotatef(rigor_mortis[9], 1, 0, 0);
			gl.glRotatef(rigor_mortis[10], 0, 1, 0);
			gl.glRotatef(rigor_mortis[11], 0, 0, 1);
		}
		gl.glTranslatef(0, -0.375f, 0);
		gl.glCallList( draw_list+3 );
		gl.glPopMatrix();		
	}
	
	private void attack(Player p) {
		p.Get_hit(1,this.direction);
	}

	private void stop() {
		
	}

	private void go(Vector dir, float speed) {
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
	}

	public Vector get_view_direction(){
		return this.direction;
	}
	
	public boolean is_in_water() {
		if(renderer.get_block_id(position)==Block.WATER)
			return true;
		return false;
	}
	
	@Override
	public void Get_hit(int dmg,Vector dmg_direction) {
		if(this.health > 0){
			this.health = Math.max((this.health - dmg),0);
		}
		go(dmg_direction,1);
		if(this.health == 0){
			this.Die();
		}
	}
	
	private void Die(){
		Random rnd = new Random();
		this.aware = false;
		this.moving = false;
		this.alive = false;
		this.position.addY(-1.5);
		for(int i = 0; i < 12; i++){
			rigor_mortis[i] = rnd.nextFloat()*220-110;
		}
		renderer.Kill_mob(this);
	}
	
	@Override
	public Boundery_box get_boundery() {
		return this.boundery;
	}

	@Override
	public Vector get_move_direction() {
		return this.direction;
	}

	@Override
	public Position get_position() {
		return this.position;
	}


	
}
