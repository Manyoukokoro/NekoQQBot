/**
  * Copyright 2021 bejson.com 
  */
package org.nekotori.entity;

/**
 * Auto-generated: 2021-08-03 23:17:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class YandereTag {

    private long id;
    private String name;
    private int count;
    private int type;
    private boolean ambiguous;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setCount(int count) {
         this.count = count;
     }
     public int getCount() {
         return count;
     }

    public void setType(int type) {
         this.type = type;
     }
     public int getType() {
         return type;
     }

    public void setAmbiguous(boolean ambiguous) {
         this.ambiguous = ambiguous;
     }
     public boolean getAmbiguous() {
         return ambiguous;
     }

}