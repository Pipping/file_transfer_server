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
                    byt = from_cli.read();
                    if ((char) byt != ' ') {
                        command += (char) byt;
                    } else {
                        break;
                    }
                }
                System.out.println(command);
                if (command.equals("send")) {
                    byte[] buffer = new byte[4 * 1024];
                    while (true) {

                        String buf = "";
                        int a = 0;
                        a = from_cli.read();
                        buf += (char) a;
                        if ((char) a == 'e')
                            for (int i = 0; i < 2; ++i) {
                                a = from_cli.read();
                                buf += (char) a;
                            }
                        // System.out.println("buf is::"+buf);
                        if (buf.equals("end")) {
                            break;
                        } else {
                            filename += buf;
                        }

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

                        String buf = "";
                        int a = 0;
                        a = from_cli.read();
                        buf += (char) a;
                        if ((char) a == 'e')
                            for (int i = 0; i < 2; ++i) {
                                a = from_cli.read();
                                buf += (char) a;
                            }
                        // System.out.println("buf is::"+buf);
                        if (buf.equals("end")) {
                            break;
                        } else {
                            filename += buf;
                        }

                    }
                    System.out.println("filename is ::" + filename);
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