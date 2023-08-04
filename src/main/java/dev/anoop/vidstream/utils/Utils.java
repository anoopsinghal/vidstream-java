package dev.anoop.vidstream.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public final class Utils {
  public static String numericStringValue(String origVal){
    String retVal = "";
    if (StringUtils.hasText(origVal)){
        retVal = origVal.replaceAll("[^0-9]", "");
        System.out.println("Parsed Long Int Value: [" + retVal + "]");
    }
    
    return retVal;
  }
  
  public static long safeParseStringValuetoLong(String valToParse, long defaultVal){
    long retVal = defaultVal;
    if (StringUtils.hasText(valToParse)){
        try {
          retVal = Long.parseLong(valToParse);
        } catch (NumberFormatException ex){
          retVal = defaultVal;
        }
    }
    
    return retVal;
  }

  public static Long getFileSize(String localFile) throws IOException{
    // assumes file exists
    File vid = ResourceUtils.getFile(localFile);
    return vid.length();
  }

}
