package org.intellij.j2ee.web.resin.resin.common;

import com.intellij.openapi.options.ConfigurationException;
import javax.swing.JTextField;

public abstract class ParseUtil {
   public int parseInt(JTextField text) throws ConfigurationException {
      String toParse = text.getText();
      if (toParse == null) {
         toParse = "";
      }

      toParse = toParse.trim();

      try {
         return Integer.parseInt(toParse);
      } catch (NumberFormatException var4) {
         throw new ConfigurationException(this.getErrorMessage(toParse));
      }
   }

   protected abstract String getErrorMessage(String var1);
}
