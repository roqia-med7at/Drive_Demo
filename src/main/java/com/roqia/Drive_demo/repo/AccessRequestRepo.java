package com.roqia.Drive_demo.repo;

import com.roqia.Drive_demo.model.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRequestRepo extends JpaRepository<AccessRequest,Integer> {
}
