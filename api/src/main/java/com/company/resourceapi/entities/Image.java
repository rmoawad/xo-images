package com.company.resourceapi.entities;


import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "type", nullable = false)
  @NotBlank
  private String type;

  @Column(name = "s3_name")
  @Size(max = 255, message = "Name on s3 must be max 255 characters long")
  private String s3Name;
  
  @Column(name = "size")
  private long size;
  
  @Column(name = "description")
  private String description;

  @Column(name = "created_date", nullable = false)
  @CreatedDate
  private Instant createdDate;

  @Column(name = "last_modified_date", nullable = false)
  @LastModifiedDate
  private Instant lastModifiedDate;

  @PrePersist
  private void onInsert() {
    this.createdDate = Instant.now();
    onUpdate();
  }

  @PreUpdate
  private void onUpdate() {
    this.lastModifiedDate = Instant.now();
  }
}
