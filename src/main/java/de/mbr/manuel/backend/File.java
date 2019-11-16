package de.mbr.manuel.backend;

import java.io.Serializable;


public class File implements Serializable {

   private static final long serialVersionUID = 4114010494427285923L;

   private String path;

   private String name;

   public String getName() {
      return name;
   }

   public void setName( String name ) {
      this.name = name;
   }

   public String getPath() {
      return path;
   }

   public void setPath( String path ) {
      this.path = path;
   }
}
