package pkg.skapik.icp.assets;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import pkg.skapik.icp.func.Renderer;

public interface Usable {
	public void Use();
	public void Draw_interface(GL2 gl, GLUT glut, Renderer renderer);
}
