package com.paradisecloud.fcm.terminal.fs.model;

public class FreeSwitchUser
{

    private Long id;
    private String userId;
    private String password;
    private Long deptId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	@Override
	public String toString() {
		return "FreeSwitchUser [id=" + id + ", userId=" + userId + ", password=" + password + ", deptId=" + deptId
				+ "]";
	}

	public FreeSwitchUser(Long id, String userId, String password, Long deptId) {
		super();
		this.id = id;
		this.userId = userId;
		this.password = password;
		this.deptId = deptId;
	}

	public FreeSwitchUser() {
		super();
		// TODO Auto-generated constructor stub
	}
}
