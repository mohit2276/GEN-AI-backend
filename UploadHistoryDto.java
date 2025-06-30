package com.genai.codeiumapp.dto;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UploadHistoryDto {
    private String uploadDate;
    private String time;
    private String status;
    private String uploader;
    private String comments;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<UploadHistoryFileDto> filesList;
    
}