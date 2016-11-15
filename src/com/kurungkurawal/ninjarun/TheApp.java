package com.kurungkurawal.ninjarun;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Konglie on 11/15/2016.
 */
public class TheApp extends JFrame {
	public TheApp(){
		super("[kurungkurawal.com] Ninja Run");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		NinjaPanel ninjaPanel = new NinjaPanel();
		getContentPane().add(ninjaPanel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args){
		new TheApp()
				.setVisible(true);
	}
}
