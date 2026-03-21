package com.jobhunter.jobhunter.entity.jobSkill;

import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.Skill;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job_skills")
@Data
@IdClass(JobSkillId.class)
public class JobSkill {
    @Id
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Id
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

}
