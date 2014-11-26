package uk.co.revsys.user.manager.model;

import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotNull;

public abstract class AbstractEntity implements Serializable{
	
	private String id;
    @NotNull
    private Date creationTime;
    @NotNull
    private Date lastModifiedTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    
	
}
