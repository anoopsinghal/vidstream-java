package dev.anoop.vidstream.controllers;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.util.ResourceUtils;

@RestController
public class VideoController {
  @GetMapping(value = "/play/media/video")
  @ResponseBody
  public ResponseEntity<StreamingResponseBody> playMediaV01(
     @RequestHeader(value = "Range", required = false)
     String rangeHeader)
  {
     try
     {
        StreamingResponseBody responseStream;
        File vid = ResourceUtils.getFile("classpath:static/sample-mp4-file.mp4");
        Long fileSize = vid.length();
        byte[] buffer = new byte[1024];      
        final HttpHeaders responseHeaders = new HttpHeaders();
  
        if (rangeHeader == null)
        {
           responseHeaders.add("Content-Type", "video/mp4");
           responseHeaders.add("Content-Length", fileSize.toString());
           responseStream = os -> {
              RandomAccessFile file = new RandomAccessFile(vid, "r");
              try (file)
              {
                 long pos = 0;
                 file.seek(pos);
                 while (pos < fileSize - 1)
                 {                            
                    file.read(buffer);
                    os.write(buffer);
                    pos += buffer.length;
                 }
                 os.flush();
              } catch (Exception e) {}
           };
           
           return new ResponseEntity<StreamingResponseBody>
                  (responseStream, responseHeaders, HttpStatus.OK);
        }
  
        String[] ranges = rangeHeader.split("-");
        Long rangeStart = Long.parseLong(ranges[0].substring(6));
        Long rangeEnd;
        if (ranges.length > 1)
        {
           rangeEnd = Long.parseLong(ranges[1]);
        }
        else
        {
           rangeEnd = fileSize - 1;
        }
           
        if (fileSize < rangeEnd)
        {
           rangeEnd = fileSize - 1;
        }
  
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        responseHeaders.add("Content-Type", "video/mp4");
        responseHeaders.add("Content-Length", contentLength);
        responseHeaders.add("Accept-Ranges", "bytes");
        responseHeaders.add("Content-Range", "bytes" + " " + 
                             rangeStart + "-" + rangeEnd + "/" + fileSize);
        final Long _rangeEnd = rangeEnd;
        responseStream = os -> {
           RandomAccessFile file = new RandomAccessFile(vid, "r");
           try (file)
           {
              long pos = rangeStart;
              file.seek(pos);
              while (pos < _rangeEnd)
              {                        
                 file.read(buffer);
                 os.write(buffer);
                 pos += buffer.length;
              }
              os.flush();
           }
           catch (Exception e) {}
        };
           
        return new ResponseEntity<StreamingResponseBody>
               (responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
     }
     catch (FileNotFoundException e)
     {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }
     catch (IOException e)
     {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
     }
  }
}