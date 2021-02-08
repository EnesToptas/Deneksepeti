package com.deneksepeti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
//TODO: MUTEX'I AYARLA. NEYIN SINGLETON OLMASI GEREKIYOR ÇÖZ. READ VE WRITE AYNI ANDA İÇİN MUTEX GEREKİYOR MU?
public class SocketServer extends Thread
{
    private ServerSocket serverSocket;
    private AuthHandler authHandler;
    private PostHandler postHandler;
    private int port;
    private boolean running = false;

    public SocketServer( int port, AuthHandler authHandler, PostHandler postHandler )
    {
        this.port = port;
        this.authHandler = authHandler;
        this.postHandler = postHandler;
    }

    public void startServer()
    {
        try
        {
            serverSocket = new ServerSocket( port );
            this.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void stopServer()
    {
        running = false;
        this.interrupt();
    }

    @Override
    public void run()
    {
        running = true;
        while( running )
        {
            try
            {
                System.out.println( "Listening for a connection" );

                // Call accept() to receive the next connection
                Socket socket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                RequestHandler requestHandler = new RequestHandler( socket, authHandler, postHandler );
                requestHandler.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main( String[] args )
    {
        int port = 3000;
        System.out.println( "Start server on port: " + port );

        AuthHandler authHandler = new AuthHandler();
        PostHandler postHandler = new PostHandler();
        SocketServer server = new SocketServer( port, authHandler, postHandler );
        server.startServer();

    }
}

class RequestHandler extends Thread
{
    private Socket socket;
    private AuthHandler authHandler;
    private PostHandler postHandler;

    RequestHandler( Socket socket, AuthHandler authHandler, PostHandler postHandler )
    {
        this.socket = socket;
        this.authHandler = authHandler;
        this.postHandler = postHandler;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println( "Received a connection" );

            // Get input and output streams
            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            PrintWriter out = new PrintWriter( socket.getOutputStream() );

            // Write out our header to the client
            String requestInput = "";

            // Echo lines back to the client until the client closes the connection or we receive an empty line
            String line = in.readLine();
            while( line != null && line.length() > 0 )
            {
                requestInput += line + "\n";
                line = in.readLine();
            }

            String[] result = requestInput.split("\\R", 2);
            String userId;

            switch (result[0]){
                case "signup":
                    signup( out, result[1] );
                    break;
                case "login":
                    login( out, result[1] );
                    break;
                case "getUsers":
                    allUsers( out );
                    break;
                case "getPost":
                    getPost( out, result[1] );
                    break;
                case "addPost":
                    addPost( out, result[1] );
                    break;
                case "applyPost":
                    applyPost( out, result[1] );
                    break;
                case "deletePost":
                    deletePost( out, result[1] );
            }
            out.println();
            out.flush();
            // Close our connection
            in.close();
            out.close();
            socket.close();

            System.out.println( "Connection closed" );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    private void allUsers( PrintWriter out )
    {
        out.println(authHandler.getUsers());
    }

    private void signup( PrintWriter out, String data )
    {
        String userId = authHandler.addUser( data );
        out.println( userId );
    }

    private void login( PrintWriter out, String data )
    {
        String userId = authHandler.checkPassword( data );
        out.println( userId );
    }

    private void getPost( PrintWriter out, String data )
    {
        String userId = data.trim();
        String posts = postHandler.getPosts( userId );
        out.println( posts );
    }

    private void addPost( PrintWriter out, String data )
    {
        String postId = postHandler.addPost( data.trim() );
        out.println( postId );
    }

    /**
     * applies to a post,
     * prints -2: already applied
     *        -1: quota full
     * quotaLeft: successful
     * @param out
     * @param data a line of postId followed by a line of userId
     */
    private void applyPost( PrintWriter out, String data )
    {
        String[] result = data.split("\\R", 2);
        String postId = result[0].trim();
        String userId = result[1].trim();
        int quotaLeft = postHandler.applyToPost( postId, userId );
        out.println( quotaLeft );
    }

    /**
     * deletes a post, prints 200
     * @param out output writer
     * @param data postId
     */
    private void deletePost( PrintWriter out, String data )
    {
        String postId = data.trim();
        int statusCode = postHandler.deletePost(postId);
        out.println( statusCode );
    }

}