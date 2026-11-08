package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.NullableLazyValue;
import java.io.File;
import java.io.IOException;
import org.jdom.Element;
import org.jdom.JDOMException;

public class ResinConfigImport {
   private static final Logger LOG = Logger.getInstance(ResinConfigImport.class);
   private final Element myRoot;
   private final NullableLazyValue<Element> myImportDoc = new NullableLazyValue<Element>() {
      protected Element compute() {
         String path = ResinConfigImport.this.myRoot.getAttributeValue("path");

         try {
            return JDOMUtil.load(new File(path));
         } catch (IOException | JDOMException var3) {
            ResinConfigImport.LOG.debug(var3);
            return null;
         }
      }
   };
   private ResinGeneratedConfig myCopy;
   private ExecutionException myCopyException;

   public ResinConfigImport(Element root) {
      this.myRoot = root;
   }

   public Element getImportDoc() {
      return (Element)this.myImportDoc.getValue();
   }

   public void copy() {
      if (this.myCopy == null && this.myCopyException == null) {
         try {
            this.myCopy = new ResinGeneratedConfig(this.getImportDoc(), "resin-import");
            this.myRoot.setAttribute("path", this.myCopy.getFile().getAbsolutePath());
         } catch (ExecutionException var2) {
            this.myCopyException = var2;
         }
      }

   }

   public void save() throws ExecutionException {
      if (this.myCopyException != null) {
         throw this.myCopyException;
      } else {
         if (this.myCopy != null) {
            this.myCopy.save();
         }

      }
   }
}
