package edu.sjtu.se.ist.forever.intercepter;

import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import edu.sjtu.se.ist.forever.actions.ActionResultConstants;
import edu.sjtu.se.ist.forever.actions.LoginAction;

public class AuthenticationIntercepter implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	//watch out the sample code.
	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		ActionContext ctx = ActionContext.getContext();  
        Map<String, Object> session = ctx.getSession();  
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
	}
}
