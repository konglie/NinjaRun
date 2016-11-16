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
	private final int FrameWidth = 784;
	private final int FrameHeight = 361;

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
						Thread.sleep(1000 / 30);
					} catch (Exception e){

					}
				}
			}
		}).start();

	}

	@Override
	public void paint(Graphics g){

		Graphics2D g2 = (Graphics2D)g;
		int w = getWidth(), h = getHeight();
		Double scaleW, scaleH;
		if(w != FrameWidth || h != FrameHeight) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			scaleW = (double) w / FrameWidth;
			scaleH = (double) h / FrameHeight;
			g2.scale(scaleW, scaleH);
		}

		if(!assetsReady){
			g2.fillRect(0, 0, w, h);
			Font font = new Font("Arial", Font.BOLD, 29);
			FontMetrics metrics = getFontMetrics(font);
			g.setColor(Color.GRAY);
			g.setFont(font);
			String loading = "Loading...";
			g.drawString(loading, (w - metrics.stringWidth(loading)) / 2, (h - metrics.getHeight()) / 2);
			return;
		} else {

			drawBackground(g2);

			BufferedImage[] frames = runningSprites;
			if (radioIdle.isSelected()) {
				frames = idleSprites;
			}
			BufferedImage ninja = frames[currentFrame++];
			int iw = ninja.getWidth(), ih = ninja.getHeight();
			int xpos = (FrameWidth / 4) - iw, ypos = (FrameHeight - ih - road.getHeight());

			g.drawImage(ninja, xpos, ypos, null);

			if (currentFrame >= frames.length) {
				currentFrame = 0;
			}
		}

		if(w != FrameWidth || h != FrameHeight) {
			scaleW = (double) FrameWidth / w;
			scaleH = (double) FrameHeight / h;
			g2.scale(scaleW, scaleH);
		}
		super.paint(g);
	}

	private void drawBackground(Graphics2D g){
		g.drawImage(background, -backgroundPos, 0, null);
		int onScreen = background.getWidth() - backgroundPos;

		if(onScreen <= FrameWidth){
			g.drawImage(background, onScreen, 0, null);
			if(backgroundPos >= FrameWidth){
				backgroundPos = 0;
			}
		}

		// the road
		int ntile = (int)Math.ceil(((double)FrameWidth + roadPos) / road.getWidth());
		int ypos = FrameHeight - road.getHeight();
		for(int i = 0; i < ntile; i++){
			g.drawImage(road, i * road.getWidth() - roadPos, ypos, null);
		}

		if(roadPos == road.getWidth()){
			roadPos = 0;
		}

		if(!radioIdle.isSelected()) {
			backgroundPos += 2;
			roadPos += road.getWidth() / 20;
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
		int ninjaHeight = 120;
		for(int i = 0; i < SpriteSize; i++){
			spriteFile = String.format("%s/Run__00%s.png", SpriteBaseResourceFolder, i);
			runningSprites[i] = scale(fetchImage(spriteFile), 0, ninjaHeight);

			spriteFile = String.format("%s/Idle__00%s.png", SpriteBaseResourceFolder, i);
			idleSprites[i] = scale(fetchImage(spriteFile), 0, ninjaHeight);
		}
		background = scale(fetchImage(String.format("%s/background.png", SpriteBaseResourceFolder)), FrameWidth, 0);
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
