package com.linecards.compare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
public class CompareResponseDto {
    private int id;
    private String studentName1;
    private String studentName2;
    private String fileContent1;
    private String fileContent2;
    private String filename1;
    private String fileUrl2;
    private int percentageSimilarity;
}
