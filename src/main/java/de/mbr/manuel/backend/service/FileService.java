package de.mbr.manuel.backend.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileService {

   public static List<Path> fileList( Path directory ) {
      List<Path> fileNames = new ArrayList<>();
      try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
         for ( Path path : directoryStream ) {
            fileNames.add(path);
         }
      }
      catch ( IOException ex ) {
      }
      return fileNames;
   }

   public static LocalDate getCreationDateTime( Path file ) throws IOException {
      BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
      return attr.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
   }

   public static List<Path> listFiles() {
      String first = ".";
      Path rootDirectory = Paths.get(first);
      return listFiles(rootDirectory);
   }

   public static List<String> listFileLists(){
      List<String> fileLists = new ArrayList<>();
      File metaDataDirectory = new File(".");
      Arrays.stream(metaDataDirectory.list(( dir, name ) -> {
         if ( name.endsWith("txt") ) {
            return true;
         }
         return false;
      })).forEach(f -> fileLists.add(f));
      return fileLists;
   }

   public static List<Path> listFiles(Path rootDirectory) {

      List<Path> images = new ArrayList<>();


      List<Path> rootLevel = fileList(rootDirectory);
      List<Path> allFiles = new ArrayList<>();
      Map<String, List<Path>> filesForDate = new HashMap<>();

      for ( Path path : rootLevel ) {
         if ( Files.isDirectory(path) ) {
            try {
               Files.walk(path).forEach(p -> {
                  allFiles.add(p);
                  try {
                     LocalDate creationDateTime = getCreationDateTime(p);
                     String key = creationDateTime.toString();
                     if ( !filesForDate.containsKey(key) ) {
                        filesForDate.put(key, new ArrayList<>());
                     }
                     filesForDate.get(key).add(p);
                     String contentType = Files.probeContentType(p);
                     if ( contentType != null && contentType.contains("image") ) {
                        images.add(p);
                     }
                  }
                  catch ( IOException e ) {
                     e.printStackTrace();
                  }
               });
            }
            catch ( IOException e ) {
               e.printStackTrace();
            }
         }
      }
      return images;
   }
}
