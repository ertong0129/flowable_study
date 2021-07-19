package test;


import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author he.ai aihehe123@gmail.com
 * 使用场景：
 * 功能描述：
 */
public class ServiceTaskTest {

    public static void main(String[] args) {

        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcPassword("")
                .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable_study?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .setJdbcDriver("com.mysql.jdbc.Driver");
                //.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        // 部署流程
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("服务任务.bpmn20.xml")
                .deploy();

        /*Deployment deployment1 = repositoryService.createDeployment()
                .addClasspathResource("信号启动任务.bpmn20.xml")
                .deploy();*/

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", "wang");

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId(),variables);

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().deploymentId(deployment.getId()).list();

        //runtimeService.signalEventReceived("signal_start3");

        for (Task task : tasks) {
            taskService.complete(task.getId(), variables);
        }

    }
}