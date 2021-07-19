package test;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class QuartzJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        FlowElement currentFlowElement = execution.getCurrentFlowElement();
        System.out.println("定时启动事件执行任务"+currentFlowElement.getName());
    }
}
