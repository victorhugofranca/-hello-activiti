package br.com.hello.activiti.webcomp.security;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class LoginAction {

	private String name;
	private String password;

	public String login() {
		System.out.println("Login");
		return "index";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
