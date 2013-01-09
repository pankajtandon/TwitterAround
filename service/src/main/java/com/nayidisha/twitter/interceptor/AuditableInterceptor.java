package com.nayidisha.twitter.interceptor;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.nayidisha.utils.NDStringUtils;
import com.nayidisha.utils.NDUtils;


public class AuditableInterceptor extends EmptyInterceptor{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(AuditableInterceptor.class);
	public boolean onSave(Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) throws CallbackException {
		if ( entity instanceof Auditable ) {
			Date currDate = java.util.Calendar.getInstance().getTime();
			for(int i = 0; i < propertyNames.length; i++){
				log.debug("INSERT: " + entity.toString());
				
				if("createdBy".equals(propertyNames[i])){
					state[i] = this.getUpdatedBy(entity);
				}
				if("updatedDate".equals(propertyNames[i])){
					state[i] = currDate;
				}
				if("createdDate".equals(propertyNames[i])){
					state[i] = currDate;
				}
				
			}
		}
		return false;
	}


	public boolean onFlushDirty(Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {

		if ( entity instanceof Auditable ) {
			for(int i = 0; i < propertyNames.length; i++){
						
				
				
				if("updatedDate".equals(propertyNames[i])){
					if (((previousState!=null && previousState[i] != null &&  currentState != null && previousState[i].equals(currentState[i]))
							||(currentState != null && currentState[i] == null))){
						currentState[i] = java.util.Calendar.getInstance().getTime();
					}
				}
				if(("createdBy".equals(propertyNames[i]) )) {
					log.debug("UPDATE: " + (entity==null?"nullEntity":entity.toString()) + " and previous is " + (previousState == null?"NullPS":previousState[i]));
				}
				//Don't change the createdBy and date
				if(("createdBy".equals(propertyNames[i]) || "createdDate".equals(propertyNames[i]))
						&& previousState!=null 
						&& previousState[i]!=null ){
					currentState[i] = previousState[i];
				}
				if("updatedBy".equals(propertyNames[i])){
					currentState[i] = this.getUpdatedBy(entity);
				}
				
				
			}
		}
		return false;
	}
	
	private String getUpdatedBy(Object entity){
		String s = "";
		if (NDStringUtils.hasSomeMeaningfulValue(NDUtils.getCurrentUserId())) {
			s = NDUtils.getCurrentUserId();
			
		} else {
			s= ((Auditable)entity).getUpdatedBy();
		}
		return s;
	}
}
