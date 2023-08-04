package dev.anoop.vidstream.controllers;

import java.io.IOException;
import java.io.FileNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import dev.anoop.vidstream.interfaces.VideoStreamLoader;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class VideoController {
   private VideoStreamLoader videoLoaderService;
   
   public VideoController(VideoStreamLoader videoLoaderService)
   {
      this.videoLoaderService = videoLoaderService;
   }
   

  @GetMapping(value = "/play/video")
  @ResponseBody
  public ResponseEntity<StreamingResponseBody> playVideoV01(
     @RequestHeader(value = "Range", required = false)
     String rangeHeader)
  {
    try{
      ResponseEntity<StreamingResponseBody> retVal = 
        this.videoLoaderService.loadPartialVideoFile(rangeHeader);
      
      return retVal;
    } catch (FileNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (IOException e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}