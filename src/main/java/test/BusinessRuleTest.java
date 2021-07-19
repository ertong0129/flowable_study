package test;


import liquibase.pro.packaged.O;
import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.impl.rules.RulesDeployer;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderFactoryService;
import org.kie.internal.io.ResourceFactory;

import java.util.*;

/**
 * @author he.ai aihehe123@gmail.com
 * 使用场景：
 * 功能描述：
 */
public class BusinessRuleTest {

    public static void main(String[] args) {

        ProcessEngineConfiguration cfg = (ProcessEngineConfiguration) new StandaloneProcessEngineConfiguration()
                .setJdbcPassword("")
                .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable_study?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setCustomPreDeployers(Arrays.asList(new RulesDeployer()));
                //.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        // 部署流程
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ManagementService managementService = processEngine.getManagementService();
        String rule = "aaabbb";

        /*Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("demo-rule-no-class.drl")
                .addClasspathResource("demo-rule.drl")
                .addClasspathResource("demo-rule-map.drl")
                .addClasspathResource("rule-no-class.bpmn20.xml")
                .deploy();*/

        String deploymentId = "352501";
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        Map<String, Object> variables = new HashMap<>();
        OrderPO order = new OrderPO(), order2 = new OrderPO();
        order.setItemCount(3);
        order2.setItemCount(10);
        variables.put("order", order);
        variables.put("order2", order2);
        variables.put("testInt", 8);
        variables.put("employee", "wang");
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("mapInt", 800);
        variables.put("testMap", testMap);
        List<Integer> testList = new ArrayList<>();
        testList.add(8);
        variables.put("testList", testList);
        Map<String, Object> new_variables = new HashMap<>();
        new_variables.put("testInt", 101);

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId(),variables);
        runtimeService.setVariables(processInstance.getId(), new_variables);

        System.out.println(order.getValid());

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().deploymentId(deploymentId).list();

        for (Task task : tasks) {
            System.out.println(runtimeService.getVariables(task.getExecutionId()));
            taskService.complete(task.getId(), variables);
        }

    }

    public static String getStringByELAndFormData(String el, Map formData) {

        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Object k : formData.keySet()) {
            if (formData.get(k) != null) {
                context.setVariable(k.toString(),
                        factory.createValueExpression(formData.get(k), formData.get(k).getClass()));
            }
        }

        ValueExpression e = factory.createValueExpression(context, el, String.class);
        return (String) e.getValue(context);
    }

    public static void validate(String ruleText) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newByteArrayResource(ruleText.getBytes());
        knowledgeBuilder.add(resource, ResourceType.DRL);
        KnowledgeBuilderErrors packageBuilderErrors = knowledgeBuilder.getErrors();
        if (packageBuilderErrors.size() > 0) {
            throw new RuntimeException("语法校验失败");
        }
    }

}