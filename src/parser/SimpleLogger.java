package parser;

public class SimpleLogger
{

	
	// --------------------------------------------------------------------------

	public static void message(String level, String message)
	{
		System.out.println("[" + level + "] : " + message);
	}

	public static void INFO(String message)
	{
		message("INFO", message);
	}
	public static void WARN(String message)
	{
		message("WARN", message);
	}
	public static void ERR(String message)
	{
		message("ERR", message);
	}

	// --------------------------------------------------------------------------

}
