package edu.sjtu.se.ist.forever.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CalWeightJob extends QuartzJobBean{

	private CalWeightTask calWeightTask;
	
	
	public void setCalWeightTask(CalWeightTask calWeightTask) {
		this.calWeightTask = calWeightTask;
	}


	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		this.calWeightTask.execute();
	}

}
