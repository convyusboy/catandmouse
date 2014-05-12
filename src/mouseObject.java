import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;


public class mouseObject extends boardObject {
	int posisiMouse;
	Image images[];
	
	public mouseObject(Image images[]) {
		super(images[1]);
		this.images = images;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setPosisiMouse(int posisiMouse){
		this.posisiMouse = posisiMouse;
	}
	
	@Override
	public void drawObject(Graphics g, int w, int h, ImageObserver i) { drawObject(g,xcoord,ycoord,w,h,i); }
	@Override
	public void drawObject(Graphics g, int w, int h) { drawObject(g,xcoord,ycoord,w,h,null); }
	@Override
	public void drawObject(Graphics g, int x, int y, int w, int h) { drawObject(g,x,y,w,h,null); }
	@Override
	public void drawObject(Graphics g, int x, int y, int w, int h, ImageObserver i) {
		if (style == O_IMG) {
			// paint image
			g.drawImage(images[posisiMouse], x*w, y*h, w, h, i);
			}

	}
}