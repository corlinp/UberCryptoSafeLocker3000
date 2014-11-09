package CeC;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JPasswordField;


/**
 * This is a combination PasswordField and progress bar. It works well compared to 
 * having to remove and add another swing component.
 * @author Corlin
 */
public class ProgressBar extends JPasswordField{
	private static final long serialVersionUID = -4293837697574734892L;
	public long total = 0;
	public long soFar = 0;
	private static final Color barColor = new Color(21,202,86);
	private boolean startProgress = false;
	private boolean isDe = false;

	public ProgressBar(boolean dec){
		isDe = dec;
	}

	/*
	 * Paints over the password field, blank canvas ready to update progress bar.
	 */
	public void startProgress(){
		startProgress = true;
		repaint();
	}

	/**
	 * Counts the total number of bits that need to be encrypted
	 * @param files an array of files to encrypt
	 */
	public void setTotal(File[] files){
		for(File f:files){
			total+=f.length();
		}
	}

	/**
	 * We paint the progress bar over the top of the Password Field for simplicity.
	 * The bar comes from the right if it is decrypting, from the left if it is encrypting.
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(startProgress){
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());
			if(total >0){
				g.setColor(barColor);
				int width = (int)((((double)soFar/(double)total))*getWidth());
				if(isDe)
					g.fillRect(getWidth()-width,0, getWidth(),getHeight());
				else
					g.fillRect(0,0, width,getHeight());
			}
		}
	}

}
