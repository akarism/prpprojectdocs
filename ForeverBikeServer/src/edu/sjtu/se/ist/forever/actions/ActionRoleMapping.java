package edu.sjtu.se.ist.forever.actions;

/**
 * for future use, to judge whether user has this authority to do this job
 * @author andy
 *
 */
public class ActionRoleMapping {

	
	public static int canDoActionRole(String actionName){
		return -1;
	}
	public static boolean CanCall(int role,String actionName)
	{
		if(role>=canDoActionRole(actionName)){
			return true;
		}
		return false;
	}
}
