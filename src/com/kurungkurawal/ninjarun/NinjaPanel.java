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
 * http://opengameart.org/content/ninja-run-free-sprites
 *
 * images may be copyrighted,
 * let's go!
 *
 */
public class NinjaPanel extends JPanel {
	private final int SpriteSize = 10;
	private final String SpriteBaseResourceFolder = "/com/kurungkurawal/sprites";
	private BufferedImage[] runningSprites, idleSprites;

	private int currentFrame = 0;
	private boolean assetsReady = false;
	private JRadioButton radioRun;
	private JRadioButton radioIdle;

	public NinjaPanel(){
		super();
		setBackground(Color.BLACK);
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
		super.paint(g);

		int w = getWidth(), h = getHeight();
		if(!assetsReady){
			Font font = new Font("Arial", Font.BOLD, 29);
			FontMetrics metrics = getFontMetrics(font);
			g.setFont(font);
			String loading = "Loading...";
			g.drawString(loading, (w - metrics.stringWidth(loading)) / 2, (h - metrics.getHeight()) / 2);
			return;
		}
		BufferedImage[] frames = runningSprites;
		if(radioIdle.isSelected()){
			frames = idleSprites;
		}
		BufferedImage frame = frames[currentFrame++];
		int iw = frame.getWidth(), ih = frame.getHeight();
		int centerx = (w-iw)/2, centery = (h-ih)/2;

		g.drawImage(frame, centerx, centery, null);

		if(currentFrame >= SpriteSize){
			currentFrame = 0;
		}
	}

	private void buildGUI(){
		radioRun = new JRadioButton("Running");
		radioRun.setSelected(true);

		radioIdle = new JRadioButton("Idle");

		ButtonGroup opt = new ButtonGroup();
		opt.add(radioRun);
		opt.add(radioIdle);

		add(radioRun);
		add(radioIdle);
	}

	private void cacheAssets(){
		runningSprites = new BufferedImage[SpriteSize];
		idleSprites = new BufferedImage[SpriteSize];
		String spriteFile;
		for(int i = 0; i < SpriteSize; i++){
			spriteFile = String.format("%s/Run__00%s.png", SpriteBaseResourceFolder, i);
			runningSprites[i] = fetchImage(spriteFile);
			spriteFile = String.format("%s/Idle__00%s.png", SpriteBaseResourceFolder, i);
			idleSprites[i] = fetchImage(spriteFile);
		}
		assetsReady = true;
	}
	/**
	 * Read image file from project/application images resource
	 *
	 * @param path the file path INSIDE /images folder
	 * @return
	 */
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
}
