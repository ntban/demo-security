package hello.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hello.entity.Viewer;

@Repository
public interface ViewerRepository extends JpaRepository<Viewer, Integer> {
	@Transactional
	@Modifying
	@Query("DELETE FROM Viewer v WHERE v.name = ?1")
	public void deleteViewer(String name);
	
	@Query("SELECT v FROM Viewer v WHERE v.name = ?1")
	public List<Viewer> findByName(String name);
}
