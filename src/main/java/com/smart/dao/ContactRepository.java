package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends CrudRepository<Contact, Integer> {
	
	//pegination
	@Query("from Contact as d where d.user.id=:userId")
	//currentPage-page
	//contact per page-5
	public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable  pePageable);
	
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
}
