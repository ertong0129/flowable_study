package test;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class SignalJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        FlowElement currentFlowElement = execution.getCurrentFlowElement();
        System.out.println("SignalStart"+currentFlowElement.getName());
    }
}
