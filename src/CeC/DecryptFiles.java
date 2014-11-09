package CeC;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;


public class DecryptFiles {
	private static final Color wrongPassColor = new Color(200,41,38);

	public static void showWindow() throws Exception{
		try{
			readStore();
		}catch(Exception e){
			lastHash = null;
		}
		jf = new CryptoWindow("Decrypt Files", true);
		jf.jpw.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
					jf.dispose();
					if(encW != null){
						encW.cancel(false);
					}
					return;
				}
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
					if(pb != null)
						return;
					//check the hash of the password against the stored one; flash red if it is not correct.
					//if there is no stored password it will accept any decryption password.
					if(lastHash != null && CryptoUtils.hashOf(jf.jpw.getPassword()).equals(lastHash)){
						jf.jpw.setBackground(wrongPassColor);
						jf.jpw.paintImmediately(new Rectangle(jf.jpw.getX(), jf.jpw.getY(), jf.jpw.getWidth(), jf.jpw.getHeight()));
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {}
						jf.jpw.setBackground(Color.black);
						jf.jpw.paintImmediately(new Rectangle(jf.jpw.getX(), jf.jpw.getY(), jf.jpw.getWidth(), jf.jpw.getHeight()));
						jf.jpw.selectAll();
						return;
					}
					try{
						pb = jf.jpw;
						pb.setTotal(files);
						jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						pb.startProgress();
						password = jf.jpw.getPassword();
						encW = new EncryptWorker();
						encW.execute();
					}catch(Exception e){
						
					}
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		jf.setVisible(true);
	}
	
	private static ProgressBar pb = null;
	private static char[] password = null;
	private static CryptoWindow jf = null;
	private static EncryptWorker encW = null;
	public static File[] files = null;
	
	public static class EncryptWorker extends SwingWorker<Long,Long>{
		protected Long doInBackground() throws Exception {
			for(File f:files){
				long fsize = f.length();
				if(isCancelled()){
					break;
				}
				try{
					DecryptFiles.decrypt(f, password);
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					publish(fsize);
				}catch(Exception e){
					e.printStackTrace();
				}

			}
			return 0L;
		}
		protected void process(List<Long> chunks){
			for(Long l:chunks){
				pb.soFar+=l;
			}
			pb.repaint();
		}
		protected void done(){
			jf.dispose();
		}
	}
	
	private static byte[] lastHash = null;
	public static void readStore() throws Exception{
		File f = new File(".password");
		lastHash = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(lastHash);
		fis.close();
	}

	public static File[] lolFiles() throws Exception{
		String loc;
		try{
			loc = EncryptFiles.runLocation();
		}catch(Exception e){
			return null;
		}
		String foldName = loc.substring(0,loc.lastIndexOf("/")+1);
		File f = new File(foldName);
		ArrayList<File> al = new ArrayList<>();
		lolFiles(f,al);
		return al.toArray(new File[al.size()]);
	}

	private static void lolFiles(File folder, ArrayList<File> al){
		for(File ff : folder.listFiles()){
			if(ff.isFile()){
				if(ff.getName().endsWith(".lol")){
					al.add(ff);
				}
			}
			else{
				lolFiles(ff,al);
			}
		}
	}
	
	public static boolean decrypt(File inF, char[] pass) throws Exception{
		File outF = new File(inF.getAbsolutePath().substring(0, inF.getAbsolutePath().length() - 4));
		CryptoUtils.decrypt(pass, inF, outF);
		inF.delete();
		return true;
	}
}
