package cn.supra.supralayer_i.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.Display;


/**
 * 这个类用来执行隐藏方法。可部分替代需要编译sdk来显示隐藏方法的问题
 * 调用实例：
 *
 *       ExecuteHideMethod.setField(textView, "mThreshold",1);
 *       Log.e(LOGTAG,ExecuteHideMethod.getFiled(textView, "mThreshold").toString());
 *       //View v=(View)ExecuteHideMethod.executeMethod(textView,"getDropDownAnchorView",null, null);
 *       View v=(View)ExecuteHideMethod.executeMethod(textView,"getDropDownAnchorView",new Class[]{} , new Object[] {});
 *       Log.e(LOGTAG,"test"+v.getId());
 *
 */
public class ReflectionUtils {

    
	//private static final String LOGTAG = "ReflectionUtils";

	@SuppressWarnings("serial")
	public static class ReflectionException extends Exception /*IllegalStateException*/{
		public ReflectionException(String msg, Throwable t){
			super(msg, t);
		}
		public ReflectionException(Throwable t){
			super("Reflection error.", t);
		}
	}
	
	
	/**
	 * 获取实例属性值
	 * 
	 * @param cls 实例的类型, 比如子类获取父类私有属性， 则类型就是父类
	 * @param instance 实例
	 * @param fieldName 属性名
	 * @return
	 * @throws ReflectionException 
	 */
	public static Object getFiledValue(Class<?> cls, Object instance, String fieldName) throws ReflectionException {
		Object o = null;

		try {
			Field f = cls.getDeclaredField(fieldName);
			f.setAccessible(true);
			o = f.get(instance);
		} catch (SecurityException e) {
			throw new ReflectionException(e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException(e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		}
		return o;
	}
	/**
	 * 获取实例属性值
	 * 
	 * @param cls 实例的类型, 比如子类获取父类私有属性， 则类型就是父类
	 * @param instance 实例
	 * @param fieldName 属性名
	 * @return
	 * @throws ReflectionException 
	 */
	public static Object getFiledValue(Field field, Object instance) throws ReflectionException {
		Object o = null;
		
		try {
			field.setAccessible(true);
			o = field.get(instance);
		} catch (SecurityException e) {
			throw new ReflectionException(e);
		}catch (IllegalArgumentException e) {
			throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		}
		return o;
	}
	
	/**
	 * 获取类成员
	 * 
	 * @param cls 实例的类型, 比如子类获取父类私有属性， 则类型就是父类
	 * @param fieldName 属性名
	 * @return
	 * @throws ReflectionException 
	 */
	public static Field getFiled(Class<?> cls,String fieldName) throws ReflectionException {
			Field f = null;
			try {
				f = cls.getDeclaredField(fieldName);
				f.setAccessible(true);
			} catch (SecurityException e) {
				throw new ReflectionException(e);
			} catch (NoSuchFieldException e) {
				throw new ReflectionException(e);
			}
			return f;
	}
    
    /**
     * 获取静态类的属性值
     * @param cls
     * @param fieldName
     * @return
     */
    public static Object getStaticField(Class<?> cls, String fieldName)  throws ReflectionException {
    	Object o = null;
		try {
			Field f = cls.getDeclaredField(fieldName);
			o = f.get(cls);
			
		} catch (SecurityException e) {
			throw new ReflectionException(e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException(e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		}
    	return o;
    }
    
    /**
     * 设置属性值，使用这个方法， 子类也可以设置父类的私有属性
     * @param cls 类
     * @param instance 类(cls或者其子类)的实例
     * @param fieldName 
     * @param value
     */
    public static void setFieldValue(Class<?> cls, Object instance, String fieldName, Object value)  throws ReflectionException {
    	try {
			Field f = cls.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (SecurityException e) {
			throw new ReflectionException(e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException(e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		}
    }
    /**
     * 设置属性值，使用这个方法， 子类也可以设置父类的私有属性
     * @param cls 类
     * @param instance 类(cls或者其子类)的实例
     * @param fieldName 
     * @param value
     */
    public static void setFieldValue(Field field, Object instance,Object value)  throws ReflectionException {
    	try {
    		field.setAccessible(true);
    		field.set(instance, value);
    	} catch (SecurityException e) {
    		throw new ReflectionException(e);
    	}catch (IllegalArgumentException e) {
    		throw new ReflectionException(e);
    	} catch (IllegalAccessException e) {
    		throw new ReflectionException(e);
    	}
    }
    
    /**
     * 执行实例方法
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @param values
     * @return
     */
    
    public static Object executeMethod(Class<?> clazz,Object instance,String methodName,Class<?>[] parameterTypes,Object[] values)  throws ReflectionException {
    	Object o=null;
    	try {
    		Method m=clazz.getDeclaredMethod(methodName, parameterTypes);
    		m.setAccessible(true);
    		o=m.invoke(instance, values);
    	}
    	catch(NoSuchMethodException e) {
    		throw new ReflectionException(e);
    	} catch (IllegalArgumentException e) {
    		throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e);
		} catch(SecurityException e) {
			throw new ReflectionException(e);
		}
		
		return o;
    }
    
    
    /**
     * 执行实例方法
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @param values
     * @return
     */
    
    public static Object executeMethod(Object instance,String methodName,Class<?>[] parameterTypes,Object[] values)  throws ReflectionException {
    	Object o=null;
    	Class<?> clazz = instance.getClass();
    	try {
    		Method m=clazz.getDeclaredMethod(methodName, parameterTypes);
//    		Method m=clazz.getm
    		m.setAccessible(true);
    		o=m.invoke(instance, values);
    	}
    	catch(NoSuchMethodException e) {
    		throw new ReflectionException(e);
    	} catch (IllegalArgumentException e) {
    		throw new ReflectionException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e);
		} catch(SecurityException e) {
			throw new ReflectionException(e);
		}
		
		return o;
    }
    
    public static void dumpMethods(Object instance) {
    	Class<?> clazz = instance.getClass();
    	Method[] allMethods = clazz.getDeclaredMethods() ;
    	for(int i= 0; i< allMethods.length; i++) {
    		System.out.println(allMethods[i].toGenericString());
    	}
    		
    }
    
    public static void dumpFields(Object instance) {
    	Class<?> clazz = instance.getClass();
    	Field[] allFields = clazz.getDeclaredFields() ;
    	for(int i= 0; i< allFields.length; i++) {
    		System.out.println(allFields[i].toGenericString());
    	}
    	
    }
    
	public static  Object callHiddenNoParametersMethod(Class clazz,Object instance,String name){
	    if( instance != null ){
	        try {
	            Method method = clazz.getDeclaredMethod(name);
	            method.setAccessible(true);
	            return method.invoke(instance);
	        } catch (NoSuchMethodException e) {
	           e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        } catch (InvocationTargetException e) {
	            e.printStackTrace();
	        }
	    }
	    return null;
	}
	
	/**
	 * 因为有的SDK可能没有提供相关方法。所以通过遍历的方式，如果含有相关方法则调用，没有则不调用。
	 * 
	 * @param clazz
	 * @param instance
	 * @param name
	 */
	public static Object searchAndExecuteMethod(Class<?> clazz,Object instance, String name) {
    	Method[] methods = Display.class.getDeclaredMethods();
    	Object object = null;
    	try {
			for(Method m: methods){
				if(m.getName().equals(name)) {
					m.setAccessible(true);
					object = m.invoke(instance);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    	
    	return object;
	}
	

    
}
