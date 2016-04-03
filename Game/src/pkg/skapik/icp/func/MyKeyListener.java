package pkg.skapik.icp.func;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pkg.skapik.icp.main.Game;

public class MyKeyListener implements KeyListener {
	
	private Game game;
	
	public MyKeyListener(Game game){
		
		this.game = game;
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.game.key_pressed(e.getKeyCode(), e.getID());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.game.key_released(e.getKeyCode(), e.getID());
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
