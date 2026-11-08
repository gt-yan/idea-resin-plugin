package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.execution.ExecutionException;
import java.io.InputStream;
import org.intellij.j2ee.web.resin.ResinModel;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.resin.WebApp;
import org.jdom.Element;

public abstract class ResinConfigurationStrategy {
   private Element myElement;

   public void init(ResinModel serverModel, Element element) throws ExecutionException {
      this.myElement = element;
   }

   protected final Element getElement() {
      return this.myElement;
   }

   public void save() throws ExecutionException {
   }

   public abstract void setPort(int var1);

   public abstract boolean deploy(WebApp var1) throws ExecutionException;

   public abstract boolean undeploy(WebApp var1) throws ExecutionException;

   public abstract InputStream getDefaultResinConfContent();

   public String getServerId() {
      return null;
   }

   public static ResinConfigurationStrategy getForInstallation(ResinInstallation installation) {
      String verNumber = installation.getVersion().getVersionNumber();
      int firstDot = verNumber.indexOf(46);
      int majorVersion = Integer.parseInt(firstDot >= 0 ? verNumber.substring(0, firstDot) : verNumber);
      int minorVersion = -1;
      if (firstDot >= 0) {
         int end = firstDot + 1;

         while(end < verNumber.length() && Character.isDigit(verNumber.charAt(end))) {
            ++end;
         }

         if (end > firstDot + 1) {
            minorVersion = Integer.parseInt(verNumber.substring(firstDot + 1, end));
         }
      }

      if (majorVersion == 2) {
         return new Resin2XConfigurationStrategy();
      } else if (majorVersion == 3) {
         if (minorVersion == 0) {
            return new Resin3XConfigurationStrategy(installation);
         } else {
            return (ResinConfigurationStrategy)(minorVersion == 1 ? new Resin31ConfigurationStrategy(installation) : new ResinXmlConfigurationStrategy(installation));
         }
      } else {
         return (ResinConfigurationStrategy)(majorVersion >= 4 ? new Resin4XmlConfigurationStrategy(installation) : new Resin3XConfigurationStrategy(installation));
      }
   }
}
