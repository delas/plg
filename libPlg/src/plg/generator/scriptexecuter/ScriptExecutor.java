package plg.generator.scriptexecuter;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import plg.exceptions.InvalidScript;

/**
 * This abstract class describes the general structure of a script executor.
 * These scripts can be used for different purposes but, in this project, they
 * are used to generate attribute values.
 * 
 * The idea of this script executor is to call a function called
 * <tt>generate()</tt>. This function will return different values, according to
 * the specific type of the script executor.
 * 
 * @author Andrea Burattin
 */
public abstract class ScriptExecutor {

	private static PythonInterpreter interpreter = new PythonInterpreter();
	private String script;
	private String functionName = "generate";
	protected PyObject result;
	
	/**
	 * Script constructor
	 * 
	 * @param script a Python script
	 * 
	 */
	public ScriptExecutor(String script) {
		this.script = script;
	}
	
	/**
	 * This method executes the script, without returning any value (see
	 * {@link #getValue()} for this).
	 * 
	 * @param arg string with the argument to be passed to the script
	 * @throws InvalidScript this exception is thrown when the required
	 * function is not found
	 */
	public void execute(String arg) throws InvalidScript {
		interpreter.exec(script);
		PyObject generator = interpreter.get(functionName);
		if (generator == null) {
			throw new InvalidScript("The script provided does not contain a " +
					"required `" + functionName + "(str)' function.");
		}
		result = generator.__call__(new PyString(arg));
	}
	
	/**
	 * This method returns the value of an execution of the script
	 * 
	 * @return a value, typed according to the type of the script executor
	 * @throws InvalidScript this exception is thrown when the required
	 * function is not found
	 */
	public abstract Object getValue() throws InvalidScript;
}
