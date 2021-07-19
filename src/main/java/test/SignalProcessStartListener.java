package test;


import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.Objects;

/**
 * @Author jiangms
 * @Date 2020-12-31 9:40
 * @Desc 流程开始监听器
 */
public class SignalProcessStartListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("启动");
        /*FlowElement currentFlowElement = delegateExecution.getCurrentFlowElement();
        System.out.println("SignalStart="+delegateExecution.getRootProcessInstanceId());*/
    }
}
