package pkg.skapik.icp.assets;

import java.nio.*;
import com.jogamp.common.nio.Buffers;
import javax.media.opengl.GL2;
import pkg.skapik.icp.func.Custom_Draw;

public class Skybox{
	
	private int skybox;
	private float size;
	private boolean day;
	
	public Skybox(float size, GL2 gl) {
		this.size = size;
		this.day = false;
		this.pre_draw(gl, Custom_Draw.float_color(0.07f*1,0.153f*1,0.239f*1,1.0f));
	}
	
	public void pre_draw(GL2 gl, float[] color) {
		float y1 = 1.0f/3.0f;
		float y2 = 2.0f/3.0f;
		
		float vert[] = {0,0,0, 0,0,size, 0,size,0, 0,size,size, size,0,0, size,0,size, size,size,0, size,size,size,  // back, front
						0,0,0, 0,0,size, 0,size,0, 0,size,size, size,0,0, size,0,size, size,size,0, size,size,size,  // left, right
						0,0,0, 0,0,size, 0,size,0, 0,size,size, size,0,0, size,0,size, size,size,0, size,size,size}; // top, bot       // 8x3 of vertex coords
		int ind[] = {0,4,6,2,1,3,7,5,   		 // back, front    // 6x4 of vertex indices
					 8,10,11,9,12,13,15,14,  	 // left, right
					 18,22,23,19,16,17,21,20};   // top, bot
		float tex[] = {0.25f,y2, 1,y2, 0.25f,y1, 1,y1, 0.5f,y2, 0.75f,y2, 0.5f,y1, 0.75f,y1,   // back, front     //  6x4x2 texture coordinates
					   0.25f,y2, 0,y2, 0.25f,y1, 0,y1, 0.5f,y2, 0.75f,y2, 0.5f,y1, 0.75f,y1,   // left, right
					   0.25f,y2, 0.25f,1, 0.25f,y1, 0.25f,0, 0.5f,y2, 0.5f,1, 0.5f,y1, 0.5f,0};  // top, bot 
		
		FloatBuffer vertices = Buffers.newDirectFloatBuffer(vert.length);
		for (int i = 0; i < vert.length; i++){
			vertices.put(vert[i]);
		}
		IntBuffer indices = Buffers.newDirectIntBuffer(vert.length);
		for (int i = 0; i < ind.length; i++){
			indices.put(ind[i]);
		}
		FloatBuffer coords = Buffers.newDirectFloatBuffer(tex.length);
		for (int i = 0; i < tex.length; i++){
			coords.put(tex[i]);
		}
		
		vertices.rewind();
		indices.rewind();
		coords.rewind();
		
		
		skybox = gl.glGenLists(1);
	    gl.glNewList( skybox, GL2.GL_COMPILE );
	    gl.glDisable(GL2.GL_TEXTURE_2D);
	    // activate and specify pointer to vertex array
 		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 	    //
 		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
 		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, coords);
 		// draw a cube
 		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0);
 		gl.glDrawElements(GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, indices);
 		
 		// deactivate vertex arrays after drawing
 		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
 		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
 		gl.glEnable(GL2.GL_TEXTURE_2D);
	    gl.glEndList();
	}

	public void draw(GL2 gl, float detail){
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, Custom_Draw.float_color("red"), 0);
		gl.glCallList( skybox );
	}
	
	public boolean is_day(){
		return this.day;
	}

	public void set_day(boolean b) {
		this.day = b;
	}
	

}
