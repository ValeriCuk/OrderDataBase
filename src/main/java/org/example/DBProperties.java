package org.example;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
public class DBProperties {
    private String url;
    private String username;
    private String password;

    public DBProperties() {
        InputStream inpStr = getClass().getClassLoader().getResourceAsStream("db.properties");
        Properties prop = new Properties();

        try{
            prop.load(inpStr);
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }

        this.url = prop.getProperty("db.url");
        this.username = prop.getProperty("db.user");
        this.password = prop.getProperty("db.password");
    }
}
