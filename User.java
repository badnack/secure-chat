/**
   This classe allows to manage users' account
*/

class User{
    private String UserName;
    private String FriendName;
    private String serverIp;
    private String clientIp;
    private int serverPort;
    private int clientPort;
    private Rsa rsa;

    public User(String name, int port,String server,String KeyDir){
        this.UserName = name;
        this.serverPort = port;
        this.serverIp = server;
        rsa = new Rsa(KeyDir);  
    }

    public void setClientPort(int port){
        clientPort = port;
    }

    public void setClientIp(String ip){
        clientIp = ip;
    }

    public void setFriendName(String name){
        FriendName = name;
    }

    public void setServerPort(int port){
        this.serverPort = port;
    }
   
    public void setServerIp(String server){
        this.serverIp = server;
    }

    public String getUserName(){
        return UserName;
    }

    public String getFriendName(){
        return FriendName;
    }
    public int getClientPort(){
        return clientPort;
    }

    public String getClientIp(){
        return clientIp;
    }
    
    public int getServerPort(){
        return serverPort;
    }
   
    public String getServerIp(){
        return this.serverIp;
    } 
    

    /**RSA methods*/
    public void CreateRsa() throws Exception{
        rsa.setUserName(UserName);
        rsa.createKeys();
    }
    
    public boolean isRsaPresent(String UserName){
        return  rsa.isPresent(UserName);
    }
    public String Decrypt(byte[] data) throws Exception{
        return rsa.Decrypt(data);
    }

    public byte[] Encrypt(String data) throws Exception{        
        return rsa.Encrypt(data,rsa.GetPublicKey(FriendName));
    }
}