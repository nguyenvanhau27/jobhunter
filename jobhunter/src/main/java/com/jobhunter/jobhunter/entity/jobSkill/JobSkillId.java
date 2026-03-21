package com.jobhunter.jobhunter.entity.jobSkill;

import lombok.Data;

import java.io.Serializable;

@Data
public class JobSkillId implements Serializable {

    private Long job;
    private Long skill;
}
