package com.linecards.compare.controller;

import com.linecards.compare.DTO.CompareRequestDto;
import com.linecards.compare.DTO.CompareResponseDto;
import com.linecards.compare.Entity.Compare;
import com.linecards.compare.Util.CompareAlgorithm;
import com.linecards.compare.service.CompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
@RequestMapping("student")
@EnableCaching
public class CompareController {

    @Autowired
    CompareRequestDto compareRequestDto;
    @Autowired
    CompareResponseDto compareResponseDto;
    @Autowired
    CompareService compareService;
    @Autowired
    Compare compare;
    @Autowired
    CompareAlgorithm compareAlgorithm;

    //  public ResponseEntity<StudentResponseDto> uploadFile(@RequestBody StudentRequestDto filecontent){
    @Value("${file.upload-dir}")
    String FILE_DIRECTORY;

    @PostMapping("/compare")
    public ResponseEntity<String> compare(@RequestParam String studentName1, @RequestParam("File1") MultipartFile file1, @RequestParam("File2") MultipartFile file2, @RequestParam String studentName2) throws Exception {
        try {
            File File1 = new File(FILE_DIRECTORY + file1.getOriginalFilename());
            File File2 = new File(FILE_DIRECTORY + file2.getOriginalFilename());

            FileOutputStream fileOutputStreams1 = new FileOutputStream(File1);
            FileOutputStream fileOutputStreams2 = new FileOutputStream(File2);
            fileOutputStreams1.write(file1.getBytes());
            fileOutputStreams2.write(file2.getBytes());

            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(File1));
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(File2));
            StringBuilder stringBuilder1 = new StringBuilder();
            String content1 = "";
            String content2 = "";
            while ((content1 = bufferedReader1.readLine()) != null) {
                content1 += bufferedReader1.readLine();
                stringBuilder1.append(content1);
                stringBuilder1.append('\n');
            }

            StringBuilder stringBuilder2 = new StringBuilder();
            while ((content2 = bufferedReader2.readLine()) != null) {
                content2 += bufferedReader2.readLine();
                stringBuilder2.append(content2);
                stringBuilder2.append('\n');
            }

            String file1Content = stringBuilder1.toString();
            String file2Content = stringBuilder2.toString();
            System.out.println("File1:" + file1Content);
            System.out.println("File2:" + file2Content);
            System.out.println(stringBuilder1.compareTo(stringBuilder2));

            int percentageSimilarity = compareAlgorithm.lock_match(stringBuilder1.toString(), stringBuilder2.toString());
            compare.setStudentName1(studentName1);
            compare.setStudentName2(studentName2);
            compare.setFileContent1(file1Content);
            compare.setFileContent2(file2Content);
            compare.setFilename1(file1.getOriginalFilename());
            compare.setFilename2(file2.getOriginalFilename());
            compare.setPercentageSimilarity(percentageSimilarity);
            compareService.saveCompare(compare);

            bufferedReader1.close();
            bufferedReader2.close();

            System.out.println("Both file contents are " + percentageSimilarity + "% similar");
            return ResponseEntity.ok("Both file contents are " + percentageSimilarity + "% similar");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Something went wrong");
    }

    @GetMapping("/history")
    //@Cacheable(key="#id", value="#pO")
    public List<Compare> viewHistory(){
        return compareService.findAllCompare();
    }

    @GetMapping("/history/{field}")
    public List<Compare> viewHistoryWithSort(@PathVariable String field){
        return compareService.findAllCompareWithSorting(field);
    }

    @GetMapping("/history/pagination/{offset}/{pageSize}")
    public Page<Compare> viewHistoryWithPagination(@PathVariable int offset, @PathVariable int pageSize){
        Page<Compare> compareWithPagination =  compareService.findAllCompareWithPagination(offset, pageSize);
        return compareWithPagination;
    }

    @GetMapping("/history/paginationAndSort/{offset}/{pageSize}/{field}")
    public Page<Compare> viewHistoryWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field){
        Page<Compare> compareWithPagination =  compareService.findAllCompareWithPaginationAndSorting(offset, pageSize, field);
        return compareWithPagination;
    }

    @GetMapping("/view-compare-details/{id}")
    public ResponseEntity<Compare> viewCompareDetails(@PathVariable int id){

        if(compareService.getCompareById(id) == null){
            return ResponseEntity.notFound().build();
        };
        return ResponseEntity.ok().body(compareService.getCompareById(id));
    }

    @GetMapping("/view-compare-rerun/{id}")
    public String rerunCompare(@PathVariable int id){
        Compare getCompare = compareService.compareRerun(id);

        InputStream inputStream = null;
        try {
            File File1 = new File(FILE_DIRECTORY+getCompare.getFilename1());
            //File file = new File(classLoader.getResource("fileTest.txt").getFile());
            //inputStream = new FileInputStream(file);
            FileOutputStream fileOutputStreams1 = new FileOutputStream(File1);

            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(File1));
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder1 = stringBuilder;
            String content1 = ""; String content2 ="";
            while ((content1=bufferedReader1.readLine()) != null) {
                content1 += bufferedReader1.readLine();
                stringBuilder1.append(content1);
                stringBuilder1.append('\n');
            }
            String file1Content = stringBuilder1.toString();
            System.out.println("File1:"+ file1Content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return "";
    }
}
