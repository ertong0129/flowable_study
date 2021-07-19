package test;


import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.*;

/**
 * @author he.ai aihehe123@gmail.com
 * 使用场景：
 * 功能描述：
 */
public class TaskDelegateTest {


    public static void main(String[] args) {
        // 首先实例化ProcessEngine，线程安全对象，一般全局只有一个即可，从ProcessEngineConfiguration创建的话，可以调整一些
        // 配置，通常我们会从XML中创建，至少要配置一个JDBC连接
        // 如果是在Spring的配置中，使用SpringProcessEngineConfiguration

        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
//                .setJdbcDriver("org.h2.Driver")
//                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable_study?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .setJdbcDriver("com.mysql.jdbc.Driver");

                // 如果数据表不存在的时候，自动创建数据表
                //.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();


        // 部署流程
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("task-delegate.bpmn20.xml")
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        // 启动process实例，需要一些初始化的变量，这里我们简单的从Scanner中获取，一般在线上会通过接口传递过来
        Scanner scanner= new Scanner(System.in);

        System.out.println("Who are you?");
        String employee = scanner.nextLine();

        System.out.println("How many holidays do you want to request?");
        Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());

        System.out.println("Why do you need them?");
        String description = scanner.nextLine();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employee", employee);
        variables.put("nrOfHolidays", nrOfHolidays);
        variables.put("description", description);

        // 当创建实例的时候，execution就被创建了，然后放在启动的事件中，这个事件可以从数据库中获取，
        // 用户后续等待这个状态即可
        /*ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("holidayRequest", variables);*/
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId(),variables);
                //runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        // 在Flowable中数据库的事务对数据一致性起着关键性的作用。
        // 查询和完成任务

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i=0; i<tasks.size(); i++) {
            for (int j = 0; j < 5; j++) {
                Task subTask = createSubTask(taskService, (TaskEntity) tasks.get(i), "审批人"+j);
                createSubTask(taskService, (TaskEntity) subTask, "审批人100"+j);
                taskService.deleteTask(subTask.getId());
            }
            //TaskEntity subTask = createSubTask(taskService, (TaskEntity) tasks.get(i), "审批人"+i);
            System.out.println((i+1) + ") " + tasks.get(i).getName());
        }


        System.out.println("Which task would you like to complete?");
        int taskIndex = Integer.valueOf(scanner.nextLine());
        Task task = tasks.get(taskIndex - 1);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " wants " +
                processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");

        boolean approved = scanner.nextLine().toLowerCase().equals("y");
        variables = new HashMap<String, Object>();
        variables.put("approved", approved);
        taskService.complete(task.getId(), variables);


        /*HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> activities =
                historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .finished()
                        .orderByHistoricActivityInstanceEndTime().asc()
                        .list();

        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityId() + " took "
                    + activity.getDurationInMillis() + " milliseconds");
        }*/
    }

    public static TaskEntity createSubTask(TaskService taskService, TaskEntity parentTask, String assignee) {
        if (parentTask == null) return null;
        TaskEntity newTask = (TaskEntity) taskService.newTask();
        newTask.setCategory(parentTask.getCategory());
        newTask.setDescription(parentTask.getDescription());
        newTask.setTenantId(parentTask.getTenantId());
        newTask.setAssignee(assignee);
        newTask.setName(parentTask.getName());
        newTask.setParentTaskId(parentTask.getId());
        newTask.setProcessDefinitionId(parentTask.getProcessDefinitionId());
        newTask.setProcessInstanceId(parentTask.getProcessInstanceId());
        newTask.setTaskDefinitionKey(parentTask.getTaskDefinitionKey());
        newTask.setTaskDefinitionId(parentTask.getTaskDefinitionId());
        newTask.setPriority(parentTask.getPriority());
        newTask.setCreateTime(new Date());
        taskService.saveTask(newTask);
        return newTask;
    }
}