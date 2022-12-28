import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Parser extends Thread {
    ArrayList<Socket> clients;
    Socket client;

    public Parser(Socket cs, ArrayList<Socket> cls) {
        this.client = cs;

        this.clients = cls;
    }

    public void run() {
        try {

            while (true) {
                // BufferedReader from_cli_buf=new BufferedReader(new
                // InputStreamReader(client.getInputStream()));
                DataInputStream from_cli = new DataInputStream(client.getInputStream());
                DataOutputStream to_cli = new DataOutputStream(client.getOutputStream());

                // from_fli.close();

                String filename = "";

                String command = "";
                int byt = 0;
                while (true) {
                    System.out.println(command);
                    byt = from_cli.read();
                    
                    if ((char) byt != ' ') {
                        command += (char) byt;
                        if(command.equals("list"))break;
                    } 
                    
                    else {
                        break;
                    }
                }
                System.out.println("command is::"+command);
                if (command.equals("send")) {
                    byte[] buffer = new byte[4 * 1024];
                    while (true) {

                        int a=0;
                        a=from_cli.read();
                        if((char)a==';')break;
                        
                        System.out.println((char)a);
                        filename+=(char)a;

                    }
                    long size = from_cli.readLong();
                    System.out.println("tying to get this work:" + size);
                    FileOutputStream foup = new FileOutputStream(filename);
                    System.out.println("here::" + filename);
                    int bytes = 0;
                    System.out.println("size is:" + Long.toString(size));
                    while (size > 0 && (bytes = from_cli.read(
                            buffer, 0,
                            (int) Math.min(buffer.length, size))) != -1) {
                        foup.write(buffer, 0, bytes);
                        size -= bytes;
                        System.out.println("remaining:" + Long.toString(size / 1000) + "kb");
                    }
                    foup.close();
                    System.out.println("done");
                } else if (command.equals("get")) {
                    while (true) {

                        int a=0;
                        a=from_cli.read();
                        if((char)a==';')break;
                        
                        System.out.println((char)a);
                        filename+=(char)a;
                        //System.out.println("fname::"+filename);

                    }
                    //System.out.println("filename is ::" + filename);
                    File file = new File(filename);
                    FileInputStream finp = new FileInputStream(file);

                    // to_cli.writeBytes(filename);
                    to_cli.writeLong(file.length());
                    int bytes;
                    byte[] buffer = new byte[4 * 1024];

                    while ((bytes = finp.read(buffer)) != -1) {
                        to_cli.write(buffer, 0, bytes);
                        to_cli.flush();
                    }
                    System.out.println("file sent!");
                    finp.close();

                }
                else if(command.equals("list")){
                    System.out.println("listing the content now");
                    File folder = new File(".");
                    File[] listOfFiles = folder.listFiles();
                    String content="";
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            content+="File " + listOfFiles[i].getName()+'\n';
                          System.out.println("File " + listOfFiles[i].getName());
                        } else if (listOfFiles[i].isDirectory()) {
                            content+="Directory " + listOfFiles[i].getName()+'\n';
                          System.out.println("Directory " + listOfFiles[i].getName());
                        }
                      }
                      System.out.println(content);
                      to_cli.writeBytes(content+';');
                      //to_cli.flush();
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

class ftp_server {
    public static void main(String argv[]) throws Exception {
        ServerSocket server_socket = new ServerSocket(6969);
        ArrayList<Socket> users = new ArrayList<Socket>();
        System.out.println("startinn");
        while (true) {
            Socket cli = server_socket.accept();
            users.add(cli);
            Parser parser = new Parser(cli, users);

            parser.start();
        }

    }
}