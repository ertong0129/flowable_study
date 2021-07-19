package test;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.impl.rules.RulesDeployer;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.util.*;

/**
 * @author he.ai aihehe123@gmail.com
 * 使用场景：
 * 功能描述：
 */
public class BoundaryTimerTest {

    public static void main(String[] args) {

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
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("test_timer_boundary_event.bpmn20.xml")
                .deploy();

        String deploymentId = deployment.getId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        ObjectNode objectNode = bpmnJsonConverter.convertToJson(bpmnModel);
        System.out.println(JSON.toJSONString(objectNode));
        System.out.println(objectNode.toPrettyString());

        Map<String, Object> variables = new HashMap<>();

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId(), variables);

        Scanner sc = new Scanner(System.in);
        sc.next();

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().deploymentId(deploymentId).list();

        for (Task task : tasks) {
            System.out.println(task.getId());
            taskService.complete(task.getId(), variables);
        }

    }

}