package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Armin Lezic 3257889 st152623@stud.uni-stuttgart.de
 * @author Yannick Liszkowski 3235102 Yannick.Liszkowski@web.de
 * @author Felix Winterhalter 2964351 felix.winterhalter@gmx.de
 * 
 */

/**
 * listener-thread reads from messages coming
 * 
 * @author roegerhe
 *
 */
public class ChatClientReceiver extends Thread {
	private boolean runValue;
	private BufferedReader inR; // create input Reader
	private PrintWriter pw; // create output Writer

	ChatClientReceiver(Socket socket) {

		try {

			pw = new PrintWriter(socket.getOutputStream()); // int writer
			inR = new BufferedReader(new InputStreamReader(socket.getInputStream())); // int
																						// reader
			this.runValue = true; // int runvalue
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * TODO: Initialize the reader to listen to messages from the server.
		 */
	}

	public void run() {

		while (runValue) { // infinit loop for run time

			try {

				String msgin = null; // create and int a msgin string

				while ((msgin = inR.readLine()) != null) { // reading
															// massage

					System.out.println(msgin); // priting massage out
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*
			 * TODO: Read messages from server Print them on the comamnd line.
			 */
		}

	}

	public void setRunValue(boolean runValue) {

		this.runValue = runValue;

	}

	public void sendMessage(String msgout) {

		pw.println(msgout);
		pw.flush();

	}

}
