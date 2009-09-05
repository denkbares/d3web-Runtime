package de.d3web.dialog2.basics.usermanaging;

public class UserBean {

    private User user;

    public UserBean() {
	user = UserManager.getInstance().getUserByEmail("guest@guest");
    }

    public User getUser() {
	return user;
    }

}
