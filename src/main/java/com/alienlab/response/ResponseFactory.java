package com.alienlab.response;

public class ResponseFactory {
	public static IResponse CreateResponse(){
		return new JSONResponse();
	}
}
