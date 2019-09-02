package dz.saticom.syvcom.dashboard.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
  public class CustomNativeRepositoryImpl implements CustomNativeRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Object> runNativeQueryList(String query) {
        return entityManager.createNativeQuery(query).getResultList();
    }
    
    @Override
    public Object runNativeQueryOne(String query) {
        return entityManager.createNativeQuery(query).getSingleResult();
         
    }
    
    @Override
    public Object runNativeQueryFirst(String query) {
        return entityManager.createNativeQuery(query).getFirstResult();
         
    }
}