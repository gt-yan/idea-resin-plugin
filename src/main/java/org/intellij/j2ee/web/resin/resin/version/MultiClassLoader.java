package org.intellij.j2ee.web.resin.resin.version;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class MultiClassLoader extends ClassLoader {
   private final Map<String, Class> classes = new ConcurrentHashMap();
   private char classNameReplacementChar;
   protected boolean monitorOn = false;
   protected boolean sourceMonitorOn = true;

   public Class loadClass(String className) throws ClassNotFoundException {
      return this.loadClass(className, true);
   }

   public synchronized Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
      this.monitor(">> MultiClassLoader.loadClass(" + className + ", " + resolveIt + ")");
      Class result = (Class)this.classes.get(className);
      if (result != null) {
         this.monitor(">> returning cached result.");
         return result;
      } else {
         try {
            result = super.findSystemClass(className);
            this.monitor(">> returning system class (in CLASSPATH).");
            return result;
         } catch (ClassNotFoundException var6) {
            this.monitor(">> Not a system class.");
            byte[] classBytes = this.loadClassBytes(className);
            if (classBytes == null) {
               throw new ClassNotFoundException();
            } else {
               result = this.defineClass(className, classBytes, 0, classBytes.length);
               if (result == null) {
                  throw new ClassFormatError();
               } else {
                  if (resolveIt) {
                     this.resolveClass(result);
                  }

                  this.classes.put(className, result);
                  this.monitor(">> Returning newly loaded class.");
                  return result;
               }
            }
         }
      }
   }

   public void setClassNameReplacementChar(char replacement) {
      this.classNameReplacementChar = replacement;
   }

   protected abstract byte[] loadClassBytes(String var1);

   protected String formatClassName(String className) {
      if (this.classNameReplacementChar == 0) {
         return className.replace('.', '/') + ".class";
      } else {
         String var10000 = className.replace('.', this.classNameReplacementChar);
         return var10000 + ".class";
      }
   }

   protected void monitor(String text) {
      if (this.monitorOn) {
         print(text);
      }

   }

   protected static void print(String text) {
      System.out.println(text);
   }
}
