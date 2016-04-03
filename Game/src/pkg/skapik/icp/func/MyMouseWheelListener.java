package pkg.skapik.icp.func;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import pkg.skapik.icp.main.Game;

public class MyMouseWheelListener implements MouseWheelListener{

	private Game game;
	
	public MyMouseWheelListener(Game game){
		this.game = game;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		this.game.mouse_wheel_moved(mwe);
	}

}
