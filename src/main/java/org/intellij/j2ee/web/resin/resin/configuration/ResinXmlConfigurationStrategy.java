package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.intellij.j2ee.web.resin.ResinModel;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.jdom.Element;
import org.jdom.filter2.Filters;
import org.jdom.xpath.XPathExpression;
import org.jdom.xpath.XPathFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

class ResinXmlConfigurationStrategy extends Resin31ConfigurationStrategy {
   private static final Logger LOG = Logger.getInstance(ResinXmlConfigurationStrategy.class);
   public static final @NonNls String IMPORT_SINGLE_PATH_ATTRIBUTE = "path";
   private static final @NonNls String[] IMPORT_ATTRIBUTE_NAMES = new String[]{"path", "fileset"};
   private static final @NonNls String CONF_FOLDER_VAR = "${__DIR__}";
   protected static final @NonNls String RESIN_CONF = "resin32.xml";
   private List<ResinConfigImport> myImports;

   ResinXmlConfigurationStrategy(ResinInstallation resinInstallation) {
      super(resinInstallation);
   }

   public void init(ResinModel serverModel, Element element) throws ExecutionException {
      super.init(serverModel, element);
      this.myImports = new ArrayList();
      this.resolveImports(element, this.myImports);
   }

   public InputStream getDefaultResinConfContent() {
      return this.getClass().getResourceAsStream("resin32.xml");
   }

   protected void resolveImports(Element rootElement, @Nullable List<ResinConfigImport> imports) {
      try {
         File confFolder = new File(this.getInstallation().getResinHome(), "conf");
         String confFolderPath = FileUtil.toSystemIndependentName(confFolder.getAbsolutePath());
         String[] var5 = IMPORT_ATTRIBUTE_NAMES;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String importAttrName = var5[var7];
            XPathExpression<Element> xpath = XPathFactory.instance().compile(".//*[name()='resin:import' or name()='resin:properties'][@" + importAttrName + "]", Filters.element());
            List<Element> elements = xpath.evaluate(rootElement);
            Iterator var11 = elements.iterator();

            while(var11.hasNext()) {
               Element element = (Element)var11.next();
               String path = element.getAttributeValue(importAttrName);
               element.setAttribute(importAttrName, StringUtil.replace(path, "${__DIR__}", confFolderPath));
               if (imports != null && StringUtil.equals("path", importAttrName)) {
                  imports.add(new ResinConfigImport(element));
               }
            }
         }
      } catch (RuntimeException var14) {
         LOG.info(var14);
      }

   }

   protected final List<ResinConfigImport> getImports() {
      return this.myImports;
   }

   public void save() throws ExecutionException {
      Iterator var1 = this.getImports().iterator();

      while(var1.hasNext()) {
         ResinConfigImport configImport = (ResinConfigImport)var1.next();
         configImport.save();
      }

   }
}
