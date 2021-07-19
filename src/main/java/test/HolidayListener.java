package test;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

/**
 * @Author jiangms
 * @Date 2020-12-31 9:40
 * @Desc 流程开始监听器
 */
public class HolidayListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        FlowElement currentFlowElement = delegateExecution.getCurrentFlowElement();
        if (currentFlowElement instanceof StartEvent) {
            System.out.println("*******  StartEvent Start  *******" );
            System.out.println("*******  StartEvent End  *******" );
        } else if (currentFlowElement instanceof UserTask) {
            System.out.println("*******  UserTask Start  *******" );
            System.out.println("*******  UserTask End  *******" );
        }

    }
}
