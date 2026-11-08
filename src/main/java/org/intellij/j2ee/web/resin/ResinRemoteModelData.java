package org.intellij.j2ee.web.resin;

import com.intellij.javaee.transport.TransportTarget;
import com.intellij.util.xmlb.annotations.Tag;

public class ResinRemoteModelData extends ResinModelDataBase {
   @Tag("host-id")
   private String myTransportHostId;
   @Tag("transport-target-webapps")
   private TransportTarget myTransportTargetWebApps;

   public String getTransportHostId() {
      return this.myTransportHostId;
   }

   public void setTransportHostId(String transportHostId) {
      this.myTransportHostId = transportHostId;
   }

   public TransportTarget getTransportTargetWebApps() {
      return this.myTransportTargetWebApps;
   }

   public void setTransportTargetWebApps(TransportTarget transportTargetWebApps) {
      this.myTransportTargetWebApps = transportTargetWebApps;
   }
}
