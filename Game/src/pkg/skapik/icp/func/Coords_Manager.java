package pkg.skapik.icp.func;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import com.jogamp.common.nio.Buffers;



public class Coords_Manager {
	
	private Mat image;
	private int width, height, tex_width, tex_height;
	private ArrayList<Point[]> tex_coords;
	private int tex_cutoff;
	
	public  Coords_Manager(String texture, int texs_per_row){
		
		image = Imgcodecs.imread(texture);
		width = image.width();
		height = image.height();
		tex_width = (int)(width/texs_per_row);
		tex_height = (int)(height/texs_per_row);
		tex_coords = new ArrayList<>();
		tex_cutoff = 1;
		cut_texes(texs_per_row);
		
	}

	public void set_cutoff(int cutoff){
		tex_cutoff = 1;
		//tex_cutoff = cutoff;
	}

	private void cut_texes(int count){
		Point[] coords;
		int x,y;
		for(int j = 0; j < count; j++){
			for(int i = 0; i < count; i++){
				coords = new Point[4];
				x = i*tex_width;
				y = j*tex_height;
				coords[0] = new Point(f(x + tex_cutoff),f(y + tex_cutoff));
				coords[1] = new Point(f(x + tex_cutoff),f(y+tex_height - tex_cutoff));
				coords[2] = new Point(f(x+tex_width - tex_cutoff),f(y+tex_height - tex_cutoff));
				coords[3] = new Point(f(x+tex_width - tex_cutoff),f(y + tex_cutoff));
				tex_coords.add(coords);
			}
		}
	}
	
	private float f(int num){
		return to_respective_float(num);
	}
	
	private float to_respective_float(int num){
		return (float)((float)num/(float)width);
	}
	
	public FloatBuffer get_texture_coords(int id){
		return this.get_texture_coords(new boolean[]{true,false,false,false,false,false},1,id,id,id,id,id,id,0);
	}
	
	public FloatBuffer get_texture_coords(boolean[] draw_faces, int faces_num, int id, int cutoff){
		return this.get_texture_coords(draw_faces,faces_num,id,id,id,id,id,id,cutoff);
	}

	public FloatBuffer get_texture_coords(boolean[] draw_faces, int faces_num, int side_id, int top_id, int bot_id, int cutoff) {
		return this.get_texture_coords(draw_faces,faces_num,side_id,side_id,side_id,side_id,top_id,bot_id,cutoff);
	}
	
	public FloatBuffer get_texture_coords(boolean[] draw_faces, int faces_num, int side_id, int cap_id, int cutoff) {
		return this.get_texture_coords(draw_faces,faces_num,side_id,side_id,side_id,side_id,cap_id,cap_id,cutoff);
	}
	
	public FloatBuffer get_texture_coords(boolean[] draw_faces, int faces_num, int front_id, int right_id, int back_id, int left_id, int top_id, int bot_id, int cutoff) {
		FloatBuffer buff = Buffers.newDirectFloatBuffer(faces_num*8);
		Point p;
		//float cutoff_float = (float)cutoff/(float)width;
		float cutoff_float = (float)0/(float)width;
		if(draw_faces[0]){
			p = tex_coords.get(front_id)[0];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y + cutoff_float);
			p = tex_coords.get(front_id)[1];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(front_id)[2];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(front_id)[3];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y + cutoff_float);
		}
		if(draw_faces[1]){
			p = tex_coords.get(right_id)[0];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y + cutoff_float);
			p = tex_coords.get(right_id)[1];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(right_id)[2];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(right_id)[3];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y + cutoff_float);
		}
		if(draw_faces[2]){
			p = tex_coords.get(back_id)[0];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y + cutoff_float);
			p = tex_coords.get(back_id)[1];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(back_id)[2];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(back_id)[3];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y + cutoff_float);
		}
		if(draw_faces[3]){
			p = tex_coords.get(left_id)[0];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y + cutoff_float);
			p = tex_coords.get(left_id)[1];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(left_id)[2];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(left_id)[3];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y + cutoff_float);
		}
		if(draw_faces[4]){
			p = tex_coords.get(top_id)[0];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y + cutoff_float);
			p = tex_coords.get(top_id)[1];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(top_id)[2];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(top_id)[3];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y + cutoff_float);
		}
		if(draw_faces[5]){
			p = tex_coords.get(bot_id)[0];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y + cutoff_float);
			p = tex_coords.get(bot_id)[1];
			buff.put((float) p.x + cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(bot_id)[2];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y - cutoff_float);
			p = tex_coords.get(bot_id)[3];
			buff.put((float) p.x - cutoff_float);
			buff.put((float) p.y + cutoff_float);
		}
		buff.rewind();
		return buff;
	}


}
