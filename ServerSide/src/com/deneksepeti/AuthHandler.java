package com.deneksepeti;

import com.deneksepeti.model.UserDto;
import com.deneksepeti.model.Users;
import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AuthHandler {
    private Users users;
    private Gson gson;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private String usersFilePath = "./resources/usersDB.json";

    public AuthHandler()
    {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            this.users = gson.fromJson( new FileReader(usersFilePath), Users.class );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public String addUser( String data )
    {
        UserDto newUser = gson.fromJson( data, UserDto.class );
        String userId = UUID.randomUUID().toString();
        newUser.setUserId(userId);
        rwlock.writeLock().lock();
        try
        {
            users.users.add( newUser );
            saveToFile();
        }
        finally
        {
            rwlock.writeLock().unlock();
        }
        return userId;
    }

    public String checkPassword( String data )
    {
        UserDto requestUser = gson.fromJson( data, UserDto.class );
        rwlock.readLock().lock();
        try
        {
            Optional<UserDto> foundUser = users.users.stream().
                    filter(userDto -> userDto.getEmail().equals( requestUser.getEmail() )).findFirst();

            if(foundUser.isPresent()){
                if(foundUser.get().getPassword().equals(requestUser.getPassword()))
                    return foundUser.get().getUserId() + "\n" + foundUser.get().getDisplayName();
                else return "401";
            }
            else return "404";
        }
        finally {
            rwlock.readLock().unlock();
        }

    }

    public String getUsers()
    {
        rwlock.readLock().lock();
        try
        {
            JsonElement userJson = gson.toJsonTree( users.users );
            for ( JsonElement jsonElement : userJson.getAsJsonArray() )
            {
                jsonElement.getAsJsonObject().remove( "password" );
            }
            return gson.toJson( userJson );
        }
        finally
        {
            rwlock.readLock().unlock();
        }

    }

    private void saveToFile()
    {
        try
        {
            FileWriter writer = new FileWriter( usersFilePath );
            gson.toJson( users, writer );
            writer.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


}
