package dev.anoop.vidstream.interfaces;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface VideoStreamLoader
{
  // we are only demoing a video stream and not a whole application with
  // error handling. Thus, there is only one file
  String localVideoFile = "classpath:static/sample-mp4-file.mp4";

  ResponseEntity<StreamingResponseBody> loadEntireVideoFile() throws IOException;
  ResponseEntity<StreamingResponseBody> loadPartialVideoFile (String rangeValues) throws IOException;
  ResponseEntity<StreamingResponseBody> loadPartialVideoFile (String localFile, long fileStartPos, long fileEndPos) throws IOException;
}