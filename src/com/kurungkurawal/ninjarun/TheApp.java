package com.kurungkurawal.ninjarun;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Konglie on 11/15/2016.
 */
public class TheApp extends JFrame {
	public TheApp(){
		super("[kurungkurawal.com] Ninja Run");
//		setResizable(false);
		setMinimumSize(new Dimension(800, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		NinjaPanel ninjaPanel = new NinjaPanel();
		getContentPane().add(ninjaPanel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e){
			e.printStackTrace();
		}
		new TheApp()
				.setVisible(true);
	}
}
