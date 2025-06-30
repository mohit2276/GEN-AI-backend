package com.genai.codeiumapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.Company;
import com.genai.codeiumapp.model.Notification;
import com.genai.codeiumapp.model.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long>{

	List<Notification> findByUser(User user);

	List<Notification> findByUserUserId(Long userId);

	List<Notification> findByCompany(Company company);


    List<Notification> findByCompanyCompanyId(Long userId);

	List<Notification> findAllByCompanyCompanyId(Long companyId);
}
