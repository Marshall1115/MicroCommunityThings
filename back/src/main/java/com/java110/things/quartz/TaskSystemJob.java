package com.java110.things.quartz;

import com.java110.things.entity.task.TaskDto;
import com.java110.things.factory.ApplicationContextFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author
 */

public class TaskSystemJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(TaskSystemJob.class);

    public static String JOB_DATA_CONFIG_NAME = "taskSystemTaskName";
    public static String JOB_DATA_TASK_ID = "taskSystemTaskId";
    public static String JOB_DATA_TASK_ATTR = "taskSystemTaskAttr";
    public static String JOB_DATA_TASK = "taskSystemTask";
    public static String JOB_GROUP_NAME = "taskSystemJobGroup"; // 任务的 分组名称


    private TaskSystemQuartz taskSystemQuartz;

    protected void executeInternal(JobExecutionContext context) {
        try {
            TaskDto taskDto = (TaskDto) (context.getJobDetail().getJobDataMap()
                    .get(JOB_DATA_TASK));


            taskSystemQuartz = (TaskSystemQuartz) ApplicationContextFactory.getBean(taskDto.getTaskTemplateDto().getClassBean());
            taskSystemQuartz.startTask(taskDto);

        } catch (Throwable ex) {
            logger.error("执行任务失败：", ex);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.executeInternal(jobExecutionContext);
    }
}
