package pkg.skapik.icp.assets;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import pkg.skapik.icp.func.Crafting_recipe;
import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Renderer;

public class Workbench extends Usable{

	private int[] inventory;
	private int[] inventory_count;
	public int[] result;
	private int[] result_count;
	
	private boolean setup;
	private int inv_off_x;
	private int inv_off_y;
	private int inv_win_x;
	private int inv_win_y;
	private int inv_slot_x;
	private int inv_slot_y;
	private int inv_item_x;
	private int inv_item_y;
	private int res1_from_x;
	private int res1_from_y;
	private int res1_to_x;
	private int res1_to_y;
	private int res2_from_x;
	private int res2_from_y;
	private int res2_to_x;
	private int res2_to_y;
	
	
	public Workbench() {
		this.inventory = new int[9];
		this.inventory[0] = -1;
		this.inventory[1] = -1;
		this.inventory[2] = -1;
		this.inventory[3] = -1;
		this.inventory[4] = -1;
		this.inventory[5] = -1;
		this.inventory[6] = -1;
		this.inventory[7] = -1;
		this.inventory[8] = -1;
		this.inventory_count = new int[9];
		this.inventory_count[0] = 0;
		this.inventory_count[1] = 0;
		this.inventory_count[2] = 0;
		this.inventory_count[3] = 0;
		this.inventory_count[4] = 0;
		this.inventory_count[5] = 0;
		this.inventory_count[6] = 0;
		this.inventory_count[7] = 0;
		this.inventory_count[8] = 0;
		this.result = new int[2];
		this.result[0] = -1;
		this.result[1] = -1;
		this.result_count = new int[2];
		this.result_count[0] = 0;
		this.result_count[1] = 0;
		this.inv_off_x = 1;
		this.inv_off_y = 1;
		this.inv_win_x = 1;
		this.inv_win_y = 1;
		this.inv_slot_x = 1;
		this.inv_slot_y = 1;
		this.inv_item_x = 1;
		this.inv_item_y = 1;
		this.setup = false;
	}
	
	private void hud_init(Renderer renderer){
		this.inv_off_x = (int)(renderer.width*0.32);
		this.inv_off_y = (int)(renderer.height*0.2442);
		this.inv_win_x = (int)(renderer.width*0.1922);
		this.inv_win_y = (int)(renderer.height*0.3548);
		this.inv_slot_x = (int)(renderer.width*0.0662);
		this.inv_slot_y = (int)(renderer.height*0.1221);
		this.inv_item_x = (int)(renderer.width*0.0604);
		this.inv_item_y = (int)(renderer.height*0.1115);
		this.res1_from_x = (int)(renderer.width*0.5545);
		this.res1_from_y = (int)(renderer.height*0.3115);
		this.res1_to_x = (int)(renderer.width*0.6743);
		this.res1_to_y = (int)(renderer.height*0.5327);
		this.res2_from_x = 0;
		this.res2_from_y = 0;
		this.res2_to_x = 0;
		this.res2_to_y = 0;
		this.setup = true;
	}
	
	@Override
	public void Use() {
		int[] res = Crafting_recipe.check_recipe(inventory, Block.WORKBENCH);
		result[0] = res[0];
		result[1] = res[2];
		result_count[0] = res[1];
		result_count[1] = res[3];
	}
	
	@Override
	public void Take() {
		for(int i = 0; i < 9; i++){
			inventory_count[i] = Math.max(0, (inventory_count[i]-1));
			if(inventory_count[i] == 0){
				inventory[i] = -1;
			}
		}
		this.Use();
	}

