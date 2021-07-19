package test;

import org.apache.tools.ant.util.DateUtils;
import org.flowable.bpmn.model.*;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.Date;
import java.util.List;

/**
 * @Author jiangms
 * @Date 2020-12-31 9:40
 * @Desc 流程开始监听器
 */
public class BoundaryTimerEventListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        FlowElement currentFlowElement = delegateExecution.getCurrentFlowElement();
        System.out.println(currentFlowElement.getId());
        if (currentFlowElement instanceof UserTask && currentFlowElement.getId().equals("userTask1")) {
            List<BoundaryEvent> boundaryEventList = ((UserTask) currentFlowElement).getBoundaryEvents();
            BoundaryEvent boundaryEvent = boundaryEventList.get(0);
            List<EventDefinition> eventDefinitionList = boundaryEvent.getEventDefinitions();
            TimerEventDefinition timerEventDefinition = (TimerEventDefinition) eventDefinitionList.get(0);
            Date now = new Date();
            String nowString = DateUtils.format(now, "yyyy-MM-dd HH:mm:ss").replace(' ', 'T');
            String timeCycle = "R10/"+nowString+"/PT30S";
            //timerEventDefinition.setTimeCycle(timeCycle);
        }
    }
}
