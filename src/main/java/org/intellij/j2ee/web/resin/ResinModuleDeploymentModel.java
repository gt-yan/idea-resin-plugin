package org.intellij.j2ee.web.resin;

import com.intellij.javaee.appServers.context.DeploymentModelContext;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.deployment.DeploymentSource;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.SkipDefaultsSerializationFilter;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;

public class ResinModuleDeploymentModel extends DeploymentModel implements DeploymentModelContext {
   private ResinModuleDeploymentModelData myData = new ResinModuleDeploymentModelData();

   public ResinModuleDeploymentModel(CommonModel commonModel, DeploymentSource source) {
      super(commonModel, source);
   }

   public boolean isDefaultContextRoot() {
      return this.isDefaultContextPath();
   }

   public String getContextRoot() {
      return this.getContextPath();
   }

   public boolean isDefaultContextPath() {
      return this.myData.isDefaultContextPath();
   }

   public void setDefaultContextPath(boolean defaultContextPath) {
      this.myData.setDefaultContextPath(defaultContextPath);
   }

   public String getContextPath() {
      return this.myData.getContextPath();
   }

   public void setContextPath(String contextPath) {
      this.myData.setContextPath(contextPath);
   }

   public String getHost() {
      return this.myData.getHost();
   }

   public void setHost(String host) {
      this.myData.setHost(host);
   }

   public void writeExternal(Element element) throws WriteExternalException {
      super.writeExternal(element);
      XmlSerializer.serializeInto(this.myData, element, new SkipDefaultsSerializationFilter());
   }

   public void readExternal(Element element) throws InvalidDataException {
      super.readExternal(element);
      this.myData = new ResinModuleDeploymentModelData();
      XmlSerializer.deserializeInto(this.myData, element);
   }

   public static class ResinModuleDeploymentModelData {
      private String myContextPath = "/";
      private String myHost = "";
      private boolean myDefaultContextPath = true;

      public boolean isDefaultContextPath() {
         return this.myDefaultContextPath;
      }

      public void setDefaultContextPath(boolean defaultContextPath) {
         this.myDefaultContextPath = defaultContextPath;
      }

      public String getContextPath() {
         return this.myContextPath;
      }

      public void setContextPath(String contextPath) {
         this.myContextPath = contextPath;
      }

      public String getHost() {
         return this.myHost;
      }

      public void setHost(String host) {
         this.myHost = host;
      }
   }
}
