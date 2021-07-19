package test;

import org.apache.tools.ant.util.DateUtils;
import org.flowable.bpmn.model.*;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.bpmn.behavior.IntermediateCatchConditionalEventActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.IntermediateCatchTimerEventActivityBehavior;

import java.util.Date;
import java.util.List;

/**
 * @Author jiangms
 * @Date 2020-12-31 9:40
 * @Desc 流程开始监听器
 */
public class CatchTimerEventListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        FlowElement currentFlowElement = delegateExecution.getCurrentFlowElement();
        if (currentFlowElement instanceof IntermediateCatchEvent) {
            List<EventDefinition> eventDefinitionList = ((IntermediateCatchEvent) currentFlowElement).getEventDefinitions();
            TimerEventDefinition timerEventDefinition = (TimerEventDefinition) eventDefinitionList.get(0);
            Date now = new Date();
            String nowString = "2021-06-17T11:07:00";//DateUtils.format(now, "yyyy-MM-dd HH:mm:ss").replace(' ', 'T');
            String timeCycle = "R3/"+nowString+"/PT30S";
            timerEventDefinition.setTimeCycle(timeCycle);
        }
    }
}
