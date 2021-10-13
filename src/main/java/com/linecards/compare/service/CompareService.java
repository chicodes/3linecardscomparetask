package com.linecards.compare.service;

import com.linecards.compare.Entity.Compare;
import com.linecards.compare.Repository.CompareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompareService {

    @Autowired
    CompareRepository repository;

    public Compare saveCompare(Compare compareDetails){
        return repository.save(compareDetails);
    }

    public List<Compare> findAllCompare(){
        return repository.findAll();
    }

    public List<Compare> findAllCompareWithSorting(String field){

        return repository.findAll(Sort.by(field));
    }

    public Page<Compare> findAllCompareWithPagination(int offset, int pageSize){
        Page<Compare> compare =  repository.findAll(PageRequest.of(offset,pageSize));
        return compare;
    }

//    public Page<Compare> findAllCompareWithPaginationAndSorting(int offset, int pageSize, String field){
//        Page<Compare> compare =  repository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(field)));
//        return compare;
//    }

    public Compare getCompareById(int id){

        return repository.findById(id).orElse(null);
    }

    public Compare compareRerun(int id){
        Compare getCompare = repository.findById(id).orElse(null);
//        String fileContent1 = getCompare.getFileContent1();
//        String fileContent2 = getCompare.getFileContent2();
//        int compareResult =  fileContent1.compareTo(fileContent2);
        return getCompare;
    }
}
