# activiti-sandbox-script
How to run activiti using a sandboxed script engine

## General Approach
1. Create an implementation of org.activiti.engine.impl.scripting.ScriptingEngines which overrides protected method my.seoj.activiti.sandbox.script.Main.PrivilegedScriptingEngines.evaluate(String, String, Bindings)
2. The overridden method should do super.evaluate() wrapped in a AccessController.doPrivileged()
3. Set the custom ScriptingEngines into org.activiti.engine.ProcessEngineConfiguration.
4. Run the application under a Java security manager. (-Djava.security.manager for normal Java apps, -security for Apache Tomcat)

See code https://github.com/seoj/activiti-sandbox-script/blob/master/src/main/java/my/seoj/activiti/sandbox/script/Main.java

## Caveats
- Default interface org.activiti.engine.ProcessEngineConfiguration does not provide method to set custom ScriptingEgines. Need to use the implementation ProcessEngineConfigurationImpl to do that.
- Default ScriptingEngines initialization that happens in ProcessEngineConfigurationImpl is non-trivial. It is safer to wrap the default instance instead of re-configuring a branch new ScriptingEngines.
- To enable security manager, a Java security policy file must be provided also. 
