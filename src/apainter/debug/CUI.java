package apainter.debug;

import static apainter.misc.Util.*;

import java.util.Scanner;

import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;

public class CUI implements Runnable{

	private Thread thread;
	private GlobalValue gloval;//これスペルミスじゃないんだぜ！
	private CommandCenter command;


	public CUI(GlobalValue g){
		gloval = nullCheack(g);
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
				command.exec(s);
			}
			if(Thread.interrupted())break;
		}
	}
}
