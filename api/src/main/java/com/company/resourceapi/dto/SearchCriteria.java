package com.company.resourceapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchCriteria {

  public static final int DEFAULT_PAGE = 1;
  public static final int DEFAULT_SIZE_PER_PAGE = 20;

  private String description;

  
  private int page;

  private String type;

  private int size;
}
