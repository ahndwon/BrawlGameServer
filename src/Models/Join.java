package Models;

public class Join {

    private String user;

    public Join(String user){
        this.user = user;
    }

    public Join(){
        this.user = "";
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}