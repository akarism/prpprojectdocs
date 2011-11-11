package edu.sjtu.se.ist.forever.actions;
public class LogoutAction extends ForeverSupport{
	private static final long serialVersionUID = 1L;
	public String execute() throws Exception {
		System.out.println("Enter into logout");
		this.setUser(null);
		this.getSession().clear();
		return ActionResultConstants.SUCCESS;
	}
}
