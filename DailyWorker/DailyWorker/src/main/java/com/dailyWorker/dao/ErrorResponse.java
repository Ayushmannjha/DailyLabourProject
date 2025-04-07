package com.dailyWorker.dao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
	protected boolean success;
	protected String message;
}
