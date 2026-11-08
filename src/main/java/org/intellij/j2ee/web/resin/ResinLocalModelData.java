package org.intellij.j2ee.web.resin;

public class ResinLocalModelData extends ResinModelDataBase {
   private String myResinConf = "";
   private boolean myDebugConfiguration = false;
   private boolean myAutoBuildClassPath = false;
   private boolean myReadOnlyConfiguration = false;
   private String myAdditionalParameters = "";
   private String myDeployMode = "automatic";
   private String myBaseDirectoryName;

   public String getResinConf() {
      return this.myResinConf;
   }

   public void setResinConf(String resinConf) {
      this.myResinConf = resinConf;
   }

   public boolean isDebugConfiguration() {
      return this.myDebugConfiguration;
   }

   public void setDebugConfiguration(boolean debugConfiguration) {
      this.myDebugConfiguration = debugConfiguration;
   }

   public boolean isAutoBuildClassPath() {
      return this.myAutoBuildClassPath;
   }

   public void setAutoBuildClassPath(boolean autoBuildClassPath) {
      this.myAutoBuildClassPath = autoBuildClassPath;
   }

   public boolean isReadOnlyConfiguration() {
      return this.myReadOnlyConfiguration;
   }

   public void setReadOnlyConfiguration(boolean readOnlyConfiguration) {
      this.myReadOnlyConfiguration = readOnlyConfiguration;
   }

   public String getAdditionalParameters() {
      return this.myAdditionalParameters;
   }

   public void setAdditionalParameters(String additionalParameters) {
      this.myAdditionalParameters = additionalParameters;
   }

   public String getDeployMode() {
      return this.myDeployMode;
   }

   public void setDeployMode(String deployMode) {
      this.myDeployMode = deployMode;
   }

   public String getBaseDirectoryName() {
      return this.myBaseDirectoryName;
   }

   public void setBaseDirectoryName(String baseDirectoryName) {
      this.myBaseDirectoryName = baseDirectoryName;
   }
}
