package hello.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hello.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer>  {

	@Query(nativeQuery=true, value="SELECT * FROM Card c WHERE c.used='unused' ORDER BY id LIMIT 6")
	public List<Card> getFirstSix();
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true, value="UPDATE card SET used ='unused' where id>=0 ")
	public void resetCard();
}