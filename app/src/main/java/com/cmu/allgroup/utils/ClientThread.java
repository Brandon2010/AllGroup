package com.cmu.allgroup.utils;

/**
 * Created by wangxi on 4/30/15.
 */

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread implements Runnable {
    private Handler handler;
    private BufferedReader br = null;

    public ClientThread(Socket socket, Handler handler) throws IOException {
        this.handler = handler;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String content = null;
            while ((content = br.readLine()) != null) {
                Message msg = new Message();
                msg.what = 0x234;
                msg.obj = content;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

