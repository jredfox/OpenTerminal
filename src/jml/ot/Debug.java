package jml.ot;

import java.io.File;
import java.io.IOException;

public class Debug {

	public static void main(String[] args) throws InterruptedException, IOException {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() 
			{
				System.out.println("here");
			}
		});
		System.exit(0);
	}

}
