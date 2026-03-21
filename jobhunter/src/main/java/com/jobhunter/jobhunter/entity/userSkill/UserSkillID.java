package com.jobhunter.jobhunter.entity.userSkill;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSkillID implements Serializable {
    private Long user;
    private Long skill;
}
