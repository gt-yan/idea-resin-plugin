package org.intellij.j2ee.web.resin.resin.version;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

final class JarResources {
   public boolean debugOn;
   private final Map<String, byte[]> htJarContents;
   private final String jarFileName;
   private final boolean fullLoaded;

   JarResources(String jarFileName) {
      this(jarFileName, true);
   }

   JarResources(String jarFileName, boolean loadAll) {
      this.debugOn = false;
      this.htJarContents = new ConcurrentHashMap();
      this.jarFileName = jarFileName;
      this.fullLoaded = loadAll;
      if (loadAll) {
         this.fullScan();
      }

   }

   public byte[] getResource(String name) {
      if (!this.fullLoaded && !this.htJarContents.containsKey(name)) {
         this.loadOnDemand(name);
      }

      return (byte[])this.htJarContents.get(name);
   }

   private void fullScan() {
      try {
         Map<String, Integer> htSizes = new HashMap();
         ZipFile zf = new ZipFile(this.jarFileName);
         Enumeration<? extends ZipEntry> e = zf.entries();

         while(e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry)e.nextElement();
            htSizes.put(ze.getName(), (int)ze.getSize());
         }

         zf.close();
         FileInputStream fis = new FileInputStream(this.jarFileName);
         BufferedInputStream bis = new BufferedInputStream(fis);
         ZipInputStream zis = new ZipInputStream(bis);

         while(true) {
            ZipEntry ze;
            do {
               if ((ze = zis.getNextEntry()) == null) {
                  return;
               }
            } while(ze.isDirectory());

            if (this.debugOn) {
               PrintStream var10000 = System.out;
               String var10001 = ze.getName();
               var10000.println("ze.getName()=" + var10001 + ",getSize()=" + ze.getSize());
            }

            int size = (int)ze.getSize();
            if (size == -1) {
               size = (Integer)htSizes.get(ze.getName());
            }

            byte[] b = new byte[size];

            int rb;
            int chunk;
            for(rb = 0; size - rb > 0; rb += chunk) {
               chunk = zis.read(b, rb, size - rb);
               if (chunk == -1) {
                  break;
               }
            }

            this.htJarContents.put(ze.getName(), b);
            if (this.debugOn) {
               System.out.println(ze.getName() + "  rb=" + rb + ",size=" + size + ",csize=" + ze.getCompressedSize());
            }
         }
      } catch (NullPointerException var12) {
         System.out.println("done.");
      } catch (Exception var13) {
         var13.printStackTrace();
      }

   }

   private void loadOnDemand(String name) {
      ZipFile zf = null;
      InputStream is = null;

      try {
         zf = new ZipFile(this.jarFileName);
         ZipEntry ze = zf.getEntry(name);
         if (ze != null) {
            is = zf.getInputStream(ze);
            int size = is.available();
            if (size == -1) {
               return;
            }

            byte[] b = new byte[size];
            is.read(b);
            this.htJarContents.put(ze.getName(), b);
            is.close();
            return;
         }
      } catch (Exception var24) {
         var24.printStackTrace();
         return;
      } finally {
         if (zf != null) {
            try {
               zf.close();
            } catch (IOException var23) {
               var23.printStackTrace();
            }
         }

         if (is != null) {
            try {
               is.close();
            } catch (IOException var22) {
               var22.printStackTrace();
            }
         }

      }

   }
}
