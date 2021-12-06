package game;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.BasicStroke;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Font;

public class Application extends JFrame {
	// App Constants
	// static private long STARTTIME = System.currentTimeMillis();
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
	BufferedImage grass_;
	BufferedImage sand_;
	Image glass;
	BufferedImage glass_;
	WallObject wood_wall;
	BufferedImage stone_;
	BufferedImage wood_;
	BufferedImage debug_texture_;

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

			// DEBUGGING
			String str = "[";
			for (double val : dynamic_params) {
				str += String.format("%.2f ", val);
			}
			str += "]";

			g.setFont(DEBUG_BOX_F);
			g.drawString(String.format("w=%d a=%d s=%d d=%d | d=%.2f vals=%s",
					PERI.keyPressed('w') ? 1 : 0,
					PERI.keyPressed('a') ? 1 : 0,
					PERI.keyPressed('s') ? 1 : 0,
					PERI.keyPressed('d') ? 1 : 0,
					delta, str),
					0, g.getFontMetrics().getAscent());
			g.drawString(String.format("player_x=%.4f player_y=%.4f", player_x, player_y),
					0, 2 * g.getFontMetrics().getAscent());
		}
	};

	float movement_base_speed = 0.04f;
	float sprint_mult = 2f;

	int gridsize = 150;
	float player_x = 0;
	float player_y = 0;

	double viewing_angle = Math.PI / 6;

	void paint_(Graphics g, int WIDTH, int HEIGHT) {
		Graphics2D g2d = (Graphics2D) g;
		viewing_angle = Math.toRadians(dynamic_params[0]);

		wood_wall = processWall(stone_, wood_);
		grass = processTile(grass_);
		sand = processTile(sand_);
		glass = processTile(glass_);
		double v_mult = Math.sin(viewing_angle);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) WIDTH, (int) HEIGHT);

		// GRID LINES
		int COL_N;
		int ROW_N;
		float tile_offset_x = player_x%1;
		float tile_offset_y = player_y%1;
		for (int x = 0; x <= (COL_N = (int) Math.ceil(WIDTH / gridsize) + 1); x += 1) {
			for (int y = 0; y <= (ROW_N = (int) (Math.ceil(HEIGHT / gridsize / v_mult * 2)) + 1); y += 1) {
				int offset = y % 2;
				int offset_p = (int) (offset * gridsize / 2);
				int dy = (int) ((y-2*tile_offset_y) * gridsize * v_mult / 2);
				int dx = (int) ((x-tile_offset_x) * gridsize + offset_p);

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

		for (int x = 4; x <= (COL_N = (int) Math.ceil(WIDTH / gridsize) + 1) && x < 5; x += 1) {
			for (int y = 9; y <= (ROW_N = (int) (Math.ceil(HEIGHT / gridsize / v_mult * 2)) + 1) && y < 10; y += 1) {
				// ---------
				int offset = y % 2;
				int offset_p = (int) (offset * gridsize / 2);
				int dy = (int) ((y-2*tile_offset_y) * gridsize * v_mult / 2);
				int dx = (int) ((x-tile_offset_x) * gridsize + offset_p);

				g.drawImage(wood_wall.tleft.img, dx - gridsize / 2, dy + wood_wall.tleft.offset-gridsize, new Color(0, 0, 0, 0), null);
				g.drawImage(wood_wall.tright.img, dx, dy-wood_wall.tright.offset, new Color(0, 0, 0, 0), null);
				g.drawImage(wood_wall.bright.img, dx, dy+wood_wall.bleft.offset2-wood_wall.bleft.offset, new Color(0, 0, 0, 0),null);
				g.drawImage(wood_wall.bleft.img, dx - gridsize / 2, dy+wood_wall.bleft.offset2-wood_wall.bleft.offset, new Color(0, 0, 0, 0), null);
				g2d.drawImage(glass, dx-gridsize/2, dy-wood_wall.bleft.offset2-gridsize/2, new Color(0, 0, 0, 0), null);
			}
		}

		for (int x = 0; x <= (COL_N = (int) Math.ceil(WIDTH / gridsize) + 1); x += 1) {
			for (int y = 1; y <= (ROW_N = (int) (Math.ceil(HEIGHT / gridsize / v_mult * 2)) + 1); y += 1) {
				// ---------
				int offset = y % 2;
				int offset_p = (int) (offset * gridsize / 2);
				int dy = (int) ((y-2*tile_offset_y) * gridsize * v_mult / 2);
				int dx = (int) ((x-tile_offset_x) * gridsize) + offset_p;

				if (offset == 1)
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);
				g.fillRect(dx - 2, dy - 2, 4, 4);
				g.setColor(Color.BLACK);

				// draw coordinate
				g.setFont(COORDS_F);
				String text = String.format("(%d %d)", x, y);
				String text2 = String.format("(%d %d)", (int)x + (int)player_x, (int)y+2*(int)(player_y));
				int w_ = g.getFontMetrics().getMaxAdvance() * text.length();
				int w_2 = g.getFontMetrics().getMaxAdvance() * text.length();
				int h_ = g.getFontMetrics().getAscent();
				g.setColor(Color.WHITE);
				g.fillRect((int) (dx), (int) (dy), (int) (Math.max(w_, w_2)), (int) (h_*2));
				g.setColor(Color.BLACK);
				g.drawString(text, dx, dy + g.getFontMetrics().getAscent());
				g.drawString(text2, dx, dy + g.getFontMetrics().getAscent()*2);
			}
		}

		// g.drawImage(grass, 300, 300, new Color(0, 0, 0, 0), null);
		g.dispose();
	}

	boolean pressing_switch = false;
	int param_index = 0;
	double delta = 0.05;

	void Square() {
		int min_side = Math.min(debug_texture_.getWidth(), debug_texture_.getHeight());
		BufferedImage img = debug_texture_.getSubimage((int) ((debug_texture_.getWidth() - min_side) / 2),
				(int) ((debug_texture_.getHeight() - min_side) / 2), min_side, min_side);
	}

	double dynamic_params[] = { Math.PI/6, 0 };

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
			float sprint = PERI.keyPressed(KeyEvent.VK_SHIFT)?sprint_mult:1;
			double intent_direction = Math.atan2(intent_y, intent_x);
			double displacement_x = Math.cos(intent_direction) * movement_base_speed*sprint;
			double displacement_y = Math.sin(intent_direction) * movement_base_speed*sprint;

			player_x += displacement_x;
			player_y -= displacement_y;
		}

		if (!pressing_switch && PERI.keyPressed('l')) {
			pressing_switch = true;
			param_index++;
			if (param_index >= dynamic_params.length)
				param_index = 0;
		}
		if (!PERI.keyPressed('l')) {
			pressing_switch = false;
		}

		if (PERI.keyPressed('k')) {
			dynamic_params[param_index] = 0;
		}

		if (PERI.keyPressed(KeyEvent.VK_UP)) {
			dynamic_params[param_index] += delta;
		}
		if (PERI.keyPressed(KeyEvent.VK_DOWN)) {
			dynamic_params[param_index] -= delta;
		}
		if (PERI.keyPressed(KeyEvent.VK_RIGHT)) {
			delta += 0.01;
		}
		if (PERI.keyPressed(KeyEvent.VK_LEFT)) {
			if (delta - 0.01 >= 0)
				delta -= 0.01;
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

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	ShearReturn shearImage(BufferedImage img1, int width, int height, double angle, double scale) {
		BufferedImage img = toBufferedImage(img1.getScaledInstance(width, height, 0));
		// BufferedImage img = toBufferedImage(img2.getScaledInstance(100, 100, 0));
		int w = img.getWidth();
		int h = img.getHeight();

		AffineTransform tf = new AffineTransform(
				1, -Math.sin(angle), 0, scale, 0, 0);

		Point2D[] ptSrc = new Point2D[4];
		ptSrc[0] = new Point(0, 0);// top left
		ptSrc[1] = new Point(w, 0);// top right
		ptSrc[2] = new Point(w, h);// bottom right
		ptSrc[3] = new Point(0, h);// bottom left

		Point2D[] ptTgt = new Point2D[4];
		tf.transform(ptSrc, 0, ptTgt, 0, ptSrc.length);

		int h1 = (int) (Math.abs(h - ptTgt[2].getY()) + h);
		int w1 = (int) (ptTgt[1].getX() - ptTgt[0].getX());
		BufferedImage imgout = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d1 = imgout.createGraphics();

		ShearReturn ret = new ShearReturn();

		g2d1.transform(tf);
		int dy = 0;
		if (angle < 0)
			dy = (int) (h - ptTgt[2].getY() + ptTgt[1].getY());
		else if (angle >= 0) {
			dy = (int) (h - ptTgt[2].getY());
		}
		g2d1.drawImage(img, 0, dy, null);
		g2d1.dispose();

		ret.img = imgout;
		ret.offset = (int)(ptTgt[2].getY());
		ret.offset2 = (int)(ptTgt[1].getY());
		return ret;
	}

	Image processTile(BufferedImage img) {
		BufferedImage img1 = rotateImage(img, Math.PI / 4);
		return img1.getScaledInstance(gridsize, (int) (gridsize * Math.sin(viewing_angle)), 0);
	}

	WallObject processWall(BufferedImage front, BufferedImage back) {
		double f = viewing_angle;
		double scaling = 1;
		int size = gridsize / 2;
		WallObject wobj = new WallObject();
		wobj.bleft = shearImage(front, size, size, -f, scaling);
		wobj.bright = shearImage(front, size, size, f, scaling);
		wobj.tleft = shearImage(back, size, size, f, scaling);
		wobj.tright = shearImage(back, size, size, -f, scaling);
		return wobj;
	}

	void ImportAssets() {
		grass = processTile(ImageFile("assets/grass.jpeg"));
		sand = processTile(ImageFile("assets/sand.jpeg"));
		grass_ = ImageFile("assets/grass.jpeg");
		sand_ = ImageFile("assets/sand.jpeg");
		glass_ = ImageFile("assets/glass.png");
		glass = processTile(ImageFile("assets/glass.png"));
		stone_ = ImageFile("assets/stone.jpeg");
		wood_ = ImageFile("assets/wood.jpeg");
		wood_wall = processWall(ImageFile("assets/stone.jpeg"), ImageFile("assets/wood.jpeg"));
		debug_texture_ = ImageFile("assets/debug_grad.jpeg");
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
	public ShearReturn bleft;
	public ShearReturn bright;
	public ShearReturn tleft;
	public ShearReturn tright;
}
class ShearReturn{
	BufferedImage img;
	int offset;
	int offset2;
}
