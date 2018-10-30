package plg.generator.scriptexecuter;

import org.python.core.Py;

import plg.exceptions.InvalidScript;

/**
 * This class describes a script executor that will generate an integer value
 * 
 * @author Andrea Burattin
 */
public class IntegerScriptExecutor extends ScriptExecutor {

	/**
	 * Script constructor
	 * 
	 * @param script a Python script
	 */
	public IntegerScriptExecutor(String script) {
		super(script);
	}

	@Override
	public Integer getValue() throws InvalidScript {
		if (result == null) {
			return null;
		}
		return Py.py2int(result);
	}
}
