package game;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Font;

public class Application extends JFrame {
	// App Constants
	// static private long STARTTIME = System.currentTimeMillis();
	final float FPS = 60;
	final int TICK_RATE = 10; // TICK RATE
	Font DEBUG_BOX_F = new Font("Courier", Font.PLAIN, 14);
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice device = env.getDefaultScreenDevice();
	GraphicsConfiguration config = device.getDefaultConfiguration();

	// Application Semi-Constants
	int CANW;
	int CANH;
	Peripherals PERI;
	AssetManager AMGR;

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
			// String str = "[";
			// for (double val : dynamic_params) {
			// 	str += String.format("%.2f ", val);
			// }
			// str += "]";

			// g.setFont(DEBUG_BOX_F);
			// g.drawString(String.format("w=%d a=%d s=%d d=%d | d=%.2f vals=%s",
			// 		PERI.keyPressed('w') ? 1 : 0,
			// 		PERI.keyPressed('a') ? 1 : 0,
			// 		PERI.keyPressed('s') ? 1 : 0,
			// 		PERI.keyPressed('d') ? 1 : 0,
			// 		delta, str),
			// 		0, g.getFontMetrics().getAscent());
			// g.drawString(String.format("player_x=%.4f player_y=%.4f", player_x, player_y),
			// 		0, 2 * g.getFontMetrics().getAscent());
		}
	};

	float movement_base_speed = 0.04f;
	float sprint_mult = 2f;

	final int TILEBASESIZE = 60;
	double zoom_mult = 1;
	final int TILE_BUFFER = 3;
	float player_x = 0;
	float player_y = 0;

	// double viewing_angle = Math.PI / 6;

	void paint_(Graphics g_base, int WIDTH, int HEIGHT) {
		Graphics2D g = (Graphics2D) g_base;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) WIDTH, (int) HEIGHT);

		int COLUMNS = (int) ((int) Math.ceil(WIDTH / TILEBASESIZE) / zoom_mult);
		int ROWS = (int) ((int) Math.ceil(HEIGHT / TILEBASESIZE * 4) / zoom_mult);
		double tilesize = TILEBASESIZE * zoom_mult;
		double halftile = tilesize / 2;
		
		

		Font coords_f = new Font("Courier", Font.PLAIN, (int) (4 * zoom_mult));
		//System.out.println(String.format("COLUMNS=%d  ROWS=%d  tilesize=%.1f", COLUMNS, ROWS, tilesize));
		for (float y = 0; y <= ROWS; y++) {
			for (float x = 0; x <= COLUMNS; x++) {
				int odd_level = (int)(y % 2);

				int dx = (int) (x * tilesize + odd_level * tilesize/2);
				int dy = (int) (y * tilesize/4);

				int centerx = (int) (x * tilesize + odd_level * tilesize/2+halftile);
				int centery = (int) (y * tilesize / 2+halftile/2);
				int cx = (int) Math.ceil(x+y/2);
				int cy = (int) Math.ceil(x-y/2);

				if (odd_level == 1) {
					g.drawImage(AMGR.getFlat("sand").src, dx, dy, (int) tilesize + TILE_BUFFER,
							(int) halftile + TILE_BUFFER, null);
				} else
					g.drawImage(AMGR.getFlat("grass").src, dx, dy, (int) tilesize + TILE_BUFFER,
							(int) halftile + TILE_BUFFER, null);
				

				if (odd_level == 1)
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);

				// int xp[] = {(int)(dx+tilesize/2), (int)(dx+tilesize), (int)(dx+tilesize/2), (int)(dx), (int)(dx+tilesize/2)};
				// int yp[] = {dy, (int)(dy+tilesize/2), (int)(dy+tilesize), (int)(dy+tilesize/2), dy};
				// g.drawPolygon(xp, yp, xp.length);
				
				//g.fillRect(dx - 2, dy - 2, 4, 4);

				
				//g.drawString(String.format("(%d %d)", (int)x, (int)y), centerx, centery+g.getFontMetrics().getAscent());
				//g.drawString(String.format("(%d %d)", cx, cy), centerx, centery+g.getFontMetrics().getAscent()*2);
				
				
			}
		}

		for (float y = 0; y <= ROWS; y++) {
			for (float x = 0; x <= COLUMNS; x++) {
				int odd_level = (int)(y % 2);

				int dx = (int) (x * tilesize + odd_level * tilesize/2);
				int dy = (int) (y * tilesize/4);

				int centerx = (int) (x * tilesize + odd_level * tilesize/2+halftile);
				int centery = (int) (y * tilesize / 4+halftile/2);
				int cx = (int) Math.ceil(x+y/2);
				int cy = (int) Math.ceil(x-y/2);

				g.setFont(coords_f);
				String text = String.format("[%d %d] (%d %d)", (int)x, (int)y, (int)cx, (int)cy);
				int w_ = g.getFontMetrics().getMaxAdvance() * text.length();
				int h_ = g.getFontMetrics().getAscent()+6;
				g.setColor(Color.WHITE);
				System.out.println(String.format("CENTER=(%d,%d)", centerx, centery));
				g.fillRect((int) (centerx), (int) (centery), (int) (w_), (int) (h_));
				g.setColor(Color.BLACK);
				g.drawString(text, (int) (centerx), (int) (centery + g.getFontMetrics().getAscent()));
			}
		}
		// GRID LINES
		/*
		 * int COL_N = (int)(WIDTH/gridsize+3);
		 * int ROW_N = (int)(HEIGHT/gridsize/v_mult*2)+4;
		 * float tile_offset_x = player_x % 1;
		 * float tile_offset_y = player_y % 1;
		 * 
		 * for (int x = 0; x <= COL_N; x += 1) {
		 * for (int y = 0; y <= ROW_N; y += 1) {
		 * int offset = y % 2;
		 * int offset_p = (int) (offset * gridsize / 2);
		 * int dy = (int) ((y - 2 * tile_offset_y) * gridsize * v_mult / 2)-gridsize;
		 * int dx = (int) ((x - tile_offset_x) * gridsize + offset_p)-gridsize;
		 * 
		 * int nx = (int) x + (int) player_x;
		 * int ny = (int) y + 2 * (int) (player_y);
		 * // System.out.println(String.format("y=%d x=%d y=%d", y, nx, ny));
		 * if (nx >= 0 && nx < LEVEL_W && ny >= 0 && ny < LEVEL_H) {
		 * Tile tile = level[nx][ny];
		 * if (tile != null && tile.floor != -1) {
		 * g2d.drawImage(AMGR.getFlat(tile.floor).src, dx, dy, gridsize+tile_buffer,
		 * (int)(gridsize*v_mult)+tile_buffer, new Color(0, 0, 0, 0), null);
		 * } else {
		 * g2d.drawImage(AMGR.getFlat("grass").src, dx, dy, gridsize+tile_buffer,
		 * (int)(gridsize*v_mult)+tile_buffer, new Color(0, 0, 0, 0), null);
		 * }
		 * }
		 * 
		 * // draw basic poly
		 * if(false){
		 * g.setColor(Color.RED);
		 * int[] x_pts = { dx + gridsize / 2, dx + gridsize, dx + gridsize / 2,
		 * dx, dx + gridsize / 2 };// top, right, bottom, left
		 * int[] y_pts = { dy + 0, (int) (dy + gridsize * v_mult / 2), (int) (dy +
		 * gridsize * v_mult),
		 * (int) (dy + gridsize * v_mult / 2), dy + 0 };
		 * g.drawPolyline(x_pts, y_pts, x_pts.length);
		 * }
		 * }
		 * }
		 * 
		 * for (int x = 0; false && x <= COL_N; x += 1) {
		 * for (int y = 0; y <= ROW_N; y += 1) {
		 * // ---------
		 * int offset = y % 2;
		 * int offset_p = (int) (offset * gridsize / 2);
		 * int dy = (int) ((y - 2 * tile_offset_y) * gridsize * v_mult / 2);
		 * int dx = (int) ((x - tile_offset_x) * gridsize + offset_p);
		 * 
		 * int nx = (int) x + (int) player_x;
		 * int ny = (int) y + 2 * (int) (player_y);
		 * if (nx >= 0 && nx < LEVEL_W && ny >= 0 && ny < LEVEL_H) {
		 * Tile tile = level[nx][ny];
		 * if (tile != null) {
		 * if (tile.wall_bottom_left != -1 ||
		 * tile.wall_bottom_right != -1 ||
		 * tile.wall_top_left != -1 ||
		 * tile.wall_top_right != -1) {
		 * 
		 * if (tile.wall_top_left != -1) {
		 * Wall w = AMGR.getWall(tile.wall_top_left);
		 * g.drawImage(w.top_left.src, dx, dy - w.top_right.offset1+w.top_right.offset2,
		 * new Color(0, 0, 0, 0), null);
		 * }
		 * if (tile.wall_top_right != -1) {
		 * Wall w = AMGR.getWall(tile.wall_top_right);
		 * g.drawImage(w.top_right.src, dx+gridsize/2, dy -
		 * w.top_right.offset1+w.top_right.offset2, new Color(0, 0, 0, 0),
		 * null);
		 * }
		 * if (tile.wall_bottom_right != -1) {
		 * Wall w = AMGR.getWall(tile.wall_bottom_right);
		 * g.drawImage(w.bottom_right.src, dx+gridsize/2, dy - w.bottom_right.offset1,
		 * new Color(0, 0, 0, 0), null);
		 * }
		 * if (tile.wall_bottom_left != -1) {
		 * Wall w = AMGR.getWall(tile.wall_bottom_left);
		 * g.drawImage(w.bottom_left.src, dx,
		 * dy + w.bottom_left.offset1-gridsize, new Color(0, 0, 0, 0), null);
		 * }
		 * }
		 * 
		 * /*if (tile.ceiling != -1) {
		 * Flat f = AMGR.getFlat(tile.floor);
		 * g2d.drawImage(f.src, dx - gridsize / 2, dy - w.offset2 - gridsize / 2,
		 * new Color(0, 0, 0, 0), null);
		 * }
		 * }
		 * }
		 * }
		 * }
		 * 
		 * for (int x = 0; x <= COL_N; x += 1) {
		 * for (int y = 0; y <= ROW_N; y += 1) {
		 * // ---------
		 * int offset = y % 2;
		 * int offset_p = (int) (offset * gridsize / 2);
		 * int dy = (int) ((y - 2 * tile_offset_y) * gridsize * v_mult / 2)-gridsize;
		 * int dx = (int) ((x - tile_offset_x) * gridsize + offset_p)-gridsize;
		 * int nx = (int) x + (int) player_x;
		 * int ny = (int) y + 2 * (int) (player_y);
		 * 
		 * if (nx >= 0 && ny >= 0 && nx < LEVEL_W && ny < LEVEL_H) {
		 * if (offset == 1)
		 * g.setColor(Color.GREEN);
		 * else
		 * g.setColor(Color.RED);
		 * g.fillRect(dx - 2, dy - 2, 4, 4);
		 * g.setColor(Color.BLACK);
		 * 
		 * // draw coordinate
		 * g.setFont(COORDS_F);
		 * String text = String.format("(%d %d)", x, y);
		 * String text2 = String.format("(%d %d)", nx, ny);
		 * int w_ = g.getFontMetrics().getMaxAdvance() * text.length();
		 * int w_2 = g.getFontMetrics().getMaxAdvance() * text.length();
		 * int h_ = g.getFontMetrics().getAscent();
		 * g.setColor(Color.WHITE);
		 * g.fillRect((int) (dx), (int) (dy), (int) (Math.max(w_, w_2)), (int) (h_ *
		 * 2));
		 * g.setColor(Color.BLACK);
		 * g.drawString(text, dx, dy + g.getFontMetrics().getAscent());
		 * g.drawString(text2, dx, dy + g.getFontMetrics().getAscent() * 2);
		 * }
		 * }
		 * }
		 */

		// g.drawImage(grass, 300, 300, new Color(0, 0, 0, 0), null);
		// g.dispose();
	}

	boolean pressing_switch = false;
	int param_index = 0;
	double delta = 0.05;

	// void Square() {
	// int min_side = Math.min(debug_texture_.getWidth(),
	// debug_texture_.getHeight());
	// BufferedImage img = debug_texture_.getSubimage((int)
	// ((debug_texture_.getWidth() - min_side) / 2),
	// (int) ((debug_texture_.getHeight() - min_side) / 2), min_side, min_side);
	// }

	double dynamic_params[] = { Math.PI / 6, 120 };
	final int LEVEL_W = 30;
	final int LEVEL_H = 30;
	Tile level[][] = new Tile[LEVEL_W][LEVEL_H];

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
			float sprint = PERI.keyPressed(KeyEvent.VK_SHIFT) ? sprint_mult : 1;
			double intent_direction = Math.atan2(intent_y, intent_x);
			double displacement_x = Math.cos(intent_direction) * movement_base_speed * sprint;
			double displacement_y = Math.sin(intent_direction) * movement_base_speed * sprint;

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
		if (PERI.keyPressed('i')) {
			ImportAssets();
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
		else if (angle >= 0 && angle < Math.PI / 4) {
			dy = (int) (h - ptTgt[2].getY());
		} else if (angle >= Math.PI / 4) {
			dy = (int) (h - ptTgt[2].getY());
		}
		g2d1.drawImage(img, 0, dy, null);
		g2d1.dispose();

		ret.image = imgout;
		ret.offset1 = (int) (ptTgt[2].getY());
		ret.offset2 = (int) (ptTgt[1].getY());
		return ret;
	}

	Flat processFlat(BufferedImage img) {
		final double f = Math.PI / 6;
		final int size = 512;

		BufferedImage img1 = rotateImage(img, Math.PI / 4);
		return new Flat(
				toBufferedImage(img1.getScaledInstance(size, (int) (size * Math.sin(f)), 0)));
	}

	Wall processWall(BufferedImage front, BufferedImage back) {
		double f = Math.PI / 6;
		double scaling = 1;
		int size = 512;
		ShearReturn bottomright = shearImage(front, size, size, f, scaling);
		ShearReturn bottomleft = shearImage(front, size, size, -f, scaling);
		ShearReturn topright = shearImage(back, size, size, -f, scaling);
		ShearReturn topleft = shearImage(back, size, size, f, scaling);

		Wall wall = new Wall();

		wall.top_left = new Shear(topleft);
		wall.top_right = new Shear(topright);
		wall.bottom_left = new Shear(bottomleft);
		wall.bottom_right = new Shear(bottomright);
		return wall;
	}

	void ImportAssets() {
		AMGR.addAsset("debug_grad_floor", processFlat(ImageFile("assets/debug_grad.jpeg")));
		AMGR.addAsset("debug_grad_wall",
				processWall(ImageFile("assets/debug_grad.jpeg"), ImageFile("assets/debug_grad.jpeg")));
		AMGR.addAsset("grass", processFlat(ImageFile("assets/grass.jpeg")));
		AMGR.addAsset("sand", processFlat(ImageFile("assets/sand.jpeg")));
		AMGR.addAsset("stone", processFlat(ImageFile("assets/stone.jpeg")));
		AMGR.addAsset("wood", processFlat(ImageFile("assets/wood.jpeg")));
		AMGR.addAsset("wood", processWall(ImageFile("assets/wood.jpeg"), ImageFile("assets/wood.jpeg")));
		AMGR.addAsset("stone", processWall(ImageFile("assets/stone.jpeg"), ImageFile("assets/stone.jpeg")));
	}

	float zoom = 0;
	float zoom_speed = 0.02f;

	public void addEventHooks() {
		PERI.addScrollHook(new ScrollEvent() {

			@Override
			public void action(double val) {
				zoom += zoom_speed * val;
				zoom_mult = Math.exp(zoom);
			}

		});
	}

	public void InitializeApplication() {
		add(panel);

		int init_w = 960;
		int init_h = 600;
		setSize(init_w, init_h);

		CANW = init_w;
		CANH = init_h;

		PERI = new Peripherals();
		AMGR = new AssetManager();
		this.addKeyListener(PERI);
		this.addMouseListener(PERI);
		this.addMouseWheelListener(PERI);

		ImportAssets();

		addEventHooks();

		// level[10][10] = new Tile(AMGR, "sand");
		level[3][3] = new Tile(AMGR, "sand", "sand", "wood", "wood", "stone", "stone");
		/*
		 * Tile t = level[10][12];
		 * System.out.println(String.format("Tile floor=%d ceiling=%d (%d,%d,%d,%d)",
		 * t.floor, t.ceiling, t.wall_top_left, t.wall_top_right, t.wall_bottom_right,
		 * t.wall_bottom_left));
		 * for(Asset a : AMGR.getAssets()){
		 * String name = a.name.substring(0,a.name.length()-2);
		 * System.out.println(String.format("[%s](%d)(%d)", name, AMGR.wallID(name),
		 * AMGR.flatID(name)));
		 * }
		 */
		// this.dispose();
		// Event Timers (screen refresh/game time)
		Timer timer = new Timer();
		initScreenRefresh();
		// game timer
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				tick(TICK_RATE);
			}

		}, 0, TICK_RATE);

		// timer.schedule(new TimerTask() {

		// @Override
		// public void run() {
		// panel.repaint();
		// }

		// }, 0, (int) (1000 / FPS));

		setTitle("Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
}

class ShearReturn {
	BufferedImage image;
	int offset1;
	int offset2;
}

class Tile {
	Tile(AssetManager amgr, String floor1, String ceil1, String wall_top_left1, String wall_top_right1,
			String wall_bottom_right1,
			String wall_bottom_left1) {
		floor = amgr.flatID(floor1);
		ceiling = amgr.flatID(ceil1);
		wall_top_left = amgr.wallID(wall_top_left1);
		wall_top_right = amgr.wallID(wall_top_right1);
		wall_bottom_right = amgr.wallID(wall_bottom_right1);
		wall_bottom_left = amgr.wallID(wall_bottom_left1);
	}

	Tile(AssetManager amgr, String floor1) {
		floor = amgr.flatID(floor1);
	}

	Tile() {

	}

	int wall_top_left;
	int wall_top_right;
	int wall_bottom_left;
	int wall_bottom_right;
	int ceiling;
	int floor;
}