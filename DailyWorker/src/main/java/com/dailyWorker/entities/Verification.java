package com.dailyWorker.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="verification")
@Setter
@Getter
public class Verification {

	@Id
	private int workerId;
	private int adharNumber;
	   @Lob
	    @Column(columnDefinition = "LONGBLOB")
	    private byte[] adharPhoto; 
	
}
