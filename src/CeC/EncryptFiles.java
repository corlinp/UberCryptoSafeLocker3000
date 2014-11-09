package CeC;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;


public class EncryptFiles {

	/**
	 * We start different windows based on whether we want to encrypt or decrypt
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		boolean hsl;
		try{
			hsl = hasLol();
		}catch(NullPointerException e){
			return;
		}
		if(hsl){
			DecryptFiles.showWindow();
		}
		else{
			showWindow();
		}
	}

	/**
	 * Checks if there are any .lol files in the subfolders.
	 * @return
	 * @throws Exception
	 */
	public static boolean hasLol() throws Exception{
		File[] test = DecryptFiles.lolFiles();
		if(test == null){
			throw new NullPointerException();
		}
		DecryptFiles.files = test;
		return (test.length > 0);
	}

	private static ProgressBar pb = null;
	private static char[] password = null;
	private static CryptoWindow jf = null;
	private static EncryptWorker encW = null;
	private static 	File[] files = null;

	public static void showWindow() throws Exception{
		jf = new CryptoWindow("Encrypt Files", false);
		jf.jpw.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {
				//close the program if escape is pressed. Cancel operations if it is running.
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
					try{
						pb = jf.jpw;
						files = toFiles();
						pb.setTotal(files);
						//we don't want the program to close prematurely
						jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						pb.startProgress();
						password = jf.jpw.getPassword();
						encW = new EncryptWorker();
						encW.execute();
					}catch(Exception e){}
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		jf.setVisible(true);
	}

	/**
	 * SwingWorkey to encrypt files in the background and let 
	 * progress bar updates run on the EDT
	 */
	public static class EncryptWorker extends SwingWorker<Long,Long>{
		protected Long doInBackground() throws Exception {
			for(File f:files){
				//we need to store f.length() before we do any IO operations.
				long fsize = f.length();
				//we still want to make a password store even if it is cancelled.
				if(isCancelled()){
					break;
				}
				try{
					encrypt(f, password);
					publish(fsize);
				}catch(Exception e){}

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
			try {
				makeStore(jf.jpw.getPassword());
			} catch (Exception e) {}
			jf.dispose();
		}
	}

	/**
	 * Lists all of the encryptable files in the folder
	 */
	public static File[] toFiles() throws Exception{
		String loc = runLocation();
		//we don't want to encrypt the jar!
		String jarName = loc.substring(loc.lastIndexOf("/")+1);
		String foldName = loc.substring(0,loc.lastIndexOf("/")+1);
		File f = new File(foldName);
		ArrayList<File> al = new ArrayList<>();
		toFiles(f,jarName,al);
		return al.toArray(new File[al.size()]);
	}

	/**
	 * Recursive way of listing the files in the folder.
	 */
	private static void toFiles(File folder, String jarName, ArrayList<File> al){
		for(File ff : folder.listFiles()){
			if(ff.isFile()){
				if(!ff.getName().endsWith(".lol") && !ff.getName().equals(jarName) && !ff.getName().equals(".password")){
					al.add(ff);
				}
			}
			else{
				toFiles(ff,jarName,al);
			}
		}
	}
	
	//find the folder in which the file was executed
	public static String runLocation() throws UnsupportedEncodingException{
		String path = EncryptFiles.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		return decodedPath;
	}

	//store the hash of the password in a file.
	public static void makeStore(char[] pass) throws Exception{
		File f = new File(".password");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(CryptoUtils.hashOf(pass));
		fos.close();
	}

	/**
	 * Encrypt a file. Takes a new file and 
	 */
	public static boolean encrypt(File inF, char[] pass) throws Exception{
		File outF = new File(inF.getAbsolutePath() + ".lol");
		CryptoUtils.encrypt(pass, inF, outF);
		inF.delete();
		return true;
	}
}
















