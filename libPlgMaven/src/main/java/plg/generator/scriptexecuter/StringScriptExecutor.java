package plg.generator.scriptexecuter;

import org.python.antlr.adapter.AstAdapters;

import plg.exceptions.InvalidScript;

/**
 * This class describes a script executor that will generate a string value
 * 
 * @author Andrea Burattin
 */
public class StringScriptExecutor extends ScriptExecutor {

	/**
	 * Script constructor
	 * 
	 * @param script a Python script
	 */
	public StringScriptExecutor(String script) {
		super(script);
	}

	@Override
	public String getValue() throws InvalidScript {
		if (result == null) {
			return null;
		}
		return result.__tojava__(Object.class).toString();
	}
}
