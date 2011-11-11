package edu.sjtu.se.ist.forever.actions;

import javax.servlet.http.HttpSession;

import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.ActionContext;

public class LoginAction extends ForeverSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String loginfeedback;

	public String execute() throws Exception {
		System.out.println("Enter into login");
		HttpSession session = this.getRequest().getSession();
		String userName = this.getRequest().getParameter(
				ParamConstants.USER_NAME);
		String password = this.getRequest().getParameter(
				ParamConstants.PASSWORD);
		if (session.getAttribute("Login") != null) {
			// already exists a session
			if (!session.getAttribute(ObjectConstants.USER_NAME).equals(
					userName)
					|| !session.getAttribute(ObjectConstants.PASSWORD).equals(
							password)) {
				// authentication fail
				return ActionResultConstants.FAILED;
			}
			// authentication success
			loginfeedback = session.getId();
		} else if (userName.equals("yongjiu") && password.equals("test")) {
			// no session but authentication success
			session.setAttribute("Login", "true");
			session.setAttribute(ObjectConstants.USER_NAME, userName);
			session.setAttribute(ObjectConstants.PASSWORD, password);
			loginfeedback = session.getId();
		} else {
			// authentication fail
			return ActionResultConstants.FAILED;
		}
		System.out.println(loginfeedback);
		return ActionResultConstants.SUCCESS;
	}

	public String getLoginfeedback() {
		return loginfeedback;
	}

	public void setLoginfeedback(String loginfeedback) {
		this.loginfeedback = loginfeedback;
	}
}
