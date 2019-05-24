package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Armin Lezic 3257889 st152623@stud.uni-stuttgart.de
 * @author Yannick Liszkowski 3235102 Yannick.Liszkowski@web.de
 * @author Felix Winterhalter 2964351 felix.winterhalter@gmx.de
 * 
 */

public class ChatClient {

	private int portNumber = 9001; // create and int portNumber
	private BufferedReader commandLineReader; // int reader
	private ChatClientReceiver ccr; // int receiver

	ChatClient() {

		try {

			ccr = new ChatClientReceiver(new Socket("localhost", portNumber)); // create
																				// new
																				// clientreceiver
																				// and
																				// int
			ccr.start(); // start the reveiver
			commandLineReader = new BufferedReader(new InputStreamReader(System.in)); // int
																						// reader

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * TODO make a connection to the chat-server start a ChatClientReceiver
		 * to listen to responses from the server and display them on the
		 * command line listen for command line inputs
		 */

	}

	/**
	 * Start a new Chat client and listen to console inputs. "quit" is the
	 * command to stop the client
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ChatClient client = new ChatClient();

		while (true) { // intfinit loop

			String msg = null;

			msg = client.commandLineReader.readLine(); // read input

			if (msg.equalsIgnoreCase("quit")) { // if input is quit send
												// information to client and set
												// runvalue to false and exit
												// system
				client.ccr.sendMessage(msg);
				client.ccr.setRunValue(false);
				System.exit(0);
			} else {
				client.ccr.sendMessage(msg); // send inotu to server
			}

		}

		/*
		 * TODO: Handle commandLine Input here and shut down ChatClient on
		 * "Quit" In order to quit the ChatClientReceiver-Thread, make use of
		 * the "runReceiver"-boolean.
		 */

	}

}
