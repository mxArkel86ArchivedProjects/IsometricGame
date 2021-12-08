package game;

public abstract class TileAction {
    public abstract void action(float x, float y, double dx, double dy, double cx, double cy, int ix, int iy, int px, int py, int odd_level);
    public void run(int ROWS, int COLUMNS, double playerx, double playery, double tilesize){
        double offsetx = playerx % 1;
		double offsety = playery % 1;

        for (float y = -2.0f; y <= ROWS; y+=1.0f) {
			for (float x = -2.0f; x <= COLUMNS; x+=1.0f) {
				int odd_level = (int)Math.abs(y % 2);

				double dx = x * tilesize + odd_level * tilesize/2-offsetx*tilesize;
				double dy = y * tilesize/4-offsety*tilesize/2;

				double cx = dx+tilesize/2;
				double cy = dy+tilesize/4;
				int ix = (int) (x+Math.ceil(y/2)+offsetx);
				int iy = (int) (x-Math.floor(y/2)-offsety);
                int px = (int) (x+Math.ceil(y/2)+playerx);
				int py = (int) (x-Math.floor(y/2)-playery);
                action(x, y, dx, dy, cx, cy, ix, iy, px, py, odd_level);
            }
        }
    }
}

