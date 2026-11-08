package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.javaee.appServers.deployment.DeploymentStatus;
import com.intellij.openapi.util.Ref;
import org.intellij.j2ee.web.resin.ResinModelBase;
import org.intellij.j2ee.web.resin.resin.WebApp;
import org.jetbrains.annotations.NotNull;

public interface JmxConfigurationStrategy {
   boolean deployWithJmx(ResinModelBase var1, WebApp var2);

   boolean undeployWithJmx(ResinModelBase var1, WebApp var2);

   @NotNull DeploymentStatus getDeployStateWithJmx(ResinModelBase var1, WebApp var2, Ref<Boolean> var3);
}
