package com.Assignement.Assignemnt.Repository;

import com.Assignement.Assignemnt.Entity_Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
