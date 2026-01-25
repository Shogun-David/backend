package com.indra.reservations_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.indra.reservations_backend.models.UsuarioEntity;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

   @Query("SELECT u FROM UsuarioEntity u WHERE u.username = :username")
   Optional<UsuarioEntity> getByUsername(@Param("username") String username); 
}
