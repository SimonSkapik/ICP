package pkg.skapik.icp.main;


import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
// JAVAAAAAA
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
// OpenGL 
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

// OpenCV
import org.opencv.core.Core;

// OUR OWN OP CODE!
import pkg.skapik.icp.assets.Player;
import pkg.skapik.icp.func.*;



public class Game{

	// OpenGL 
	private static FPSAnimator animator;
    public int width;
    public int height;
    private static Point center;
    private static Point center_on_screen;
    public static Rectangle screenSize;
    public static JFrame frame;

	private BufferedImage cursorImg;
	private Cursor blankCursor;
	
	// Game
	private Player player;
	private Robot robot_controller;
    private JFrame game_frame;
    
	public Game(int w, int h, JFrame frame) {
		width = w;
	    height = h;
		game_frame = frame;
	    cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	    blankCursor = Toolkit.getDefaultToolkit().createCustomCursor((Image)cursorImg, new Point(0, 0), "blank cursor");
	}

	public void init(Renderer renderer){ // Initialization - called when you initially want to initialize the game! 
		this.player = new Player(renderer); // YOU ARE HERE
		
		try {
			this.robot_controller = new Robot(); // Robot pro systemove rizeni mysi
			robot_controller.mouseMove(center_on_screen.x,center_on_screen.y); // Sup krysu na stred
			player.init_view_coords(center.x,center.y); // In case you haven't noticed you are looking right here!
		} catch (AWTException e) {
			e.printStackTrace(); // OOOOPS
		}
	}

	///////// Main Method //////////
		
	public static void main(String[] args) {
		// Nacteni knihoven
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Jak velky okno?
	    screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	    /*width = 1280;// screenSize.width; //(int) screenSize.getWidth();
	    height = 762;// screenSize.height; //(int) screenSize.getHeight();
	    */
	    
	    
	    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor((Image)cursorImg, new Point(0, 0), "blank cursor");
	    
	    // Nove okno
	    GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
	    frame = new JFrame("Frame name");
	    frame.setAlwaysOnTop(false);
	    frame.setSize(screenSize.width, screenSize.height);
	    frame.getContentPane().setCursor(blankCursor); // vlastni kurzor
	    frame.setUndecorated(true); // borderless
	    frame.add(canvas);
	    
	    // Pridat platno a vykreslovaci smycku
	    animator = new FPSAnimator(30);
	    animator.add(canvas);
	    animator.start();
	    
	    // FullScreeen
	    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    
	    // AAAAAAND OPEEEEEN!!!
	    frame.setVisible(true);
	    
	    // stred obrazovky
	    center = new Point((screenSize.width/2),(screenSize.height/2));
	    center_on_screen = new Point((canvas.getLocationOnScreen().x + screenSize.width/2),(canvas.getLocationOnScreen().y + screenSize.height/2));
	    
	    // navazani listeneru
	    Game game = new Game(screenSize.width, screenSize.height, frame);
	    Renderer renderer = new Renderer(game);
	    canvas.addGLEventListener(renderer);
	    canvas.addKeyListener((java.awt.event.KeyListener) new MyKeyListener(game));
	    canvas.addMouseListener(new MyMouseListener(game));
	    canvas.addMouseWheelListener(new MyMouseWheelListener(game));
	    canvas.addMouseMotionListener(new MyMouseListener(game));
	    canvas.requestFocus();
	    frame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	            System.exit(0);
	        }
	    });
	}

	public void key_pressed(int code, int id) { // Kocka slapla na klavesnici
		if(code == 87 || code == 83 || code == 65 || code == 68 || code == 32 || code == 17){
			player.Controll(code, id);
		}else if(code == 16){
			player.set_sneak(true);
		}else if(code == 27){
			String[] buttons = { "Yeah... :-|", "Psych! JK :D" };
			int dialogResult = JOptionPane.showOptionDialog(null, "Already leaving? :-(", "Srsly??", JOptionPane.YES_NO_OPTION,
			        JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
			
			if(dialogResult == JOptionPane.YES_OPTION){
				if(player.is_in_inventory())
					player.open_inventory();
				System.exit(0);
			}
		}else if(code >= 49 && code <= 57){
			player.hbar_select(code - 49);
		}else if(code == 69){
			if(player.is_in_inventory()){
				robot_controller.mouseMove(center_on_screen.x,center_on_screen.y);
			}
			player.open_inventory();
		}else if(code == 84){
			player.get_renderer().add_time(-100);
		}else if(code == 90){
			player.get_renderer().add_time(100);
		}else if(code == 67){
			player.toggle_cam();
		}else{
			System.out.println("Key: "+code);
		}
	}
	
	public void key_released(int code, int id) { // Kocka slezla z klavesnice
		if(code == 87 || code == 83 || code == 65 || code == 68 || code == 32 || code == 17){
			player.Controll(code, id);
		}else if(code == 16){
			player.set_sneak(false);
		}
	}

	public void mouse_dragged(int button, int x, int y) { // Full out spraaaaay!
		this.mouse_moved(x,y);
	}

	public void mouse_moved(int x, int y) { // Krysy se vozej mo mysi
		if(player.is_in_inventory()){
			game_frame.getContentPane().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			player.inventory_move(x,y);
		}else{	
			game_frame.getContentPane().setCursor(blankCursor);
			if(x != center.x || y != center.y){
				player.Look(x,y);
			}
			robot_controller.mouseMove(center_on_screen.x,center_on_screen.y);
		}
	}

	public Player get_player() { // Do I seriously need to describe this one?
		return this.player;
	}

	public void mouse_clicked(MouseEvent m) {
		if(this.player.is_in_inventory()){
			this.player.inventory_click(m);
		}else{
			if(m.getButton() == 1){ // Left click - HIT IT BABY
				this.player.Hit();
			}else if(m.getButton() == 3){ // Right click - USE IT OR LOOSE IT
				this.player.Use();
			}
		}
	}
	
	public void revive(Renderer rnd){
		this.player = new Player(rnd);
	}

	public void mouse_wheel_moved(MouseWheelEvent mwe) { // Mouse WHEEEEEEEE    L
		this.player.Scroll(mwe.getWheelRotation());
	}
}
