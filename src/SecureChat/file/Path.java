package SecureChat.file;


public class Path {
	public String KEYDIRECTORY = "";
	public String PATHDH = "";
	public String CREDENTIALSPATH = "";
	

	public Path () {
		KEYDIRECTORY = Path.class.getResource("").getPath().split("bin")[0] + "test/KeyFiles/";
		PATHDH = Path.class.getResource("").getPath().split("bin")[0] + "test/KeyFiles/PrimeDH/Prime";
		CREDENTIALSPATH = Path.class.getResource("").getPath().split("bin")[0] + "test/Credentials/";
	}
}
