package online.dinghuiye.core.persistence.testcase;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * @author Strangeen
 * on 2017/08/09
 */
@Entity
@DynamicInsert(true)
@Table(name = "user2")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserExtraInfo info;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserExtraInfo getInfo() {
        return info;
    }

    public void setInfo(UserExtraInfo info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", info is null: " + (info == null) +
                '}';
    }
}
