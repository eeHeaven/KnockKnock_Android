package org.techtown.knockknock.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FirebaseUser {
    public String id;
    public String pw;

    public FirebaseUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FirebaseUser(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getpw() {
        return pw;
    }

    public void setpw(String pw) {
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}
