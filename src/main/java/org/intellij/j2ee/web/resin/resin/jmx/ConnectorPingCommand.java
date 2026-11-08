package org.intellij.j2ee.web.resin.resin.jmx;

import java.io.IOException;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import org.intellij.j2ee.web.resin.ResinModelBase;
import org.intellij.j2ee.web.resin.resin.configuration.Resin3XConfigurationStrategy;

public class ConnectorPingCommand extends ConnectorCommandBase<Boolean> {
   private final int myJmxPort;

   public ConnectorPingCommand(ResinModelBase resinModel, int jmxPort) {
      super(resinModel);
      this.myJmxPort = jmxPort;
   }

   protected int getJmxPort() {
      return this.myJmxPort;
   }

   protected Boolean doExecute(MBeanServerConnection connection) throws JMException, IOException {
      return "active".equalsIgnoreCase((String)connection.getAttribute(Resin3XConfigurationStrategy.MBEAN_WEB_APP_DEPLOY, "State"));
   }
}
