package com.jobhunter.jobhunter.entity.userSkill;

import com.jobhunter.jobhunter.entity.Skill;
import com.jobhunter.jobhunter.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_skils")
@Data
@IdClass(UserSkillID.class)
public class UserSkill {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private String levelSkill;
}
