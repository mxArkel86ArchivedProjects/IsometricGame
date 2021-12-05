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
import java.awt.geom.Rectangle2D;

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
	WallObject wood_wall;

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

			float scaling = 1.0f;
			int scaled_w = (int) (CANW * scaling);
			int scaled_h = (int) (CANH * scaling);
			BufferedImage canvas = new BufferedImage(scaled_w, scaled_h, BufferedImage.TYPE_INT_RGB);

			paint_(canvas.getGraphics(), scaled_w, scaled_h);

			g2d.drawImage(canvas, 0, 0, CANW, CANH, Color.WHITE, null);
		}
	};

	float movement_speed = 0.75f;

	int gridsize = 125;
	float player_x = 0;
	float player_y = 0;
	float val;
	float v_mult = 0.5f;
	

	void paint_(Graphics g, int WIDTH, int HEIGHT) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) WIDTH, (int) HEIGHT);

		// GRID LINES
		int COL_N;
		int ROW_N;
		for (int x = 0; x <= (COL_N = (int) Math.ceil(WIDTH / gridsize) + 1); x += 1) {
			for (int y = 1; y <= (ROW_N = (int) (Math.ceil(HEIGHT / gridsize / v_mult * 2)) + 1); y += 1) {
				// ---------
				int offset = y % 2;
				int offset_p = (int) (offset * gridsize / 2);
				int dy = (int) (y * gridsize * v_mult / 2);
				int dx = (int) (x * gridsize)-offset_p;

				if (y % 2 == 0)
					g2d.drawImage(grass, dx, dy, new Color(0, 0, 0, 0), null);
				else
					g2d.drawImage(sand, dx, dy, new Color(0, 0, 0, 0), null);

				
				// draw basic poly
				g2d.setColor(new Color((int) (x * 255.0f / COL_N), 0, (int) (y * 255.0f / ROW_N)));
				int[] x_pts = { dx + gridsize / 2, dx + gridsize, dx + gridsize / 2,
						dx, dx + gridsize / 2 };// top, right, bottom, left
				int[] y_pts = { dy + 0, (int) (dy + gridsize * v_mult / 2), (int) (dy + gridsize * v_mult),
						(int) (dy + gridsize * v_mult / 2), dy + 0 };
				g.drawPolyline(x_pts, y_pts, x_pts.length);
			}
		}

		for (int x = 0; x <= (COL_N = (int) Math.ceil(WIDTH / gridsize) + 1); x += 1) {
			for (int y = 1; y <= (ROW_N = (int) (Math.ceil(HEIGHT / gridsize / v_mult * 2)) + 1); y += 1) {
				// ---------
				int offset = y % 2;
				int offset_p = (int) (offset * gridsize / 2);
				int dy = (int) (y * gridsize * v_mult / 2);
				int dx = (int) (x * gridsize)-offset_p;

				if(true){
				g.drawImage(wood_wall.bleft, dx, dy, new Color(0, 0, 0, 0), null);
				//g.drawImage(wood_wall.bright, dx+gridsize/2, dy, new Color(0, 0, 0, 0), null);
				//g.drawImage(wood_wall.tleft, dx+gridsize/2, dy-gridsize/2, new Color(0, 0, 0, 0), null);
				//g.drawImage(wood_wall.tright, dx+gridsize/2, dy-gridsize/4, new Color(0, 0, 0, 0), null);
				}
				
			}
		}

		for (int x = 0; x <= (COL_N = (int) Math.ceil(WIDTH / gridsize) + 1); x += 1) {
			for (int y = 1; y <= (ROW_N = (int) (Math.ceil(HEIGHT / gridsize / v_mult * 2)) + 1); y += 1) {
				// ---------
				int offset = y % 2;
				int offset_p = (int) (offset * gridsize / 2);
				int dy = (int) (y * gridsize * v_mult / 2);
				int dx = (int) (x * gridsize)-offset_p;

				if (offset == 1)
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);
				g.fillRect(dx - 2, dy - 2, 4, 4);
				g.setColor(Color.BLACK);

				// draw coordinate
				g.setFont(COORDS_F);
				String text = String.format("(%d %d)", x, y);
				int w_ = g.getFontMetrics().getMaxAdvance()*text.length();
				int h_ = g.getFontMetrics().getAscent();
				g.setColor(Color.WHITE);
				g.fillRect((int)(dx), (int)(dy), (int)(w_), (int)(h_));
				g.setColor(Color.BLACK);
				g.drawString(text, dx, dy + g.getFontMetrics().getAscent());
			}
		}

		// g.drawImage(grass, 300, 300, new Color(0, 0, 0, 0), null);

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

		// BufferedImage imgTgt = config.createCompatibleImage(rc.width, rc.height,
		// Transparency.TRANSLUCENT);
		BufferedImage imgTgt = new BufferedImage(rc.width, rc.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = imgTgt.createGraphics();

		// create a NEW rotation transformation around new center
		tf = AffineTransform.getRotateInstance(angle, rc.getWidth() / 2, rc.getHeight() / 2);
		g2d.setTransform(tf);
		g2d.drawImage(img, -rc.x, -rc.y, null);
		g2d.dispose();

		// drawOffset.x += rc.x;
		// drawOffset.y += rc.y;

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

					while (now - lastrender < (1000000000 / FPS)) {
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

	BufferedImage shear45Image(BufferedImage img, int x, int y) {
		int w = img.getWidth();
		int h = img.getHeight();

		AffineTransform tf = AffineTransform.getShearInstance(x, y);

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

		// BufferedImage imgTgt = config.createCompatibleImage(rc.width, rc.height,
		// Transparency.TRANSLUCENT);
		BufferedImage imgTgt = new BufferedImage(rc.width, rc.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = imgTgt.createGraphics();

		// create a NEW rotation transformation around new center
		g2d.setTransform(tf);
		g2d.drawImage(img, -rc.x, -rc.y, null);
		g2d.dispose();

		// drawOffset.x += rc.x;
		// drawOffset.y += rc.y;

		return imgTgt;
	}

	Image processTile(BufferedImage img) {
		// int new_size = (int)((img.getWidth()+img.getHeight())/Math.sqrt(2));
		// BufferedImage grass_1 = new BufferedImage(new_size, new_size,
		// BufferedImage.TYPE_INT_ARGB);
		BufferedImage img1 = rotateImage(img, Math.PI / 4);
		return img1.getScaledInstance(gridsize, (int) (gridsize * v_mult), 0);
	}

	WallObject processWall(BufferedImage front, BufferedImage back) {
		WallObject wobj = new WallObject();
		BufferedImage img1 = shear45Image(front, 0, 1);
		wobj.bleft = img1.getScaledInstance((int) (gridsize / 2), (int) (gridsize / 2), 0);
		BufferedImage img2 = shear45Image(front, 0, -1);
		wobj.bright = img2.getScaledInstance((int) (gridsize / 2), (int) (gridsize / 2), 0);
		BufferedImage img3 = shear45Image(back, 0, -1);
		wobj.tleft = img3.getScaledInstance((int) (gridsize / 2), (int) (gridsize / 2), 0);
		BufferedImage img4 = shear45Image(back, 0, 1);
		wobj.tright = img4.getScaledInstance((int) (gridsize / 2), (int) (gridsize / 2), 0);
		return wobj;
	}

	void ImportAssets() {
		grass = processTile(ImageFile("assets/grass.jpeg"));
		sand = processTile(ImageFile("assets/sand.jpeg"));
		wood_wall = processWall(ImageFile("assets/stone.jpeg"), ImageFile("assets/wood.jpeg"));
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
		// initScreenRefresh();
		// game timer
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				tick(TICK_RATE);
			}

		}, 0, TICK_RATE);

		t.schedule(new TimerTask() {

			@Override
			public void run() {
				panel.repaint();
			}

		}, 0, (int) (1000 / FPS));

		setTitle("Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
}

class WallObject {
	public Image bleft;
	public Image bright;
	public Image tleft;
	public Image tright;
}
