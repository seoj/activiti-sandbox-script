# activiti-sandbox-script
How to run activiti using a sandboxed script engine

## General Approach
1. Create an implementation of org.activiti.engine.impl.scripting.ScriptingEngines which overrides protected method my.seoj.activiti.sandbox.script.Main.PrivilegedScriptingEngines.evaluate(String, String, Bindings)
2. The overridden method should do super.evaluate() wrapped in a AccessController.doPrivileged()
3. Set the custom ScriptingEngines into org.activiti.engine.ProcessEngineConfiguration via method provided in org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl
4. Run the application under a Java security manager. (-Djava.security.manager for normal Java apps, -security for Apache Tomcat)
