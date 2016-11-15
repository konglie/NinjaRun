package com.kurungkurawal.ninjarun;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Created by Konglie on 11/15/2016.
 *
 * images are downloaded from
 * http://opengameart.org/
 *
 * Images may be copyrighted.
 * Anyway, here it is...
 *
 */
public class NinjaPanel extends JPanel {
	private final int SpriteSize = 10;
	private final String SpriteBaseResourceFolder = "/com/kurungkurawal/sprites";
	private BufferedImage[] runningSprites, idleSprites;
	private BufferedImage background, road;

	private int currentFrame = 0;
	private boolean assetsReady = false;
	private JRadioButton radioRun;
	private JRadioButton radioIdle;
	private int backgroundPos = 0, roadPos = 0;

	public NinjaPanel(){
		super();
		setOpaque(false);
		buildGUI();

		new Thread(new Runnable() {
			@Override
			public void run() {
				cacheAssets();
				while(true){
					try{
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								NinjaPanel.this.repaint();
							}
						});
						Thread.sleep(1000 / 25);
					} catch (Exception e){

					}
				}
			}
		}).start();

	}

	@Override
	public void paint(Graphics g){

		int w = getWidth(), h = getHeight();
		if(!assetsReady){
			Font font = new Font("Arial", Font.BOLD, 29);
			FontMetrics metrics = getFontMetrics(font);
			g.setFont(font);
			String loading = "Loading...";
			g.drawString(loading, (w - metrics.stringWidth(loading)) / 2, (h - metrics.getHeight()) / 2);
			return;
		} else {

			drawBackground((Graphics2D) g);

			BufferedImage[] frames = runningSprites;
			if (radioIdle.isSelected()) {
				frames = idleSprites;
			}
			BufferedImage ninja = frames[currentFrame++];
			int iw = ninja.getWidth(), ih = ninja.getHeight();
			int xpos = (w / 4) - iw, ypos = (h - ih - road.getHeight());

			g.drawImage(ninja, xpos, ypos, null);

			if (currentFrame >= SpriteSize) {
				currentFrame = 0;
			}
		}

		super.paint(g);
	}

	private void drawBackground(Graphics2D g){
		g.drawImage(background, -backgroundPos, 0, null);
		int onScreen = background.getWidth() - backgroundPos;

		if(onScreen <= getWidth()){
			g.drawImage(background, onScreen, 0, null);
			if(backgroundPos >= getWidth()){
				backgroundPos = 0;
			}
		}

		// the road
		int ntile = (int)Math.ceil(((double)getWidth() + roadPos) / road.getWidth());
		int ypos = getHeight() - road.getHeight();
		for(int i = 0; i < ntile; i++){
			g.drawImage(road, i * road.getWidth() - roadPos, ypos, null);
		}

		if(roadPos == road.getWidth()){
			roadPos = 0;
		}

		if(!radioIdle.isSelected()) {
			backgroundPos += 2;
			roadPos += 5;
		}
	}

	private void buildGUI(){
		radioRun = radio("Running");
		radioRun.setSelected(true);

		radioIdle = radio("Idle");

		ButtonGroup opt = new ButtonGroup();
		opt.add(radioRun);
		opt.add(radioIdle);

		add(radioRun);
		add(radioIdle);
	}

	private JRadioButton radio(String text){
		JRadioButton r = new JRadioButton(text);
		r.setOpaque(true);
		return r;
	}

	private void cacheAssets(){
		runningSprites = new BufferedImage[SpriteSize];
		idleSprites = new BufferedImage[SpriteSize];
		String spriteFile;
		int spriteHeight = 200;
		for(int i = 0; i < SpriteSize; i++){
			spriteFile = String.format("%s/Run__00%s.png", SpriteBaseResourceFolder, i);
			runningSprites[i] = scale(fetchImage(spriteFile), 0, spriteHeight);

			spriteFile = String.format("%s/Idle__00%s.png", SpriteBaseResourceFolder, i);
			idleSprites[i] = scale(fetchImage(spriteFile), 0, spriteHeight);
		}
		background = scale(fetchImage(String.format("%s/background.png", SpriteBaseResourceFolder)), 800, 0);
		road = scale(fetchImage(String.format("%s/road.png", SpriteBaseResourceFolder)), 0, 90);
		assetsReady = true;
	}

	public BufferedImage fetchImage(String path){
		URL url = getClass().getResource(path);
		try {
			BufferedImage image = ImageIO.read(url);
			return image;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public BufferedImage scale(BufferedImage image, int width, int height){
		if(width <= 0){
			width = Integer.MAX_VALUE;
		}

		if(height <= 0){
			height = Integer.MAX_VALUE;
		}

		Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
		Dimension resize = calcResize(
			imgSize,
				new Dimension(width, height)
		);

		Image img = image.getScaledInstance(
				(int)resize.getWidth(),
				(int)resize.getHeight(),
				Image.SCALE_SMOOTH
		);
		BufferedImage result = new BufferedImage(
				(int)resize.getWidth(), (int) resize.getHeight(), BufferedImage.TYPE_4BYTE_ABGR
		);
		Graphics g = result.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return result;
	}

	public static Dimension calcResize(Dimension original, Dimension target) {
		int ow = original.width,  oh = original.height;
		int tw = target.width, th = target.height;
		int newWidth = ow, newHeight = oh;

		if (ow > tw) {
			newWidth = tw;
			newHeight = (newWidth * oh) / ow;
		}

		if (newHeight > th) {
			newHeight = th;
			newWidth = (newHeight * ow) / oh;
		}

		return new Dimension(newWidth, newHeight);
	}
}
