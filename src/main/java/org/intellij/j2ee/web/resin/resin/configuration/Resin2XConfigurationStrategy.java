package org.intellij.j2ee.web.resin.resin.configuration;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.intellij.j2ee.web.resin.resin.WebApp;
import org.jdom.Element;

class Resin2XConfigurationStrategy extends ResinConfigurationStrategy {
   private static final String HTTP_SERVER = "http-server";
   private static final String HTTP = "http";
   private static final String PORT = "port";
   private static final String APP_DIR = "app-dir";
   private static final String DIRTY = "dirty";
   private static final String WEB_APP = "web-app";
   private static final String ID = "id";
   private static final String HOST = "host";
   private static final String RESIN_CONF = "resin2.conf";
   private static final String CHARSET = "character-encoding";

   public void setPort(int port) {
      Element rootElement = this.getElement();
      Element httpElement = rootElement.getChild("http-server").getChild("http");
      if (httpElement == null) {
         httpElement = new Element("http");
         httpElement.setAttribute("port", Integer.toString(port));
         rootElement.getChild("http-server").addContent(httpElement);
      } else if (!httpElement.getAttribute("port").getValue().equals(Integer.toString(port))) {
         httpElement.setAttribute("port", Integer.toString(port));
      }

   }

   public boolean deploy(WebApp webApp) {
      boolean dirty = false;
      Element host = getHost(this.getElement().getChild("http-server"), webApp);
      if (host.getAttribute("app-dir") != null) {
         host.removeAttribute("app-dir");
         dirty = true;
      }

      if (host.getAttribute("dirty") != null) {
         host.removeAttribute("dirty");
         dirty = true;
      }

      boolean webAppFound = false;
      List webpapps = host.getChildren("web-app");
      if (webpapps != null) {
         Iterator var6 = webpapps.iterator();

         label53:
         while(true) {
            Element webappEl;
            do {
               do {
                  if (!var6.hasNext()) {
                     break label53;
                  }

                  Object webpapp = var6.next();
                  webappEl = (Element)webpapp;
               } while(!webappEl.getAttribute("id").getValue().equals(webApp.getContextPath()));

               webAppFound = true;
               if (webappEl.getAttribute("app-dir") == null || !webappEl.getAttribute("app-dir").getValue().equals(webApp.getLocation())) {
                  webappEl.setAttribute("app-dir", webApp.getLocation());
                  dirty = true;
               }
            } while(webappEl.getAttribute("character-encoding") != null && webappEl.getAttribute("character-encoding").getValue().equals(webApp.getCharSet()));

            if (webApp.getCharSet() != null && !webApp.getCharSet().trim().isEmpty()) {
               webappEl.setAttribute("character-encoding", webApp.getCharSet());
               dirty = true;
            }
         }
      }

      if (!webAppFound) {
         dirty = true;
         Element newWebAppEl = new Element("web-app");
         newWebAppEl.setAttribute("id", webApp.getContextPath());
         newWebAppEl.setAttribute("app-dir", webApp.getLocation());
         if (webApp.getCharSet() != null && !webApp.getCharSet().trim().isEmpty()) {
            newWebAppEl.setAttribute("character-encoding", webApp.getCharSet());
         }

         host.addContent(newWebAppEl);
      }

      return dirty;
   }

   private static Element getHost(Element parent, WebApp webApp) {
      List hosts = parent.getChildren("host");
      if (hosts != null) {
         Iterator var3 = hosts.iterator();

         while(var3.hasNext()) {
            Object host1 = var3.next();
            Element host = (Element)host1;
            if (host.getAttribute("id").getValue().equals(webApp.getHost())) {
               return host;
            }
         }
      }

      Element host = new Element("host");
      host.setAttribute("id", webApp.getHost());
      host.setAttribute("dirty", "true");
      parent.addContent(host);
      return host;
   }

   public boolean undeploy(WebApp webApp) {
      boolean dirty = false;
      List hosts = this.getElement().getChild("http-server").getChildren("host");
      if (hosts != null) {
         Iterator var4 = hosts.iterator();

         while(true) {
            Element host;
            List webpapps;
            do {
               if (!var4.hasNext()) {
                  return dirty;
               }

               Object host1 = var4.next();
               host = (Element)host1;
               webpapps = host.getChildren("web-app");
            } while(webpapps == null);

            Object[] var8 = webpapps.toArray();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Object webpapp = var8[var10];
               Element webappEl = (Element)webpapp;
               if (webappEl.getAttribute("id").getValue().equals(webApp.getContextPath())) {
                  host.removeContent(webappEl);
                  dirty = true;
               }
            }
         }
      } else {
         return dirty;
      }
   }

   public InputStream getDefaultResinConfContent() {
      return this.getClass().getResourceAsStream("resin2.conf");
   }
}
