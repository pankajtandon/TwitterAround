package com.nayidisha.twitter.faces.utils;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class FacesUtils {

	    
	    public static Object getBean(String beanReference){
	    	ValueBinding vb = FacesContext.getCurrentInstance().getApplication().createValueBinding("#{"+ beanReference + "}");
	        return vb.getValue(FacesContext.getCurrentInstance());
	    }
	    public static String getRequestParameter(String name) {
	        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
	    }
	    
	    public static UIComponent findComponentInRoot(String id) {
	        UIComponent component = null;

	        FacesContext facesContext = FacesContext.getCurrentInstance();
	        if (facesContext != null) {
	          UIComponent root = facesContext.getViewRoot();
	          //component = root.findComponent(id);
	          component = findComponent(root, id);
	        }

	        return component;
	    }
	    private static UIComponent findComponent(UIComponent base, String id) {
	        if (id.equals(base.getId()))
	          return base;
	      
	        UIComponent kid = null;
	        UIComponent result = null;
	        Iterator kids = base.getFacetsAndChildren();
	        while (kids.hasNext() && (result == null)) {
	          kid = (UIComponent) kids.next();
	          if (id.equals(kid.getId())) {
	            result = kid;
	            break;
	          }
	          result = findComponent(kid, id);
	          if (result != null) {
	            break;
	          }
	        }
	        return result;
	    }

}
