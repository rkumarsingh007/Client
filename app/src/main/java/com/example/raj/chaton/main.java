package com.example.raj.chaton;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class main extends AppCompatActivity  {

    public static Exception ex;
    public static boolean server_status = false;
    public static TextView text;
    public static EditText message;
    public static int sport = 6666 ;
    public static String serverIp ;
    public static Button send;
    public static boolean surver_status= true;
    public static Socket serversocket= null;
    public static MyAdapter myAdapter;
    public static ArrayList<Data> messagesArray;
    public static ListView display;
    public static Boolean message_ent;
    public static main m;
    public static waitInBack w;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text=findViewById(R.id.text);
        display = findViewById(R.id.display);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        onButtonClick();
        messagesArray = new ArrayList<Data>();
        myAdapter= new MyAdapter(this,messagesArray);
        display.setAdapter(myAdapter);
        serverIp = "192.168.151.205";
        //m= new main();
        w= new waitInBack();
        new Thread(w).start();
        message_ent = false;
        ex = new Exception();
    }
    protected void onResume() {

        super.onResume();
        try {
            if(serversocket!=null)
            serversocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void onStop() {
        super.onStop();
        if (serversocket!=null)
            try {
                serversocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    private void onButtonClick() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(serversocket==null){
                        alert("Oops","SERVER is OFF !!! :( ");
                        message.setText(null);
                        return;
                    }
                    if(message.getText().toString()==null){
                        Toast.makeText(getApplicationContext(),"Enter Some Text !!!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    myAdapter.add(new Data(message.getText().toString(), true));
                    new ObjectOutputStream(serversocket.getOutputStream()).writeObject(new Data(message.getText().toString(),true));
                    message.setText(null);
                    message_ent =true;
                } catch (UnknownHostException e) {
                    Toast.makeText(getApplicationContext(),"Error : "+ e.toString(),Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Error : "+ e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean onCreateOptionsMenu( Menu menu){
        getMenuInflater().inflate(R.menu.menu_main , menu);
        return true;
    }
    public boolean onOptionsItemSelected (MenuItem item){
        switch ( item.getItemId()) {
            case R.id.info_id: {
                Toast.makeText(getApplicationContext(), "info is clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.server: {
                Toast.makeText(getApplicationContext(), "info is clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    synchronized public void alert (String title ,String body){
        final AlertDialog.Builder Alert = new AlertDialog.Builder(this);
        Alert.setCancelable(true)
                .setTitle(title)
                .setMessage(body);
        Alert.setNegativeButton("Okey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Alert.create().show();
    }
    private void alertfinish(String title ,String body){
        final AlertDialog.Builder Alert = new AlertDialog.Builder(this);
        Alert.setCancelable(true)
                .setTitle(title)
                .setMessage(body);
        Alert.setNegativeButton("finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        Alert.create().show();
    }
    class waitInBack implements Runnable{
        @Override
        public void run() {
            try {
                serversocket = new Socket(serverIp,sport);
                /*SocketAddress adr = new InetSocketAddress(serverIp, sport);
                serversocket.connect(adr,1500);*/
            } catch (UnknownHostException e) {
                alert("Oops", "Hots :" + serverIp + " Not found");
            } catch (IOException e) {
                try {
                    serversocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //alert("Error", "IOException occured");
            }
            while (surver_status){
                if(message_ent==true)
                {
                    new communicationThread();
                    message_ent = false;
                }
            }
            alertfinish("Bye", "click finish to close the app");
        }
    }
    class communicationThread implements Runnable {

        DataInputStream input = null;
        DataOutputStream out =null;
        public communicationThread(){
            try {
                this.input= new DataInputStream(serversocket.getInputStream());
                this.out = new DataOutputStream(serversocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try{
                Data example = (Data) new ObjectInputStream( serversocket.getInputStream()).readObject();
                out.flush();
                String inputLine;
                inputLine = example.message;
                if(inputLine==null){
                    throw  ex;
                }
                myAdapter.add(new Data(inputLine, false));
                if (inputLine.equals("STOP")) {
                    server_status = false;
                    //t.interrupt();
                }
                display.post(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });
                Toast.makeText(getApplicationContext(),"new message "+inputLine,Toast.LENGTH_LONG);
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                alert("no message ", "Enter the message");
            }
        }
    }
}
