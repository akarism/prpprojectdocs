package edu.sjtu.se.ist.forever.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

import edu.sjtu.se.ist.forever.domain.ForeverUser;


public class ForeverSupport extends ActionSupport 
implements SessionAware, ApplicationAware, ServletRequestAware{
	private static final long serialVersionUID = 1L;
	private String timestamp;
	private String task=null;
	@SuppressWarnings("unchecked")
	private Map session;
	@SuppressWarnings("unchecked")
	private Map application;
	@SuppressWarnings("unchecked")
	private HttpServletRequest request;
	@SuppressWarnings("unchecked")
//	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
		this.session=arg0;
	}

	@SuppressWarnings("unchecked")
//	@Override
	public void setApplication(Map arg0) {
		// TODO Auto-generated method stub
		this.application=arg0;
	}
	@SuppressWarnings("unchecked")
//	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// TODO Auto-generated method stub
		this.request=arg0;
	}
	@SuppressWarnings("unchecked")
	public Map getApplication(){
		return this.application;
	}
	@SuppressWarnings("unchecked")
	public Map getSession(){
		return this.session;
	}
	@SuppressWarnings("unchecked")
	public HttpServletRequest getRequest(){
		return this.request;
	}

	public ForeverUser getUser() {
		return (ForeverUser) getSession().get(ObjectConstants.USER_KEY);
	}

	@SuppressWarnings("unchecked")
	public void setUser(ForeverUser user) {
		getSession().put(ObjectConstants.USER_KEY, user);
	}
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
