package com.jobhunter.jobhunter.entity.userSkill;


import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "user_skills")
@IdClass(UserSkillId.class)
public class UserSkill {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "level_skill")
    private String levelSkill;

    public UserSkill() {}

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public String getLevelSkill() { return levelSkill; }
    public void setLevelSkill(String levelSkill) { this.levelSkill = levelSkill; }
}