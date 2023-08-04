package dev.anoop.vidstream.impls;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import dev.anoop.vidstream.interfaces.VideoStreamLoader;
import dev.anoop.vidstream.utils.Utils;

@Component
public class VideoStreamLoaderImpl implements VideoStreamLoader{
  private static String localVideoFile = VideoStreamLoader.localVideoFile;

  @Override
  public ResponseEntity<StreamingResponseBody> loadEntireVideoFile() throws IOException {
      Long fileSize = Utils.getFileSize(localVideoFile);
      Long endPos = (fileSize > 0L) ? fileSize - 1 : 0L;
      return loadPartialVideoFile(localVideoFile, 0, endPos);
  } 

  @Override
  public ResponseEntity<StreamingResponseBody> loadPartialVideoFile
                     (String rangeValues)
      throws IOException {
    if (!StringUtils.hasText(rangeValues)) {
      System.out.println("Read all media file content.");
      return loadEntireVideoFile();
    } else {
      long rangeStart = 0L;
      long rangeEnd = 0L;

      Long fileSize = Utils.getFileSize(localVideoFile);
      
      int dashPos = rangeValues.indexOf("-");
      if (dashPos > 0 && dashPos <= (rangeValues.length() - 1)){
        // we have a dash in the string
        String[] rangesArr = rangeValues.split("-");
         
        if (rangesArr != null && rangesArr.length > 0) {
          if (StringUtils.hasText(rangesArr[0])){
              String valToParse = Utils.numericStringValue(rangesArr[0]);
              rangeStart = Utils.safeParseStringValuetoLong(valToParse, 0L);
          } else {
              rangeStart = 0L;
          }
                  
          if (rangesArr.length > 1) {
              String valToParse = Utils.numericStringValue(rangesArr[1]);
              rangeEnd = Utils.safeParseStringValuetoLong(valToParse, 0L);
          } else if (fileSize > 0){
            rangeEnd = fileSize - 1L;
          }
        }
      }

      return loadPartialVideoFile(localVideoFile, rangeStart, rangeEnd);
    }
   }

  @Override
  public ResponseEntity<StreamingResponseBody> loadPartialVideoFile(String localVideoFilePath, long fileStartPos, long fileEndPos) throws IOException
  {
    if (fileStartPos < 0L) {
      fileStartPos = 0L;
    }
    
    long fileSize = Utils.getFileSize(localVideoFilePath);
    if (fileSize > 0L) {
      fileStartPos = Math.min(fileStartPos, fileSize - 1);
      fileEndPos = Math.min(fileEndPos, fileSize - 1);
    } else {
      fileStartPos = 0L;
      fileEndPos = 0L;
    }
    
    StreamingResponseBody responseStream;
    Path filePath = Paths.get(localVideoFilePath); 

    byte[] buffer = new byte[1024];
    String mimeType = Files.probeContentType(filePath);

    final HttpHeaders responseHeaders = new HttpHeaders();
    String contentLength = String.valueOf((fileEndPos - fileStartPos) + 1);
    responseHeaders.add("Content-Type", mimeType);
    responseHeaders.add("Content-Length", contentLength);
    responseHeaders.add("Accept-Ranges", "bytes");
    responseHeaders.add("Content-Range", 
            String.format("bytes %d-%d/%d", fileStartPos, fileEndPos, fileSize));

    final long fileStartPos2 = fileStartPos;
    final long fileEndPos2 = fileEndPos;
    responseStream = os -> {
      RandomAccessFile file = new RandomAccessFile(localVideoFilePath, "r");
      try (file){
        long pos = fileStartPos2;
        file.seek(pos);
        while (pos < fileEndPos2){
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
}
