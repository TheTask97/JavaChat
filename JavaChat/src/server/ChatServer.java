package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Armin Lezic 3257889 st152623@stud.uni-stuttgart.de
 * @author Yannick Liszkowski 3235102 Yannick.Liszkowski@web.de
 * @author Felix Winterhalter 2964351 felix.winterhalter@gmx.de
 * 
 */

/**
 * Chat server that handles message communication with multiple clients.
 * 
 * @author u.a.: roegerhe
 *
 */
public class ChatServer extends Thread {

	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = 9001;
	private ServerSocket ss;
	/**
	 * The set of all registered users with their output printers
	 */
	private static HashMap<String, PrintWriter> registeredUsers = new HashMap<String, PrintWriter>();

	public ChatServer() throws IOException {

		ss = new ServerSocket(PORT); // int server socket
	}

	public void run() {

		System.out.println("Server is running..."); // notification that server
													// is running

		while (true) {

			Socket socket; // create a socket

			try {
				socket = ss.accept(); // accept connection

				ClientHandler handler = new ClientHandler(socket); // create a
																	// handler
																	// for new
																	// socket
				handler.start(); // start the thread

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO accept new connections and instantiate a new ClientHandler
			// for
			// each session
		}
	}

	public static void main(String[] args) throws Exception {
		ChatServer server = new ChatServer();
		server.start();

		/* command line interaction for server */
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {

			if ((consoleReader.readLine()).equalsIgnoreCase("quit")) { // check
																		// for
																		// quit
																		// input
																		// and
																		// close
																		// server
																		// if
																		// quit
																		// is
																		// entered

				server.ss.close();

				System.exit(0);
			}

		}

		// TODO: listen for commands from commandLine and shut down server on
		// "quit" command.

	}

	/*********************
	 * session class
	 ******************************************************/

	/**
	 * A handler thread class to start a new session for each client.
	 * Responsible for a dealing with a single client and sending its messages
	 * to the respective user.
	 */
	private class ClientHandler extends Thread {

		private BufferedReader inR, bfr; // create reader for socket and console
		private PrintWriter pw; // create writer
		private String username; // create username

		/**
		 * Constructs a handler thread.
		 */
		public ClientHandler(Socket socket) {

			try {
				inR = new BufferedReader(new InputStreamReader(socket.getInputStream())); // int
																							// socket
																							// reader
				pw = new PrintWriter(socket.getOutputStream(), true); // int
																		// writer
				bfr = new BufferedReader(new InputStreamReader(System.in)); // int
																			// reader
																			// for
																			// console
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name
		 * until a unique one has been submitted, then acknowledges the name and
		 * registers the output stream for the client in a global set.
		 */
		public void run() {

			// Name request

			this.nameRequest(); // run nameRequest methode

		}

		private void nameRequest() {
			boolean noname = true; // boolean for checking if a valid name is
									// token
			pw.println("Choose a Name. (Not allowed: only  Blankspace, / in Username or nothing )"); // say
																										// to
																										// user
																										// to
																										// choose
																										// a
																										// name
			pw.flush();

			while (noname) { // looping for a valid name

				try {
					String name = inR.readLine(); // take userinput
					if (name.equalsIgnoreCase("quit")) {

						closeStreams();
						break;

					} else {
						if (!(registeredUsers.containsKey(name)) && !name.equals("") && name != null // check
																										// if
																										// its
																										// valid
								&& !name.contains("/") && (name.trim().length() > 0)
								&& !name.equalsIgnoreCase("quit")) {
							pw.println("Logged in! \n"); // if valid tell hm hes
															// looged in
							pw.flush();
							note(); // little note for using system
							this.username = name; // set username
							registeredUsers.put(name, pw); // add user
							displayUsers(); // show him all users
							sendToUser(); // start chat methode
							break;

						} else {
							pw.println("Name already in Use or contains illegal character. Choose other Username"); // if
							// not
							// valid
							// tell
							// him
							pw.flush();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		/*
		 * TODO: Request a name from this client. Keep requesting until a name
		 * is submitted that is not already used. Note that checking for the
		 * existence of a name and adding the name must be done while locking
		 * the set of names.
		 */

		private void displayUsers() { // loop though map and show all Users
										// online

			pw.println("Aviable Users:");
			pw.flush();
			for (String name : registeredUsers.keySet()) {
				pw.println(name);
				pw.flush();
			}

		}

		/*
		 * TODO Now that a successful name has been chosen, display all
		 * available users to the client.
		 */

		private boolean checkUser(String user) { // methode to check if a certan
													// user is online

			for (String name : registeredUsers.keySet()) {

				if (name.equals(user)) {

					return true;

				}

			}

			return false;

		}

		private void sendMessage(String user, String msg) { // send message to
															// certan user

			PrintWriter rc = registeredUsers.get(user);
			rc.println(username + " says to you: " + msg);

		}

		private void sendMessageToAll(String msg) { // send a message to all
													// Users

			for (String name : registeredUsers.keySet()) {

				sendMessage(name, msg);

			}
		}

		private void closeStreams() { // methode for closing streams

			try {
				pw.close();
				inR.close();
				bfr.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void deleteUser(String name) { // delte user from list if he
												// goes
												// offline

			registeredUsers.remove(username, pw);
			updateUsers();
		}

		private void updateUsers() {

			sendMessageToAll("I'm leaving see you all! :) ");

		}

		private void note() { // aviable commands

			pw.println("to see aviable Users use command /users when asked for a receiver.\n"
					+ "To Exit type quit as receiver.\n" + "To view this notes again type /notes as receiver.\n"
					+ "If you want to send a Message to all Users type /all");
			pw.flush();

		}

		private void sendToUser() {

			while (true) { // infinit loop for sending messages to Users

				pw.println("Enter Username of Message receiver"); // tell user
																	// what to
																	// do
				pw.flush();

				try {

					String receiver;

					if ((receiver = inR.readLine()) != null) {// read user input

						if (receiver.equalsIgnoreCase("quit")) { // check if he
							// wants to quit
							deleteUser(username);
							closeStreams();
							break;
							// if he wants delete him from
							// list
							// and close streams and quit
							// loop
						} else {
							if (receiver.equals("/users")) {
								displayUsers(); // show him online users with
												// this
												// command
							} else {

								if (receiver.equals("/all")) { // check for
																// command

									pw.println("Enter Message:"); // Enter
																	// Message
																	// for all

									String msg = inR.readLine(); // store
																	// Message

									sendMessageToAll(msg); // send Message

								} else {

									if (receiver.equals("/note")) {

										note();

									} else {

										if (checkUser(receiver)) { // if it was
																	// a
																	// name
																	// check
																	// if valid

											pw.println("Enter Message:"); // if
																			// valid
																			// he
																			// can
																			// enter
																			// the
																			// message
											pw.flush();

											String msg = inR.readLine(); // store
																			// Message

											sendMessage(receiver, msg); // send
																		// Message

										} else {

											pw.println("User or command doesnt exist"); // if
																						// nothing
											// is valid
											// tell him
											// user
											// doesnt
											// exist
											pw.flush();
										}
									}
								}
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		/*
		 * TODO: Here comes the interesting part: Listen for input from the
		 * client. Falls "quit": User von der Liste der User entfernen, Thread
		 * schließen Ansonsten: Nachricht deserialisieren und an den Zieluser
		 * weiterleiten. Falls der User nicht verfügbar ist, eine Fehlermeldung
		 * auf der Server-Konsole ausgeben.
		 */
	}
}