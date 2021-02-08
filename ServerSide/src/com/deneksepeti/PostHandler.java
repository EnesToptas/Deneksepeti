package com.deneksepeti;

import com.deneksepeti.model.PostDto;
import com.deneksepeti.model.Posts;
import com.deneksepeti.model.UserDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PostHandler {
    private Posts posts;
    private Gson gson;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private String postsFilePath = "./resources/posts.json";


    public PostHandler() {
        this.gson = new GsonBuilder().setPrettyPrinting().setDateFormat( "dd/mm/yyyy" ).create();
        try
        {
            this.posts = gson.fromJson( new FileReader( postsFilePath ), Posts.class );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public String addPost( String data ){
        PostDto newPost = gson.fromJson( data, PostDto.class );
        String postId = UUID.randomUUID().toString();
        newPost.setPostId(postId);
        rwlock.writeLock().lock();
        try
        {
            posts.posts.add( newPost );
            saveToFile();
        }
        finally
        {
            rwlock.writeLock().unlock();
        }
        return postId;
    }

    public String getPosts( String userId )
    {
        rwlock.readLock().lock();
        try
        {
            JsonElement postJson = gson.toJsonTree( posts.posts );
            for ( JsonElement jsonElement : postJson.getAsJsonArray() )
            {
                List<String> array = gson.fromJson( jsonElement.getAsJsonObject().get( "users" ), List.class );
                array.stream()
                        .filter( s -> s.equals( userId ) )
                        .findFirst()
                        .ifPresentOrElse(
                                 value -> jsonElement.getAsJsonObject().addProperty( "allowed", false ),
                                () -> jsonElement.getAsJsonObject().addProperty( "allowed", true ));
            }

            return gson.toJson( postJson );
        }
        finally
        {
            rwlock.readLock().unlock();
        }
    }

    public int applyToPost( String postId, String userId )
    {
        rwlock.writeLock().lock();
        try
        {
            PostDto post = posts.posts.stream().filter( postDto -> postDto.getPostId().equals( postId ) ).findFirst().get();

            if( post.getUsers().contains( userId ) ) //Already applied
                return -2;
            else if ( post.getQuota() <= 0 )          //Quota full
                return -1;
            else {
                post.setQuota( post.getQuota() - 1 );
                post.getUsers().add( userId );
                saveToFile();
                return post.getQuota();
            }
        }
        finally
        {
            rwlock.writeLock().unlock();
        }

    }

    public int deletePost( String postId )
    {
        rwlock.writeLock().lock();
        try
        {
            if ( posts.posts.removeIf( p -> p.getPostId().equals( postId ) ) ){
                saveToFile();
                return 200;
            }
            else return 404;

        }
        finally
        {
            rwlock.writeLock().unlock();
        }
    }

    private void saveToFile()
    {
        try
        {
            FileWriter writer = new FileWriter(postsFilePath);
            gson.toJson( posts, writer );
            writer.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

}
