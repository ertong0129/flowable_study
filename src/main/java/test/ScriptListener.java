package test;

import org.flowable.engine.ManagementService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.Objects;

/**
 * @Author jiangms
 * @Date 2020-12-31 9:40
 * @Desc 流程开始监听器
 */
public class ScriptListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("*******  ProcessStartListener Start  *******" );
        ManagementService managementService = ScriptTest.processEngine.getManagementService();
        String scriptText = "println 'script start';\n" +
                "                println execution.getVariables();\n" +
                "                execution.setVariable(\"test\", 123)";
        managementService.executeCommand(new CustomExecuteScriptCmd(scriptText, "groovy", delegateExecution));
        System.out.println("*******  ProcessStartListener End  *******" );
    }
}
