package my.seoj.activiti.sandbox.script;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;

import javax.script.Bindings;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.scripting.ScriptingEngines;

public class Main
{
    public static class PrivilegedScriptingEngines extends ScriptingEngines
    {
        public PrivilegedScriptingEngines(ScriptingEngines scriptingEngines)
        {
            super(scriptingEngines.getScriptBindingsFactory());
        }

        @Override
        protected Object evaluate(String script, String language, Bindings bindings)
        {
            Permissions permissions = new Permissions();
            // permissions.add(new AllPermission());
            // permissions.add(new FilePermission("", ""));
            // permissions.add(new SocketPermission("", ""));

            CodeSource codeSource = new CodeSource(null, (Certificate[]) null);
            ProtectionDomain protectionDomain = new ProtectionDomain(codeSource, permissions);
            ProtectionDomain[] protectionDomains = {protectionDomain};
            AccessControlContext accessControlContext = new AccessControlContext(protectionDomains);
            return AccessController.doPrivileged(new PrivilegedAction<Object>()
            {
                @Override
                public Object run()
                {
                    return PrivilegedScriptingEngines.super.evaluate(script, language, bindings);
                }
            }, accessControlContext);
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println(System.getSecurityManager() != null ? "Running under security manager" : "Not running under security manager");

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        ProcessEngineConfigurationImpl processEngineConfigurationImpl = (ProcessEngineConfigurationImpl) processEngineConfiguration;

        ScriptingEngines defaultScriptingEngines = processEngineConfigurationImpl.getScriptingEngines();
        PrivilegedScriptingEngines securityWrappedScriptingEngines = new PrivilegedScriptingEngines(defaultScriptingEngines);
        processEngineConfigurationImpl.setScriptingEngines(securityWrappedScriptingEngines);

        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("workflow.bpmn20.xml").deploy();

        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("test");
    }
}
