/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author SST3ALBISG
 */
public abstract class BaseProperties {
        
    protected abstract String propsFilename();
    public enum Keys{};
    
    protected Properties load(){
        String workDirPath = System.getProperty("user.dir");
        File workDir = new File(workDirPath);
        File propsFile = new File(workDir, propsFilename());
        InputStream in;
        
        Properties props = new Properties();
        try {
            if(propsFile.isFile()){
                in = new FileInputStream(propsFile);
                System.out.println(propsFilename() + " file located.");
            }else{
                in = getClass().getClassLoader().getResourceAsStream(propsFilename());
                System.out.println(propsFilename() + " file not located. Default properties file used.");
            }
            

            props.load(in);
            in.close();
            
            checkAllKeys(props);           
                        
        } catch (IOException ex) {
            System.out.println(propsFilename() + " cannot be read.");
            System.out.println(ex.getMessage());
            throw new AssertionError();
        } 
        
        return props;
    }        

    private void checkAllKeys(Properties props) {
                        
        for (Keys key : Keys.values()) {
            if (!props.containsKey(key.name())) {                
                System.out.println(propsFilename() + " file not setup correctly: " + key.name());
                throw new AssertionError();                
            }
        }        
    }
    
    protected abstract void assignKeys(Properties props);    
}
