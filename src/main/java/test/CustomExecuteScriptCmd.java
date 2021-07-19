package test;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.scripting.ScriptingEngines;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.util.CommandContextUtil;

/**
 * 使用flowable底层执行脚本的逻辑，执行自定义脚本
 * flowable-engine-6.5.0.jar
 * org.flowable.engine.impl.bpmn.behavior.ScriptTaskActivityBehavior#execute
 *
 */
public class CustomExecuteScriptCmd implements Command<Void> {

    private String scriptText;
    private String language;
    private DelegateExecution execution;
    private boolean storeScriptVariables;

    public CustomExecuteScriptCmd(){}

    public CustomExecuteScriptCmd(String scriptText, String language, DelegateExecution execution, boolean storeScriptVariables){
        this.scriptText = scriptText;
        this.language = language;
        this.execution = execution;
        this.storeScriptVariables = storeScriptVariables;
    }

    public CustomExecuteScriptCmd(String scriptText, String language, DelegateExecution execution){
        this.scriptText = scriptText;
        this.language = language;
        this.execution = execution;
        this.storeScriptVariables = false;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ScriptingEngines scriptingEngines = CommandContextUtil.getProcessEngineConfiguration().getScriptingEngines();
        Object result = scriptingEngines.evaluate(this.scriptText, this.language, this.execution, this.storeScriptVariables);
        return null;
    }
}
