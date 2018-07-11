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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class main extends AppCompatActivity implements Runnable {

    public static Exception ex;
    public static boolean server_status = false;
    public static TextView text;
    public static EditText message;
    public static int sport = 9216 ;
    public static String serverIp ;
    public static Button send;
    public static boolean surver_status= true;
    public static Socket serversocket= null;
    public static MyAdapter myAdapter;
    public static ArrayList<Data> messagesArray;
    public static ListView display;
    public static Thread t;
    public static boolean message_ent=false;
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
        serverIp = "192.168.151.169";
        t = new Thread(new main());
        t.start();
        ex = new Exception();
    }
    //public static Thread t;
    void main()  {
        try {
            serversocket = new Socket(serverIp, sport);
        } catch (UnknownHostException e) {
            alert("Oops","Hots :"+serverIp+" Not found");
            t.interrupt();
        } catch (IOException e) {
            alert("Error","IOException occured");
            t.interrupt();
        }
    }
    @Override
    public void run() {
        while (surver_status){
                if(message_ent =true);
                {
                    new communicationThread();
                    message_ent = false;
                }

        }
        alertfinish("Bye", "click finish to close the app");
    }
    protected void onResume() {

        super.onResume();
        t.interrupt();
        try {
            serversocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void onStop() {
        super.onStop();
        if (!t.isInterrupted())
            t.interrupt();
    }
    private void onButtonClick() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(serversocket==null){
                        alert("Oops","No user online to send message wait for the client to join");
                        message.setText(null);
                        return;
                    }
                    messagesArray.add(new Data(message.getText().toString(), true));
                    display.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
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

    public boolean onCreateOptionsMenu( Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main , menu);
        return true;
    }
    public boolean onOptionsItemSelected (MenuItem item)
    {
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
    public void alert (String title ,String body)
    {
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

    private void alertfinish(String title ,String body)
    {
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
                String inputLine;
                inputLine = example.message;
                if(inputLine==null){
                    throw  ex;
                }
                messagesArray.add(new Data(inputLine.toString(), false));
                if (inputLine.toString().equals("STOP")) {
                    server_status = false;
                    t.interrupt();
                }
                display.post(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });
                Toast.makeText(getApplicationContext(),"new message "+inputLine.toString(),Toast.LENGTH_LONG);
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
