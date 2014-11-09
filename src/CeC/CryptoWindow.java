package CeC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Just provides some initial variables for the JFrame from the showWindow() methods
 * @author Corlin
 */
public class CryptoWindow extends JFrame{
	private static final long serialVersionUID = -3717115010104145514L;
	
	public JPanel jp = null;
	public ProgressBar jpw = null;
	public boolean isDe = false;

	public CryptoWindow(String title, boolean isDe){
		super(title);
		this.isDe = isDe;
		setUndecorated(true);
		setLocationRelativeTo(null);
		setSize(200,60);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jp = new JPanel();
		add(jp);
		jp.setLayout(new BorderLayout());
		jpw = new ProgressBar(isDe);
		jpw.setCaretColor(Color.white);
		jpw.setForeground(Color.white);
		jpw.setBackground(new Color(18,18,22));
		jpw.setHorizontalAlignment(JPasswordField.CENTER);
		if(isDe)
			jpw.setBorder(BorderFactory.createLineBorder(new Color(191,136,52), 2));
		else
			jpw.setBorder(BorderFactory.createLineBorder(new Color(48,95,212), 2));
		jpw.setFont(new Font("Arial", Font.BOLD, 30));
		jp.add(jpw, BorderLayout.CENTER);
		jpw.requestFocus();
	}

}
