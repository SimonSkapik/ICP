package pkg.skapik.icp.assets;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import pkg.skapik.icp.func.Crafting_recipe;
import pkg.skapik.icp.func.Custom_Draw;
import pkg.skapik.icp.func.Renderer;

public class Workbench implements Usable{

	private int[] inventory;
	private int[] inventory_count;
	private int[] result;
	
	
	public Workbench() {
		inventory = new int[9];
		inventory[0] = Block.AXE;
		inventory[1] = -1;
		inventory[2] = Block.BRICKS;
		inventory[3] = -1;
		inventory[4] = -1;
		inventory[5] = -1;
		inventory[6] = Block.ORE_COAL;
		inventory[7] = -1;
		inventory[8] = -1;
		inventory_count = new int[9];
		inventory_count[0] = 1;
		inventory_count[1] = 0;
		inventory_count[2] = 5;
		inventory_count[3] = 0;
		inventory_count[4] = 0;
		inventory_count[5] = 0;
		inventory_count[6] = 3;
		inventory_count[7] = 0;
		inventory_count[8] = 0;
		result = new int[4];
		result[0] = Block.BEDROCK;
		result[1] = 5;
		result[2] = -1;
		result[3] = 0;
	}
	
	@Override
	public void Use() {
		result = Crafting_recipe.check_recipe(inventory, Block.WORKBENCH);
	}

	@Override
	public void Draw_interface(GL2 gl, GLUT glut, Renderer renderer) {
		gl.glPushMatrix();
		gl.glTranslatef(renderer.width*0.3f, renderer.height*0.2f , -50.0f);
		//Custom_Draw.drawQuad(gl, renderer.width*0.4f);
		gl.glTranslatef(renderer.width*0.05f, renderer.height*0.1f , 0);
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
		        	gl.glTranslatef(renderer.width*0.003f, renderer.width*-0.009f, 49);
		        	gl.glRotatef(180, 1, 0, 0);
		        	gl.glScaled(0.13,0.13,1);
		        	gl.glDisable(GL2.GL_TEXTURE_2D);
		        	gl.glColor3f(0,0,0);
		        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("black"), 0);
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
	        if(result[1] > 1){
		        gl.glPushMatrix();
	        	gl.glTranslatef(renderer.width*0.003f, renderer.width*-0.009f, 49);
	        	gl.glRotatef(180, 1, 0, 0);
	        	gl.glScaled(0.13,0.13,1);
	        	gl.glDisable(GL2.GL_TEXTURE_2D);
	        	gl.glColor3f(0,0,0);
	        	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("black"), 0);
	        	glut.glutStrokeString(GLUT.STROKE_ROMAN, Integer.toString(result[1]));
	        	gl.glEnable(GL2.GL_TEXTURE_2D);
		        gl.glPopMatrix();
	        }
		}
		gl.glPopMatrix();
	}

}
