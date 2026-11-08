package org.intellij.j2ee.web.resin.resin;

import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.PathUtil;

public class WebApp {
   private final boolean myDefaultContextPath;
   private String myLocation;
   private String myContextPath;
   private String myHost;
   private String myCharset;

   public WebApp(boolean defaultContextPath, String contextPath, String host, String location, String charset) {
      this.myDefaultContextPath = defaultContextPath;
      this.myContextPath = contextPath;
      this.myHost = host;
      this.myLocation = location;
      this.myCharset = charset;
   }

   public String getLocation() {
      return this.myLocation;
   }

   public void setLocation(String location) {
      this.myLocation = location;
   }

   public String getContextPath() {
      return this.myDefaultContextPath ? "/" + FileUtilRt.getNameWithoutExtension(PathUtil.getFileName(this.myLocation)) : this.myContextPath;
   }

   public void setContextPath(String contextPath) {
      this.myContextPath = contextPath;
   }

   public String getHost() {
      return this.myHost == null ? "" : this.myHost;
   }

   public void setHost(String host) {
      this.myHost = host;
   }

   public String getCharSet() {
      return this.myCharset;
   }

   public void setCharSet(String charset) {
      this.myCharset = charset;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof WebApp)) {
         return false;
      } else {
         WebApp webApp = (WebApp)o;
         if (this.myContextPath != null) {
            if (!this.myContextPath.equals(webApp.myContextPath)) {
               return false;
            }
         } else if (webApp.myContextPath != null) {
            return false;
         }

         label42: {
            if (this.myHost != null) {
               if (this.myHost.equals(webApp.myHost)) {
                  break label42;
               }
            } else if (webApp.myHost == null) {
               break label42;
            }

            return false;
         }

         if (this.myLocation != null) {
            if (!this.myLocation.equals(webApp.myLocation)) {
               return false;
            }
         } else if (webApp.myLocation != null) {
            return false;
         }

         if (this.myDefaultContextPath != webApp.myDefaultContextPath) {
            return false;
         } else {
            return true;
         }
      }
   }

   public int hashCode() {
      int result = this.myLocation != null ? this.myLocation.hashCode() : 0;
      result = 29 * result + (this.myContextPath != null ? this.myContextPath.hashCode() : 0);
      result = 29 * result + (this.myHost != null ? this.myHost.hashCode() : 0);
      result = 29 * result + (this.myDefaultContextPath ? 1 : 0);
      return result;
   }
}
