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
		try {
			//this.calWeightTask.execute();
		} catch (Exception e) {
			e.printStackTrace();
			}
	}
	
	public static void main(String[] args)
	{
		CalWeightTask cwt = new CalWeightTask();
		try{
			String[] printData = new String[] { "2", "3", "2", "2", "2", "1"};
			double[] res = cwt.execute(printData);
			for (int i=0; i<res.length; i++)
			{
				System.out.println(res[i]);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
