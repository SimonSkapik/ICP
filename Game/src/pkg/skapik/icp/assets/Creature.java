package pkg.skapik.icp.assets;

import javax.media.opengl.GL2;

import pkg.skapik.icp.func.Boundery_box;
import pkg.skapik.icp.func.Position;
import pkg.skapik.icp.func.Vector;

public interface Creature {

	Vector get_move_direction();
	Position get_position();
	Boundery_box get_boundery();
	void Update();
	void draw(GL2 gl);
	void Get_hit(int dmg,Vector dmg_direction);


}
