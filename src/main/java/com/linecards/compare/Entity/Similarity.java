package com.linecards.compare.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Similarity {
    @Id
    @GeneratedValue
    private int id;
    private int student1_id;
    private int student2_id;
    private int percentage_similarity;
}
