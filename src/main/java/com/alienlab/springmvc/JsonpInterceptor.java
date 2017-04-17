package com.alienlab.springmvc;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class JsonpInterceptor implements HandlerInterceptor {

	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("aaa");
		
	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		String secret=arg0.getHeader("secret");
		//Object loginuser=arg0.getSession().getAttribute("loginname");
		if(secret==null){
			arg1.setCharacterEncoding("utf-8");
			arg1.getWriter().write("{\"result\":\"非法请求，您无权限请求该资源。\",\"code\":\"9999\"}");
			return false;
		}else{
			if(!secret.equals("a6l1i0e2nblaalbn2e0i1l6a")){
				arg1.setCharacterEncoding("utf-8");
				arg1.getWriter().write("{\"result\":\"非法请求，您无权限请求该资源。\",\"code\":\"9999\"}");
				return false;
			}
			String callback=arg0.getParameter("callback");
			String accept=arg0.getHeader("Accept");
			//System.out.println("accept>>>"+accept);
			if(accept.indexOf("/json")<0&&callback!=null){
				//System.out.println("class>>"+arg2.getClass());
				String uri=arg0.getRequestURI();
				Method [] methods=arg2.getClass().getDeclaredMethods();
				Method m=null;
				for(int i=0;i<methods.length;i++){
					try{
						String [] requestmapping=methods[i].getAnnotation(RequestMapping.class).value();
						if(uri.indexOf(requestmapping[0])>=0){
							m=methods[i];
							break;
						}else{
							continue;
						}
					}catch(Exception ex){
						ex.printStackTrace();
						continue;
					}
				}
				Object o=m.invoke(arg2.getClass().newInstance(),arg0);
				String result=callback+"("+o+")";
				//System.out.println(result);
				arg1.setCharacterEncoding("utf-8");
				arg1.getWriter().write(result);
				return false;
			}else{
				return true;
			}
		}
	}

}
