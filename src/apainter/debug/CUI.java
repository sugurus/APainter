package apainter.debug;

import java.util.Scanner;

import apainter.GlobalValue;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;

public class CUI implements Runnable{

	public static CUI cui=new CUI();
	private Thread thread;
	private GlobalValue gloval;
	private CommandCenter command = new CommandCenter();


	public void setGlobalValue(GlobalValue g){
		if(gloval!=null)return;
		gloval = g;
		command.setGlobalValue(g);
	}

	public synchronized void start(){
		if(thread!=null&&thread.isAlive())return;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop(){
		thread.interrupt();
		thread=null;
	}

	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);

		while(true){
			System.out.print("command>");
			String s = scan.nextLine();
			if(!"".equals(s)){
				command.exec(s);
			}
			if(Thread.interrupted())break;
		}
	}
}