	@Override
	public void Draw_interface(GL2 gl, GLUT glut, Renderer renderer) {
		if(!this.setup){
			this.hud_init(renderer);
		}
		gl.glPushMatrix();
		gl.glTranslatef(renderer.width*0.35f, renderer.height*0.3f , -50.0f);
		//Custom_Draw.drawQuad(gl, renderer.width*0.4f);
		//gl.glTranslatef(renderer.width*0.05f, renderer.height*0.1f , 0);
		gl.glScaled(renderer.width*0.06f, renderer.width*0.06f, 1);
		for(int i = 0; i < 9; i++){
       		gl.glCallList( renderer.hbar_bg );
       		if(inventory[i] >= 0){
	        	gl.glPushMatrix();
	        	Block B = new Block(inventory[i], renderer.get_coords_manager());
	        	if(Block.is_tool(inventory[i])){
	        		gl.glRotatef(180, 0, 1, 0);
	        		B.draw(gl,(-0.6f));
	        	}else{
		        	gl.glRotatef(-20, 1, 0, 0);
	        		gl.glRotatef(45, 0, 1, 0);
	        		B.draw(gl,(-0.6f));
	        	}
		        gl.glPopMatrix();
		        if(inventory_count[i] > 1){
			        gl.glPushMatrix();
		        	gl.glTranslatef(renderer.width*0.000045f, renderer.width*-0.00015f, 49);
		        	gl.glRotatef(180, 1, 0, 0);
		        	gl.glScaled(0.0013,0.0013,1);
		        	gl.glDisable(GL2.GL_TEXTURE_2D);
		        	gl.glPushMatrix();
		        	gl.glColor3f(1,1,1);
		        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
		        	gl.glLineWidth(3);
		        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(inventory_count[i]));
		        	gl.glPopMatrix();
		        	gl.glColor3f(0,0,0);
		        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("black"), 0);
		        	gl.glLineWidth(1);
		        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(inventory_count[i]));
		        	gl.glEnable(GL2.GL_TEXTURE_2D);
			        gl.glPopMatrix();
		        }
        	}
        	if(i%3 < 2){
        		gl.glTranslatef(1.1f, 0 , 0);
        	}else{
        		gl.glTranslatef(-2.2f, 1.1f , 0);
        	}
		}
		gl.glTranslatef(4.4f, -2.2f , 0);
		
		gl.glScaled(2, 2, 1);
		gl.glCallList( renderer.hbar_bg );
		if(result[0] >= 0){
			gl.glPushMatrix();
        	Block B = new Block(result[0], renderer.get_coords_manager());
        	if(Block.is_tool(result[0])){
        		gl.glRotatef(180, 0, 1, 0);
        		B.draw(gl,(-0.6f));
        	}else{
	        	gl.glRotatef(-20, 1, 0, 0);
        		gl.glRotatef(45, 0, 1, 0);
        		B.draw(gl,(-0.6f));
        	}
	        gl.glPopMatrix();
	        if(result_count[0] > 1){
		        gl.glPushMatrix();
	        	gl.glTranslatef(renderer.width*0.000047f, renderer.width*-0.00016f, 49);
	        	gl.glRotatef(180, 1, 0, 0);
	        	gl.glScaled(0.0013,0.0013,1);
	        	gl.glDisable(GL2.GL_TEXTURE_2D);
	        	gl.glPushMatrix();
	        	gl.glColor3f(1,1,1);
	        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("white"), 0);
	        	gl.glLineWidth(3);
	        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(result_count[0]));
	        	gl.glPopMatrix();
	        	gl.glColor3f(0,0,0);
	        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("black"), 0);
	        	gl.glLineWidth(1);
	        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(result_count[0]));
	        	gl.glEnable(GL2.GL_TEXTURE_2D);
		        gl.glPopMatrix();
	        }
		}
		gl.glPopMatrix();
	}
	
	@Override
	public int get_index(int x, int y, int in_out) {
		int index = -1;
		if(in_out == 0){
			x -= this.inv_off_x;
			y -= this.inv_off_y;
			if(x >= 0 && x <= this.inv_win_x && y >=0 && y <= this.inv_win_y){
				if(x % this.inv_slot_x < this.inv_item_x){
					index = (int)(x/this.inv_slot_x);
				}
				if((index >= 0) && ((y % this.inv_slot_y) < this.inv_item_y)){
					index += 3*(int)(y/this.inv_slot_y);
				}else{
					index = -1;
				}
			}
		}else{
			if(x >= this.res1_from_x && x <= this.res1_to_x && y >= this.res1_from_y && y <= this.res1_to_y && result[0] >= 0){
				index = 0;
			}
		}
		return index;
	}

	@Override
	public int[] get_inventory() {
		return this.inventory;
	}
	
	@Override
	public int[] get_inventory_count() {
		return this.inventory_count;
	}
	
	@Override
	public int[] get_result() {
		return this.result;
	}
	
	@Override
	public int[] get_result_count() {
		return this.result_count;
	}
}
