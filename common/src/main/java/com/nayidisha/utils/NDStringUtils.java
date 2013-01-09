package com.nayidisha.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NDStringUtils {
	

	
	static Log log = LogFactory.getLog(NDStringUtils.class.getName());
	
	public static String safeToString(Object o){
		String s = "";
		if (o != null){
			s = o.toString();
		}
		return s;
	}
	
	public static String removeLineFeedsFromString(String o){
		String s = "";
		if (o != null){
			s = o.replaceAll("[\r\n]", " ");
		}
		return s;
	}
	
	public static String removeSlashesFromString(String o){
		String s = "";
		if (o != null){
			s = StringUtils.replace(o, "/", "_");
			s = StringUtils.replace(s, "\\", "_");
		}
		return s;
	}
	public static String removeSpacesFromString(String o){
		String s = "";
		if (o != null){
			s = StringUtils.replace(o, " ", "");
		}
		return s;
	}
	
	public static String removeSingleQuotesFromString(String o){
		String s = "";
		if (o != null){
			s = StringUtils.replace(o, "'", "");
		}
		return s;
	}
	
	public static String makeStringBlurbSafe(String s){
		s = NDStringUtils.removeLineFeedsFromString(s);
		s = NDStringUtils.removeSlashesFromString(s);
		s = NDStringUtils.removeSingleQuotesFromString(s);
		return s;
	}
	
    public static Collection commaDelimitedStringToCollection(String commaDelimitedString) {
        ArrayList list = null;

        if (commaDelimitedString != null) {
            StringTokenizer st = new StringTokenizer(commaDelimitedString, ",");
            list = new ArrayList();

            while (st.hasMoreTokens()) {
                list.add(st.nextToken().trim());
            }
        }

        return list;
    }
    
    public static void removeEmptyStringsFromCollection(Collection collection){
    	if (collection != null){
    		Iterator iter = collection.iterator();
    		while (iter.hasNext()) {
				Object o = iter.next();
				if (o == null || !NDStringUtils.hasSomeMeaningfulValue(o.toString())){
					collection.remove(o);
				}
			}
    	}
    }
    
    public static String toCommaDelimitedString(Collection sa, boolean withQuotes) {
        StringBuffer sb = new StringBuffer();

        if (sa != null) {

            Iterator iter = sa.iterator();
            while (iter.hasNext()){
            	if (withQuotes){
            		sb.append("'" + (String)iter.next() + "'");
            	} else {
            		sb.append((String)iter.next());
            	}
                sb.append(",");
            }
            if (sb.length()>0){
            	sb.deleteCharAt(sb.length()-1);
            }
        }

        return sb.toString();

    }
    
    public static Collection stringArrayToCollection(String[] sa){
    	String commaDelimitedString = NDStringUtils.toCommaDelimitedString(sa);
    	return NDStringUtils.commaDelimitedStringToCollection(commaDelimitedString);
    	//TODO: Try this return Collections.unmodifiableCollection(Arrays.asList(sa));
    }
    public static String toCommaDelimitedString(String[] sa) {
        StringBuffer sb = new StringBuffer();

        if (sa != null) {

            for (int i=0;i<sa.length;i++) {
                sb.append(sa[i]);
                sb.append(",");
            }
            if (sb.length()>0){
            	sb.deleteCharAt(sb.length()-1);
            }
        }

        return sb.toString();

    }    
    
    /**
     * Returns false if s is null, "", " ", or a string with any number of blanks only, else true
     * @param s
     * @return
     */
    public static boolean hasSomeMeaningfulValue(String s){
    	s = StringUtils.trim(s);
    	boolean boo = false;
    	if (!"null".equalsIgnoreCase(s)){
    		boo = (!StringUtils.isEmpty(s)  && !StringUtils.isBlank(s));
    	}
    	return boo;
    }
    
    /**
     * This method inserts a specified String at every insertWidth number of bytes in the string passed.
     * @param insertStringAt
     * @param insertWidth
     * @return
     */
    public static String insertStringAt(String targetString, int insertWidth, String insertString){
    		ArrayList<String> al = new ArrayList<String>();
    		String currentChunk = targetString;
    		String returnString = "";
        	if (NDStringUtils.hasSomeMeaningfulValue(targetString)){
    	    	for (int i = 0; i < currentChunk.length(); i++) {
    				if ((true) && ((i % insertWidth) == 0)){
    					int till = i + insertWidth;
    					if (till > targetString.length()){
    						till = targetString.length();
    					}
    					al.add(currentChunk.substring(i, till));
 
    				}
    			}
    			
    			Iterator iter = al.iterator();
    			while(iter.hasNext()){
    				String curr = (String)iter.next();
    				returnString = returnString + curr + insertString;
    			}
        	}

    	
    	return returnString;
    }
    
    /* Work on this 8/5/07
    public static String introduceHTMLBreaksAfter(String string, int insertAfter){
    	String s = "";
    	if (NDStringUtils.hasSomeMeaningfulValue(string)){
    		if (string.length() > insertAfter){
    			s = insertStringAt(string, insertAfter, "<BR>");
    		}
    	}
    }
    */
    /**
     * Checks two objects for equality using reflection on fields. The first argument can be a superclass
     * of the second argument or it can be the same class as the second argument.
     * If the objects passed in are Strings, then a simple String compare is done and exceptTheseFields is ignored
     * If the objects being passed in are Collecctions then the list is iterated and on each element of the list
     * fields are interrogated using reflection. The fields mentioned in exceptTheseFields are skipped for the compare. 
     * 
     * Example 1: 
     * if the two objects being passed in are ArrayLists of Property and PropertyView, and the exceptTheseFields 
     * is a list of fields that need to be ignored are "propId" and "poiTypeList", then when each Property and
     * PropertyView is compared, the above 2 flds will not be used in the compare and so, if they are different,
     * they will not cause an entry in the discrepantFieldHash
     * 
     * Example 2:
     * If two collections are passed in and one is null and the other an empty collection, the result will be
     * an empty discprepantFieldHash.
     * 
     * Example 3: If two strings are passed in: null and "", the method will return an empty discrepantFieldHash
     * 
     * Example 4: If two dates are passed in and they are d1 and d2, they are compared, and if different, they end up in the 
     * discrepantFieldHash
     * 
     * Example 5: If two collections are passed in and they have a different number of members, they will 
     * be recorded in the discrepantFieldHash with the difference in size specified.
     * 
     * Example 6: For two strings passed in:    
     * 			null and null are equal
     * 			"" and null are equal
     * 			null and "" are equal
     * 			"" and "" are equal
     * 			abc and ABC are NOT equal so this will appear in the discrepantFieldHash passed in
     * 
     * Fields in the superclass/first argument are compared only, except those listed in the third arg.
     * @param o1 - First object to compare, parent object
     * @param o2 - second object to compare, child object
     * @param exceptTheseFields - leaves these fields out of comparison, can be null.
     * @param discrepantFieldHash - hash containing the names of the fields and the discrepancy in value, cannot be null
     * @param prefix - a uniqueId to be prefixed to every discrepant field name in the discrepantFieldHash
     * Also leaves all static fields and inner class fields and transient flds.
     * @exception throws ClassCastException if o2 is not an instanceof o1
     * @return nothing 
     */
    public static void compareObjects(Object o1, Object o2, ArrayList exceptTheseFields, 
    	Hashtable discrepantFieldHash, String prefix){
    	
    	
    	if ((o1 == null) && (o2 == null)) return;
    	
    	if ((o1 == null) && (o2 != null)) {
    		if (o2 instanceof NDComparable){
    			if (!((NDComparable)o2).isEmpty()){
    				discrepantFieldHash.put(prefix, "A new value " + o2.toString() + " was added where none existed.");
    			} 
    		}else if ((java.util.Collection.class.isInstance(o2))){
				Iterator iterE = ((Collection)o2).iterator();
				while (iterE.hasNext()){
					compareObjects(o1, iterE.next(), exceptTheseFields, discrepantFieldHash, prefix);
				}
    		} else {//Not a collection and not a CCICComparable, string value added
    			compareStrings(null, o2.toString(), discrepantFieldHash, prefix);
    			//discrepantFieldHash.put(prefix, "A new value " + o2.toString() + " was added where none existed.");
    		}
    		return ;
    	}
    	
    	if ((o1 != null) && (o2 == null)) {
    		if (o1 instanceof NDComparable){
    			if (!((NDComparable)o1).isEmpty()){
    	    		discrepantFieldHash.put(prefix, "Old value("+o1.toString() + ") existed and was removed."); 
    			} 
    		}else if ((java.util.Collection.class.isInstance(o1))){
				Iterator iterE = ((Collection)o1).iterator();
				while (iterE.hasNext()){
					compareObjects(iterE.next(), o2, exceptTheseFields, discrepantFieldHash, prefix);
				}
    		} else {//Not a collection and not a CCICComparable. string value removed
    			compareStrings(o1.toString(), null, discrepantFieldHash, prefix);
    			//discrepantFieldHash.put(prefix, "Old value("+o1.toString() + ") existed and was removed.");
    		}
    		return;
    	}
    	if ((o1 instanceof java.util.Date) && (o2 instanceof java.util.Date)) {
    		compareDates((java.util.Date)o1, (java.util.Date)o2, discrepantFieldHash, prefix);
    		return;
    	}
    	if (!((o1.getClass()).isInstance(o2))){
    		String s = "Object1: " + o1.getClass().getName() + " Object2: " + o2.getClass().getName();
    		log.warn(s);
    		throw new ClassCastException(s);
    	}
    	
    	//If these objects have a meaningful toString() impl then do a string compare and we are done
    	if (String.class.isInstance(o1) 
    			|| Double.class.isInstance(o1)
				|| Boolean.class.isInstance(o1)
				|| Integer.class.isInstance(o1)
				|| Long.class.isInstance(o1)
				|| StringBuffer.class.isInstance(o1)
				|| java.util.Date.class.isInstance(o1)
				|| java.sql.Date.class.isInstance(o1)
				){
    		compareStrings(o1.toString(), o2.toString(), discrepantFieldHash, prefix);
    		return;
    	}// else continue...


    	
    	//Deal with lists
    	if ((java.util.Collection.class.isInstance(o1) && (java.util.Collection.class.isInstance(o2)))){
    		//This field is a List
    		Iterator iter1 =((Collection)o1).iterator();
    		Iterator iter2 =((Collection)o2).iterator(); 
    		int i = 0;
    		while ((iter1.hasNext()) && (iter2.hasNext())){//Iterate only over the common elem
    			Object ob1 = iter1.next();
    			Object ob2 = iter2.next();

    			compareObjects(ob1, ob2, exceptTheseFields, discrepantFieldHash, prefix+i);
    			i++;
    		}
    		//Make sure that they are same size
    		int obACount = 0;
    		int obBCount = 0;
    		Iterator iterA =((Collection)o1).iterator();
    		Iterator iterB =((Collection)o2).iterator(); 
    		while (iterA.hasNext()){
    			Object obA  = iterA.next();
    			if ((obA instanceof NDComparable)){
    				if (!((NDComparable)obA).isEmpty()){
    					obACount++;
    				}	
    			}
    		}
    		while (iterB.hasNext()){
    			Object obB  = iterB.next();
    			if ((obB instanceof NDComparable)){
    				if (!((NDComparable)obB).isEmpty()){
    					obBCount++;
    				}	
    			}
    		}
    		if (obACount != obBCount){
    			discrepantFieldHash.put(prefix, "Size1: " + obACount + " Size2: " + obBCount);
    		}
    		return ;
    	} 
    	
    	//Maps
    	if ((Map.class.isInstance(o1) && (Map.class.isInstance(o2)))){
        	compareObjects(((Map)o1).keySet(), ((Map)o2).keySet(), exceptTheseFields, discrepantFieldHash, prefix+":Map:keySet:");
        	compareObjects(((Map)o1).values(), ((Map)o2).values(), exceptTheseFields, discrepantFieldHash, prefix+":Map:values:");
        	
    	}    	
    	
    	String className = o1.getClass().getName();
    	//Only compare cc classes
    	if (!className.startsWith("com.nayidisha")) return;
    	
    	//Not a String or a List.. check indivdual fields via reflection
        Field[] fields = o1.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        //CompareToBuilder builder = new CompareToBuilder();
        for(int i = 0; i < fields.length ; i++)
        {
            Field f = fields[i];
            //if(f.getName().indexOf('$') != -1 
            if ( Modifier.isTransient(f.getModifiers()) 
					|| Modifier.isStatic(f.getModifiers())
					|| (exceptTheseFields == null?
							false:
							(ArrayUtils.contains(exceptTheseFields.toArray(), f.getName())))
					){
            	log.trace("Skipping..." + f.getName());
                continue;
			}
            try{
            	log.trace("Checking..." + prefix+":"+f.getName());
            	compareObjects(f.get(o1), f.get(o2), exceptTheseFields, discrepantFieldHash, prefix+":"+f.getName());
            }catch(IllegalAccessException e){
            	String s = "RuntimeException for field " + f.getName() + " with message: " + e.getMessage();
            	log.warn(s);
                throw new RuntimeException(s);
            }
        }
    	return ;
    }
    
    /**
     * This method will assume that "" and null are the same; so these strings will not be placed in the discrepantHash
     * For example null and null are equal
     * 			"" and null are equal
     * 			null and "" are equal
     * 			"" and "" are equal
     * 			abc and ABC are NOT equal so this will appear in the discrepantFieldHash passed in
     * @param s1
     * @param s2
     * @param discrepantFieldHash can be null
     * @param prefix, prefix that is placed as key in the hashtable passed in, can be null.
     * @return true or false depending on the values passed
     */
    public static boolean compareStrings(String s1, String s2, Hashtable discrepantFieldHash, String prefix){
    	boolean rval = false;
    	if (!NDStringUtils.hasSomeMeaningfulValue(s1)){
    		s1 = null;
    	}
    	if (!NDStringUtils.hasSomeMeaningfulValue(s2)){
    		s2 = null;
    	}
    	boolean compare =  StringUtils.equals(s1, s2);
    	if (!compare){
    		if (discrepantFieldHash != null){
			   discrepantFieldHash.put(prefix==null?"":prefix, "Value1: " + (s1==null?"NULL":s1) 
			   												+ " Value2: " + (s2==null?"NULL":s2));
    		}
		}else {
			rval = true;
		}
    	return rval;
    }

    public static void compareDates(java.util.Date d1, java.util.Date d2, Hashtable discrepantFieldHash, String prefix){
    	if (d1.compareTo(d2) != 0){
    		discrepantFieldHash.put(prefix, "Value1: " + d1 + " Value2: " + d2);
    	}   		
    }
    
    /**
     * This is a convenience method which wraps compareObjects(Object o1, Object o2, ArrayList exceptTheseFields, 
    	Hashtable discrepantFieldHash, String prefix) method.
     *  This method can be used to determine <B>if</B> the objects are different, not <b>what</b> is different between the 
     *  two objects.
     * @param o1
     * @param o2
     * @return true if the objects are the same, false otherwise.
     * @see <code> compareObjects(Object o1, Object o2, ArrayList exceptTheseFields, 
    	Hashtable discrepantFieldHash, String prefix)</code>
     */
    public static boolean compareObjects(Object o1, Object o2){
    	Hashtable hash = new Hashtable();
    	boolean rval;
    	String prefix = "dummy";
    	compareObjects(o1, o2, null, hash, prefix);
    	if (hash.size() > 0){
    		rval = false;
    	} else {
    		rval = true;
    	}
    	return rval;
    }
    
    /** 
     * convenience method for clients that dont care about <b>what</b> is different.
     * @param s1
     * @param s2
     * @return true if the strings are equal, false, if not
     */
    public static boolean compareStrings(String s1, String s2){
    	return compareStrings(s1, s2, null, null);
    }
    
    /**
     * Returns dollars in format "###,##0.00" 
     * @param d
     * @param fmt
     * @return
     */
    public static String getDollarValue(double d,  boolean prefixDollarSign){
    	
    	String s = NDStringUtils.formatDoubleToString(d, "###,##0.00");
    	return (prefixDollarSign?"$ ":"") +s;
    }
    
    public static String getDollarValueFormatted(double d, boolean prefixDollarSign){
    	String s = getDollarValue(d, prefixDollarSign);
    	if (s.equals("$ 0") || s.equals("$ 0.0") || s.equals("$ 0.00") || 
    			s.equals("0") || s.equals("0.0") || s.equals("0.00")){
			s = "";
		}
    	return s;
    }
    
    /**
     * Converts a formatted dollar value to double and to 0 if not formattable
     * @param dollarString
     * @return
     */
    public static double convertToDollarValue(String dollarString){
    	if (dollarString != null){
    		if (dollarString.indexOf(",") != -1){
    			dollarString = StringUtils.replace(dollarString, ",", "");
    		}
    		if (dollarString.indexOf("$") != -1){
    			dollarString = StringUtils.replace(dollarString, "$", "");
    		}
    	}else {
    		dollarString = "0";
    	}
    	double d = 0.0;
    	try {
    		d = Double.parseDouble(dollarString);
    	}catch (Exception e){
    		log.warn("Could not convert " + dollarString + " to double.", e);
    	}
    	return d;
    }
    
    /**
     * Typical formats are "###,###.##" 
     * @param d
     * @param fmt
     * @return
     */
    public static String formatDoubleToString(double d, String fmt){
        String s = "";

        DecimalFormat form = (DecimalFormat) NumberFormat.getInstance();
        form.applyPattern(fmt);
        try {
            s = form.format(new Double(d));
        } catch (IllegalArgumentException iae) {
        	//eat
        }
        return s;
    }
    
    public static byte[] getStringAsByteArray(String string) throws IOException {
        byte[] b = null;
        StringWriter sout = new StringWriter();
        b = string.getBytes();
        sout.flush();
        sout.close();
        return b;        
    } 
    
    public static boolean doesStringArrayContainElement(String[] sa, String element){
    	boolean boo = false;
    	if ((sa != null)  && (element != null)){
    		for (int i = 0; i <  sa.length; i++){
    			if (element.equals(sa[i])){
    				boo = true;
    				break;
    			}
    		}
    	}
    	return boo;
    }
    
    public static boolean doesCollectionContainElement(Collection coll, Object element){
    	boolean boo = false;
    	if ((coll != null)  && (element != null)){
    		Iterator iter = coll.iterator();
    		while(iter.hasNext()){
    		    Object o = iter.next();
    			if (element.equals(o)){
    				boo = true;
    				break;
    			}
    		}
    	}
    	return boo;
    }
    
    public static boolean doCollectionsHaveAnyCommonElement(Collection firstCollection, Collection secondCollection){
    	boolean boo = false;
    	if (firstCollection != null && secondCollection != null){
    		Iterator iter = secondCollection.iterator();
    		while (iter.hasNext()){
    			Object o = iter.next();
    			if (doesCollectionContainElement(firstCollection, o)){
    				boo = true;
    				break;
    			}
    		}
    	}
    	return boo;
    }
    
    public static boolean isFirstCollectionTotallyContainedInSecondCollection(Collection first, Collection second){
    	boolean boo = true;
    	if (first != null && second != null){
    		Iterator iter = first.iterator();
    		while (iter.hasNext()){
	    		Object o = iter.next();
	    		if(!NDStringUtils.doesCollectionContainElement(second, o)) {
	    			boo = false;
	    			break;
	    		}
    		}
    	}
    	return boo;
    }
    
	public static String truncateTo(String s, int i){
		if (NDStringUtils.hasSomeMeaningfulValue(s)){
			if (s.length() > i){
				s = s.substring(0, i);
			}
		}
		return s;
	}
	
	public static String removeTrailingComma(String s){
		if (s != null){
			int comma = s.lastIndexOf(",");
			if (comma > 0){
				s = s.substring(0, comma);
			}
		}
		return s;
	}
	
	/**
	 * Purposely garbled string to make the url look confusing. 
	 * @return
	 */
	public static String getOverrideToken(){
		String s = System.currentTimeMillis()+"";
		String seedha = s.substring(5, 8);
		String ulta = StringUtils.reverse(s.substring(4, 9));
		s = ulta + NDStringUtils.getTodaysHash() + seedha;
		return s;
	}
	
	public static String getTodaysHash(){
		String s = "";
		String dateString = NDDateUtils.applyNDStandardDate(new Date());
		s = Math.abs(StringUtils.reverse(dateString).hashCode()) + "";
		return s;
	}
}
