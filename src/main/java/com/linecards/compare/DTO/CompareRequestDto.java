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
public class CompareRequestDto {
    private String studentName1;
    private String file_content1;
    private String studentName2;
    private String file_content2;
}
