package com.linecards.compare.controller;

import com.linecards.compare.DTO.CompareRequestDto;
import com.linecards.compare.DTO.CompareResponseDto;
import com.linecards.compare.Entity.Compare;
import com.linecards.compare.service.CompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
@RequestMapping("student")
public class CompareController {

    @Autowired
    CompareRequestDto compareRequestDto;
    @Autowired
    CompareResponseDto compareResponseDto;
    @Autowired
    CompareService compareService;
    private String st;

    @Autowired
    Compare compare;

    //  public ResponseEntity<StudentResponseDto> uploadFile(@RequestBody StudentRequestDto filecontent){
    @Value("${file.upload-dir}")
    String FILE_DIRECTORY;

    @PostMapping("/compare")
    public String compare(@RequestParam String studentName1, @RequestParam("File1") MultipartFile file1, @RequestParam("File2") MultipartFile file2, @RequestParam String studentName2) throws Exception {

        File File1 = new File(FILE_DIRECTORY+file1.getOriginalFilename());

        File File2 = new File(FILE_DIRECTORY+file2.getOriginalFilename());

        FileOutputStream fileOutputStreams1 = new FileOutputStream(File1);
        FileOutputStream fileOutputStreams2 = new FileOutputStream(File2);
        fileOutputStreams1.write(file1.getBytes());
        fileOutputStreams2.write(file2.getBytes());

        BufferedReader bufferedReader1 = new BufferedReader(new FileReader(File1));
        BufferedReader bufferedReader2 = new BufferedReader(new FileReader(File2));
        StringBuilder stringBuilder1 = new StringBuilder();
        String content1 = ""; String content2 ="";
        while ((content1=bufferedReader1.readLine()) != null) {
            content1 += bufferedReader1.readLine();
            stringBuilder1.append(content1);
            stringBuilder1.append('\n');
        }

//      System.out.println(st);
//        System.out.println("File1:"+ sb1.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        while ((content2 = bufferedReader2.readLine()) != null) {
            content2 += bufferedReader2.readLine();
            stringBuilder2.append(content2);
            stringBuilder2.append('\n');
        }

        String file1Content = stringBuilder1.toString();
        String file2Content = stringBuilder2.toString();
        System.out.println("File1:"+ file1Content);
        System.out.println("File2:"+ file2Content);
        System.out.println(stringBuilder1.compareTo(stringBuilder2));

        int percentageSimilarity = lock_match(stringBuilder1.toString(),stringBuilder2.toString());
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

        System.out.println("Your Strings are Matched="+percentageSimilarity);
        return "Both file content are "+percentageSimilarity+" similar";
    }

    @GetMapping("/history")
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

//    @GetMapping("/history/paginationAndSort/{offset}/{pageSize}/{field}")
//    public Page<Compare> viewHistoryWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field){
//        Page<Compare> compareWithPagination =  compareService.findAllCompareWithPaginationAndSorting(offset, pageSize, field);
//        return compareWithPagination;
//    }

    @GetMapping("/view-compare-details/{id}")
    public Compare viewCompareDetails(@PathVariable int id){

        return compareService.getCompareById(id);
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

    public static int lock_match(String s, String t) {

        int totalw = word_count(s);
        int total = 100;
        int perw = total / totalw;
        int gotperw = 0;

        if (!s.equals(t)) {

            for (int i = 1; i <= totalw; i++) {
                if (simple_match(split_string(s, i), t) == 1) {
                    gotperw = ((perw * (total - 10)) / total) + gotperw;
                } else if (front_full_match(split_string(s, i), t) == 1) {
                    gotperw = ((perw * (total - 20)) / total) + gotperw;
                } else if (anywhere_match(split_string(s, i), t) == 1) {
                    gotperw = ((perw * (total - 30)) / total) + gotperw;
                } else {
                    gotperw = ((perw * smart_match(split_string(s, i), t)) / total) + gotperw;
                }
            }
        } else {
            gotperw = 100;
        }
        return gotperw;
    }



    public static int anywhere_match(String s, String t) {
        int x = 0;
        if (t.contains(s)) {
            x = 1;
        }
        return x;
    }

    public static int front_full_match(String s, String t) {
        int x = 0;
        String tempt;
        int len = s.length();

        //----------Work Body----------//
        for (int i = 1; i <= word_count(t); i++) {
            tempt = split_string(t, i);
            if (tempt.length() >= s.length()) {
                tempt = tempt.substring(0, len);
                if (s.contains(tempt)) {
                    x = 1;
                    break;
                }
            }
        }
        //---------END---------------//
        if (len == 0) {
            x = 0;
        }
        return x;
    }

    public static int simple_match(String s, String t) {
        int x = 0;
        String tempt;
        int len = s.length();


        //----------Work Body----------//
        for (int i = 1; i <= word_count(t); i++) {
            tempt = split_string(t, i);
            if (tempt.length() == s.length()) {
                if (s.contains(tempt)) {
                    x = 1;
                    break;
                }
            }
        }
        //---------END---------------//
        if (len == 0) {
            x = 0;
        }
        return x;
    }

    public static int smart_match(String ts, String tt) {

        char[] s = new char[ts.length()];
        s = ts.toCharArray();
        char[] t = new char[tt.length()];
        t = tt.toCharArray();


        int slen = s.length;
        //number of 3 combinations per word//
        int combs = (slen - 3) + 1;
        //percentage per combination of 3 characters//
        int ppc = 0;
        if (slen >= 3) {
            ppc = 100 / combs;
        }
        //initialising an integer to store the total % this class genrate//
        int x = 0;
        //declaring a temporary new source char array
        char[] ns = new char[3];
        //check if source char array has more then 3 characters//
        if (slen < 3) {
        } else {
            for (int i = 0; i < combs; i++) {
                for (int j = 0; j < 3; j++) {
                    ns[j] = s[j + i];
                }
                if (cross_full_match(ns, t) == 1) {
                    x = x + 1;
                }
            }
        }
        x = ppc * x;
        return x;
    }

    public static int  cross_full_match(char[] s, char[] t) {
        int z = t.length - s.length;
        int x = 0;
        if (s.length > t.length) {
            return x;
        } else {
            for (int i = 0; i <= z; i++) {
                for (int j = 0; j <= (s.length - 1); j++) {
                    if (s[j] == t[j + i]) {
                        // x=1 if any charecer matches
                        x = 1;
                    } else {
                        // if x=0 mean an character do not matches and loop break out
                        x = 0;
                        break;
                    }
                }
                if (x == 1) {
                    break;
                }
            }
        }
        return x;
    }

    public static String split_string(String s, int n) {

        int index;
        String temp;
        temp = s;
        String temp2 = null;

        int temp3 = 0;

        for (int i = 0; i < n; i++) {
            int strlen = temp.length();
            index = temp.indexOf(" ");
            if (index < 0) {
                index = strlen;
            }
            temp2 = temp.substring(temp3, index);
            temp = temp.substring(index, strlen);
            temp = temp.trim();

        }
        return temp2;
    }

    public static int word_count(String s) {
        int x = 1;
        int c;
        s = s.trim();
        if (s.isEmpty()) {
            x = 0;
        } else {
            if (s.contains(" ")) {
                for (;;) {
                    x++;
                    c = s.indexOf(" ");
                    s = s.substring(c);
                    s = s.trim();
                    if (s.contains(" ")) {
                    } else {
                        break;
                    }
                }
            }
        }
        return x;
    }
}
