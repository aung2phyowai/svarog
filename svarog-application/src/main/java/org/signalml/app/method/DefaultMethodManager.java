/* DefaultMethodManager.java created 2007-10-22
 * 
 */

package org.signalml.app.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.signalml.exception.SignalMLException;
import org.signalml.method.InitializingMethod;
import org.signalml.method.Method;

/** DefaultMethodManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultMethodManager implements MethodManager {

	protected static final Logger logger = Logger.getLogger(DefaultMethodManager.class);
	
	protected ArrayList<Method> methods = new ArrayList<Method>();
	protected Map<String,Method> methodsByName = new HashMap<String,Method>();
	protected Map<String,Method> methodsByUID = new HashMap<String, Method>();
	
	@Override
	public int getMethodCount() {
		return methods.size();
	}

	@Override
	public Method[] getMethods() {
		Method[] arr = new Method[methods.size()];
		methods.toArray(arr);
		return arr;
	}
	
	@Override
	public Method getMethodAt(int index) {
		return methods.get(index);
	}

	@Override
	public Method getMethodByName(String name) {
		return methodsByName.get(name);
	}
	
	@Override
	public Method getMethodByUID(String uid) {
		return methodsByUID.get(uid);
	}

	@Override
	public void registerMethod(Method method) {
		if( methods.contains(method) ) {
			return;
		}
		String name = method.getName();
		Method oldMethod = methodsByName.get(name);
		if( oldMethod != null ) {
			removeMethod(oldMethod);			
		}
		String uid = method.getUID();
		oldMethod = methodsByUID.get(uid);
		if( oldMethod != null ) {
			removeMethod(oldMethod);
		}
		methods.add(method);
		methodsByName.put(name, method);
		methodsByUID.put(uid, method);
	}
	
	@Override
	public Method registerMethod( Class<?> clazz ) throws SignalMLException {
		if( !Method.class.isAssignableFrom(clazz) ) {
			throw new ClassCastException("Class is not a method");
		}
		Method method;
		try {
			method = (Method) clazz.newInstance();
		} catch (InstantiationException ex) {
			logger.error("Failed to instantiate method", ex);
			throw new SignalMLException(ex);
		} catch (IllegalAccessException ex) {
			logger.error("Failed to instantiate method - illegal access", ex);
			throw new SignalMLException(ex);
		}
		if( method instanceof InitializingMethod ) {
			((InitializingMethod) method).initialize();
		}
		registerMethod(method);
		return method;
	}

	@Override
	public void removeMethod(Method method) {
		if( !methods.contains(method) ) {
			return;
		}
		methods.remove(method);
		methodsByName.remove(method.getName());
		methodsByUID.remove(method.getUID());
	}	

}
