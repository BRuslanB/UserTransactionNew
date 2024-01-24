package lab.solva.user.transaction.repository;

import lab.solva.user.transaction.model.AmountLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AmountLimitRepository extends JpaRepository<AmountLimitEntity, Long>  {

    List<AmountLimitEntity> findAllByOrderByLimitDateTimeDesc();
}
