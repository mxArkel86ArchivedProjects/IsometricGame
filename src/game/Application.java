package game;

import game.Peripherals;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.security.KeyPairGeneratorSpi;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;

import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Font;

public class Application extends JFrame {
	// App Constants
	static private long STARTTIME = System.currentTimeMillis();
	final float FPS = 60;
	final int TICK_RATE = 10; // TICK RATE
	Font DEBUG_BOX_F = new Font("Courier", Font.PLAIN, 14);
	Font COORDS_F = new Font("Courier", Font.PLAIN, 8);
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = env.getDefaultScreenDevice();
    GraphicsConfiguration config = device.getDefaultConfiguration();

	// Application Semi-Constants
	int CANW;
	int CANH;
	Peripherals PERI;

	// Game Assets
	Image grass;
	Image sand;

	public JPanel panel = new JPanel() {
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			// super.paintComponent(g);

			// Custom Screen Options
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);

			rh.put(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);

			g2d.setRenderingHints(rh);

			paint_(g);
		}
	};

	float movement_speed = 0.75f;

	int gridsize = 150;
	float player_x = 0;
	float player_y = 0;
	float val;
	float v_mult = 0.5f;

	void paint_(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) CANW, (int) CANH);

		// GRID LINES
		int COL_N;
		int ROW_N;
		for (int x = 0; x < (COL_N = (CANW / gridsize) + 2); x += 1) {
			int dx = (int)(x*gridsize);
			for (int y = 2; y < (ROW_N = (int) (CANH / gridsize / v_mult * 2)); y += 1) {
				// ---------
				int offset = y % 2;
				int offset_p = (int)(offset * gridsize / 2);
				int dy = (int) (y * gridsize * v_mult / 2);

				if((x+y)%2==0)
					g.drawImage(grass, dx-offset_p, dy, new Color(0,0,0,0), null);
				else
					g.drawImage(sand, dx-offset_p, dy, new Color(0,0,0,0), null);
				// draw basic poly
				g2d.setColor(new Color((int) (x * 255.0f / COL_N), 0, (int) (y * 255.0f / ROW_N)));
				int[] x_pts = { dx + gridsize / 2 - offset_p, dx + gridsize - offset_p, dx + gridsize / 2 - offset_p,
						dx - offset_p, dx + gridsize / 2 - offset_p };// top, right, bottom, left
				int[] y_pts = { dy + 0, (int) (dy + gridsize * v_mult / 2), (int) (dy + gridsize * v_mult),
						(int) (dy + gridsize * v_mult / 2), dy + 0 };
				g.drawPolyline(x_pts, y_pts, x_pts.length);

				

				// draw marker
				if (offset == 1)
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);
				g.fillRect(dx - 2 - offset_p, dy - 2, 4, 4);
				g.setColor(Color.BLACK);

				// draw coordinate
				g.setFont(COORDS_F);
				g.drawString(String.format("(%d %d)", x, y), dx, dy + g.getFontMetrics().getAscent());
			}
		}

		//g.drawImage(grass, 300, 300, new Color(0, 0, 0, 0), null);

		g.setColor(Color.BLUE);
		g.fillRect((int) player_x, (int) player_y, 40, 40);

		// DEBUGGING
		g.setFont(DEBUG_BOX_F);
		g.drawString(String.format("w=%d a=%d s=%d d=%d",
				PERI.keyPressed('w') ? 1 : 0,
				PERI.keyPressed('a') ? 1 : 0,
				PERI.keyPressed('s') ? 1 : 0,
				PERI.keyPressed('d') ? 1 : 0),
				0, g.getFontMetrics().getAscent());
		g.drawString(String.format("player_x=%.4f player_y=%.4f", player_x, player_y),
				0, 2 * g.getFontMetrics().getAscent());
		g.dispose();
	}

	void tick(int tr) {

		int intent_x = 0;
		int intent_y = 0;
		if (PERI.keyPressed('a'))
			intent_x--;
		if (PERI.keyPressed('d'))
			intent_x++;
		if (PERI.keyPressed('w'))
			intent_y++;
		if (PERI.keyPressed('s'))
			intent_y--;

		if (intent_x != 0 || intent_y != 0) {

			double intent_direction = Math.atan2(intent_y, intent_x);
			double displacement_x = Math.cos(intent_direction) * movement_speed;
			double displacement_y = Math.sin(intent_direction) * movement_speed;

			player_x += displacement_x;
			player_y -= displacement_y;
		}
	}

	BufferedImage rotateImage(BufferedImage img, double angle) {
		int w = img.getWidth();
		int h = img.getHeight();
	
		AffineTransform tf = AffineTransform.getRotateInstance(angle, w / 2.0, h / 2.0);
	
		// get coordinates for all corners to determine real image size
		Point2D[] ptSrc = new Point2D[4];
		ptSrc[0] = new Point(0, 0);
		ptSrc[1] = new Point(w, 0);
		ptSrc[2] = new Point(w, h);
		ptSrc[3] = new Point(0, h);
	
		Point2D[] ptTgt = new Point2D[4];
		tf.transform(ptSrc, 0, ptTgt, 0, ptSrc.length);
	
		Rectangle rc = new Rectangle(0, 0, w, h);
	
		for (Point2D p : ptTgt) {
			if (p.getX() < rc.x) {
				rc.width += rc.x - p.getX();
				rc.x = (int) p.getX();
			}
			if (p.getY() < rc.y) {
				rc.height += rc.y - p.getY();
				rc.y = (int) p.getY();
			}
			if (p.getX() > rc.x + rc.width)
				rc.width = (int) (p.getX() - rc.x);
			if (p.getY() > rc.y + rc.height)
				rc.height = (int) (p.getY() - rc.y);
		}
	
		BufferedImage imgTgt = config.createCompatibleImage(rc.width, rc.height, Transparency.TRANSLUCENT);
		//BufferedImage imgTgt = new BufferedImage(rc.width, rc.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = imgTgt.createGraphics();
	
		// create a NEW rotation transformation around new center
		tf = AffineTransform.getRotateInstance(angle, rc.getWidth() / 2, rc.getHeight() / 2);
		g2d.setTransform(tf);
		g2d.drawImage(img, -rc.x, -rc.y, null);
		g2d.dispose();
	
		//drawOffset.x += rc.x;
		//drawOffset.y += rc.y;
	
		return imgTgt;
	}
	

	void initScreenRefresh() {
		new Thread() {
			public void run() {
				long lastlong = System.nanoTime();
				float delta = 0;
				long timer = System.currentTimeMillis();
				final double physicstick = 1000000000 / 140;

				while (true) {
					long now = System.nanoTime();
					delta += (now - lastlong) / physicstick;
					long lastrender = System.nanoTime();
					lastlong = now;
					while (delta >= 1) {
						delta--;
						// physics code 8% CPU, is that normal?
					}

					panel.repaint();
					lastrender = now;

					while (now - lastrender < (1000000000 / 60)) {
						try {
							Thread.sleep(1);
							// Without sleeping there is a 100% CPU usage CRAZY!
						} catch (InterruptedException ie) {
						}
						now = System.nanoTime();
					}
				}
			}
		}.start();
	}

	BufferedImage ImageFile(String name) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(name));
		} catch (IOException e) {
		}
		return img;
	}

	Image processTile(BufferedImage img){
		//int new_size = (int)((img.getWidth()+img.getHeight())/Math.sqrt(2));
		//BufferedImage grass_1 = new BufferedImage(new_size, new_size, BufferedImage.TYPE_INT_ARGB);
		BufferedImage img1 = rotateImage(img, Math.PI/4);
		return img1.getScaledInstance(gridsize, (int)(gridsize*v_mult), 0);
	}

	void ImportAssets(){
		grass = processTile(ImageFile("assets/grass.jpeg"));
		sand = processTile(ImageFile("assets/sand.jpeg"));
		
	}

	public void InitializeApplication() {
		add(panel);

		int init_w = 960;
		int init_h = 600;
		setSize(init_w, init_h);

		CANW = init_w;
		CANH = init_h;

		PERI = new Peripherals();
		this.addKeyListener(PERI);

		ImportAssets();
		// Event Timers (screen refresh/game time)
		Timer t = new Timer();
		initScreenRefresh();
		// game timer
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				tick(TICK_RATE);
			}

		}, 0, TICK_RATE);

		/*t.schedule(new TimerTask() {

			@Override
			public void run() {
				panel.repaint();
			}

		}, 0, (int) (1000 / FPS));*/

		setTitle("Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
}
