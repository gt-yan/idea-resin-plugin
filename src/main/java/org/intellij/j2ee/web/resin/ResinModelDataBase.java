package org.intellij.j2ee.web.resin;

import com.intellij.util.xmlb.annotations.Tag;

public class ResinModelDataBase {
   @Tag("port")
   private int myPort = 80;
   @Tag("mbean-port")
   private int myJmxPort = 9999;
   private String myCharset = "";

   public String getCharset() {
      return this.myCharset;
   }

   public void setCharset(String charset) {
      this.myCharset = charset;
   }

   public int getPort() {
      return this.myPort;
   }

   public void setPort(int port) {
      this.myPort = port;
   }

   public int getJmxPort() {
      return this.myJmxPort;
   }

   public void setJmxPort(int jmxPort) {
      this.myJmxPort = jmxPort;
   }
}
