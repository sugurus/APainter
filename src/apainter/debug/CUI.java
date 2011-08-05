package apainter.debug;

import java.util.Scanner;

import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.command.CommandCenter;
import apainter.command.NotFoundCommandException;

public class CUI implements Runnable{

	private Thread thread;
	private CommandCenter command;


	public CUI(GlobalValue g){
		command = g.get(GlobalKey.CommandCenter, CommandCenter.class);
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
				try {
					command.exec(s);
				} catch (NotFoundCommandException e) {
					System.out.println(e.getMessage()+" not found!");
				}
			}
			if(Thread.interrupted())break;
		}
	}
}
