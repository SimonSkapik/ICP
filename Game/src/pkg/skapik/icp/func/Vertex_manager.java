package pkg.skapik.icp.func;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

public class Vertex_manager {

	private static FloatBuffer vertices;

	public static FloatBuffer get_Block_vertices(boolean[] draw_faces, int faces_to_draw, Position pos) {
		int x = pos.getX_I();
		int y = pos.getY_I();
		int z = pos.getZ_I();
		int x1 = x+1;
		int y1 = y+1;
		int z1 = z+1;
		
		float vert[] = {x,y1,z1, x,y,z1, x1,y,z1, x1,y1,z1, x1,y1,z1, x1,y,z1, x1,y,z, x1,y1,z,  // front, right       // 6x4x3 of vertex coords
				x1,y1,z, x1,y,z, x,y,z, x,y1,z, x,y1,z, x,y,z, x,y,z1, x,y1,z1,  // back, left
				x,y1,z, x,y1,z1, x1,y1,z1, x1,y1,z, x1,y,z, x1,y,z1, x,y,z1, x,y,z,}; // top, bot
		vertices = Buffers.newDirectFloatBuffer(faces_to_draw*12);
		for (int i = 0; i < 6; i++){
			if(draw_faces[i]){
				for (int j = 0; j < 12; j++){
					vertices.put(vert[i*12+j]);
				}
			}
		}
		vertices.rewind();
		return vertices;
	}

}
