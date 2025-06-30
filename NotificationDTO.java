package com.genai.codeiumapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {

	private Long id;
    private String challengeName;
    private String launchDate;
    private String launchTime;
    private String name;
    private String userEmpId;
    private String registrationDate;
    private String registrationTime;
    private String enrollDate;
    private String enrollTime;
    private Boolean isRead;
    private Long userId;
}
