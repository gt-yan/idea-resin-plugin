package org.intellij.j2ee.web.resin.resin.version;

class JarClassLoader extends MultiClassLoader {
   private final JarResources jarResources;

   JarClassLoader(String jarName) {
      this.jarResources = new JarResources(jarName, false);
   }

   protected byte[] loadClassBytes(String className) {
      className = this.formatClassName(className);
      return this.jarResources.getResource(className);
   }
}
