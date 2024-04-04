package com.example.proyectobeta.List;

import java.io.Serializable;

public class Usuario  implements Serializable {

    private int id;
    private String userName;
    private String userPass;
    private String userMail;
    private String userBirth;
    private int userAcc;
    private byte[] userImage;

    public Usuario() {
    }

    public Usuario(int id, String userName, String userMail, String userBirth, int userAcc,byte[] userImage) {
        this.id = id;
        this.userName = userName;
        this.userMail = userMail;
        this.userBirth = userBirth;
        this.userAcc = userAcc;
        this.userImage = userImage;
    }
    public Usuario(int id, String userName, String userMail,String userPass, String userBirth, int userAcc,byte[] userImage) {
        this.id = id;
        this.userName = userName;
        this.userMail = userMail;
        this.userPass = userPass;
        this.userBirth = userBirth;
        this.userAcc = userAcc;
        this.userImage = userImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public int getUserAcc() {
        return userAcc;
    }

    public void setUserAcc(int userAcc) {
        this.userAcc = userAcc;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }
}
