package edu.sjtu.se.ist.forever.intercepter;

import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import edu.sjtu.se.ist.forever.actions.ActionResultConstants;
import edu.sjtu.se.ist.forever.actions.ActionRoleMapping;
import edu.sjtu.se.ist.forever.actions.LoginAction;
import edu.sjtu.se.ist.forever.actions.ObjectConstants;
import edu.sjtu.se.ist.forever.domain.ForeverUser;

public class AuthenticationIntercepter implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	//watch out the sample code.
//	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		ActionContext ctx = ActionContext.getContext();  
        Map session = ctx.getSession();  
        Action action = (Action) actionInvocation.getAction();  
        if (action instanceof LoginAction) {  
            return actionInvocation.invoke();  
        }  
        String status = (String) session.get("Login");  
        if (status == null) {  
            return ActionResultConstants.FAILED;  
        } else {  
            return actionInvocation.invoke();  
        }
		/*
		if(null!=ai.getInvocationContext().getSession().get(ObjectConstants.USER_KEY))
		{
			ForeverUser user=(ForeverUser)ai.getInvocationContext().getSession().get(ObjectConstants.USER_KEY);
			String actionName = ai.getProxy().getActionName();
			if(!ActionRoleMapping.CanCall(user.getRole(), actionName))
			{
				return ActionResultConstants.LOGIN;
			}
			return ai.invoke();
		}
		else{
			return ActionResultConstants.LOGIN;
		}*/
	}

}
