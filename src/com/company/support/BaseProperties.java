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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author SST3ALBISG
 */
public abstract class BaseProperties {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseProperties.class);
    
    protected abstract String propsFilename();
    //protected Enum Keys;
        
    protected Properties load(){
        String workDirPath = System.getProperty("user.dir");
        File workDir = new File(workDirPath);
        File propsFile = new File(workDir, propsFilename());
        InputStream in;
        
        Properties props = new Properties();
        try {
            if(propsFile.isFile()){
                in = new FileInputStream(propsFile);
                props.load(in);
                in.close();    
                logger.info(propsFilename() + " file located.");                
                //checkAllKeys(props);                
            }else{                
                logger.info(propsFilename() + " file not located.");
            }                                    

        } catch (IOException ex) {
            logger.info(propsFilename() + " cannot be read.");
            logger.info(ex.getMessage());
            throw new AssertionError();
        } 
        
        return props;
    }        
    
    protected abstract void checkAllKeys(Properties props);        
    
    protected abstract void assignKeys(Properties props);    
}
