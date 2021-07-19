package test;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.*;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author he.ai aihehe123@gmail.com
 * 使用场景：
 * 功能描述：
 */
public class BoundarySignalTest {

    public static void main(String[] args) throws InterruptedException {

        ProcessEngineConfiguration cfg = (ProcessEngineConfiguration) new StandaloneProcessEngineConfiguration()
                .setJdbcPassword("")
                .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable_study?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setAsyncExecutorActivate(true);
        //.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        // 部署流程
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ManagementService managementService = processEngine.getManagementService();
        TaskService taskService = processEngine.getTaskService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("test_signal_boundary_catch_event.bpmn20.xml")
                .deploy();

        String deploymentId = deployment.getId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        Map<String, Object> variables = new HashMap<>();

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId(), variables);

        List<Execution> executionList = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("signal")
                .list();
        System.out.println(executionList.toString());

        //Thread.sleep(30 * 1000);

        //runtimeService.signalEventReceived("signal");

        //任务处理
        List<Task> tasks = taskService.createTaskQuery().deploymentId(deploymentId).list();

        for (Task task : tasks) {
            System.out.println(task.getId());
            //taskService.complete(task.getId(), variables);
        }

    }

}