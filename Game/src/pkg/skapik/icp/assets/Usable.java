package pkg.skapik.icp.assets;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import pkg.skapik.icp.func.Renderer;

public abstract class Usable {

	
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
	
	public void Use(){
		
	};
	
	public void Draw_interface(GL2 gl, GLUT glut, Renderer renderer){
		
	}

	public int get_index(int x, int y, int in_out) {
		return -1;
	}

	public int[] get_inventory() {
		return null;
	}

	public int[] get_inventory_count() {
		return null;
	}

	public int[] get_result() {
		return null;
	}

	public int[] get_result_count() {
		return null;
	}

	public void Take() {
		// TODO Auto-generated method stub
		
	}
}
