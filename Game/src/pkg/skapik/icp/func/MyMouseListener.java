package pkg.skapik.icp.func;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import pkg.skapik.icp.main.Game;

public class MyMouseListener implements MouseListener, MouseMotionListener {

	private Game game;

	public MyMouseListener(Game game){
		this.game = game;
	}
	
	@Override
	public void mouseClicked(MouseEvent m) {
		
	}

	@Override
	public void mouseEntered(MouseEvent m) {

	}

	@Override
	public void mouseExited(MouseEvent m) {

	}

	@Override
	public void mousePressed(MouseEvent m) {
		this.game.mouse_clicked(m);
	}

	@Override
	public void mouseReleased(MouseEvent m) {

	}

	@Override
	public void mouseDragged(MouseEvent m) {
		this.game.mouse_dragged(m.getButton(), m.getX(), m.getY());
	}

	@Override
	public void mouseMoved(MouseEvent m) {
		this.game.mouse_moved(m.getX(), m.getY());
	}

}
