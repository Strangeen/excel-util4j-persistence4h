package online.dinghuiye.core.persistence.testcase;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Strangeen
 * on 2017/8/3
 */
@Entity
@DynamicInsert(true)
@Table(name = "school_man")
public class SchoolMan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "create_time")
    private Date createTime;

    private String description;

    private Integer sex;

    private String college;

    private String grade;

    @Column(name = "class_name")
    private String className;

    private String major;

    private String phone;

    @Column(name = "cate_id")
    private Integer cateId;

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    @Override
    public String toString() {
        return "SchoolMan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", description='" + description + '\'' +
                ", sex=" + sex +
                ", college='" + college + '\'' +
                ", grade='" + grade + '\'' +
                ", className='" + className + '\'' +
                ", major='" + major + '\'' +
                ", phone='" + phone + '\'' +
                ", cateId=" + cateId +
                '}';
    }
}
