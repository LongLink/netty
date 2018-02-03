package com.nan.sio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeServerHandler implements Runnable {

	private Socket socket;

	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(), true);
			String currentTime = null;
			String body = null;
			while (true) {
				body = in.readLine();
				if (body == null)
					break;
				System.out.println("The time server order : " + body);
				currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(
						System.currentTimeMillis()).toString() : "BAD ORDER";
				System.out.println(currentTime);
			}
		} catch (Exception e) {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (this.socket != null) {
				try {
					socket.close();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
				this.socket = null;
			}
		}
	}

}
