/**
   Path.java
   
   @author Nilo Redini
   @author Davide Pellegrino
   
   This class initializes main paths

*/
package SecureChat.file;


public class Path {
    /** Key directory path */
	public String KEYDIRECTORY = "";
    /** Diffie-Hellman base numbers path*/
	public String PATHDH = "";
    /** Users credentials path */
	public String CREDENTIALSPATH = "";
	
    /** Main constructor*/
	public Path () {
		KEYDIRECTORY = Path.class.getResource("").getPath().split("bin")[0] + "test/KeyFiles/";
		PATHDH = Path.class.getResource("").getPath().split("bin")[0] + "test/KeyFiles/PrimeDH/Prime";
		CREDENTIALSPATH = Path.class.getResource("").getPath().split("bin")[0] + "test/Credentials/";
	}
}
