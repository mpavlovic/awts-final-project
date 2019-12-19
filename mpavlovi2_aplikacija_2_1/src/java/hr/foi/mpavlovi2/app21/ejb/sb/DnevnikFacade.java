/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.mpavlovi2.app21.ejb.sb;

import hr.foi.mpavlovi2.app21.util.LogFilterCriteria;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Dnevnik;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Korisnik;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Milan
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> {
    @EJB
    private KorisnikFacade korisnikFacade;
    
    @PersistenceContext(unitName = "mpavlovi2_aplikacija_2_1PU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }
    
    public List<Dnevnik> filterLogs(LogFilterCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = builder.createQuery();
        Root<Dnevnik> root = criteriaQuery.from(Dnevnik.class);
        criteriaQuery.select(root);
        Predicate[] predicates = createWherePredicates(criteria, builder, root);
        criteriaQuery.where(predicates);
        TypedQuery<Dnevnik> typedQuery = em.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    private Predicate[] createWherePredicates(LogFilterCriteria criteria, CriteriaBuilder builder, Root<Dnevnik> root) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate predicate;
        if(null != (predicate = createTimeFromPredicate(criteria, builder, root))) {
            predicates.add(predicate);
        }
        if(null != (predicate = createTimeToPredicate(criteria, builder, root))) {
            predicates.add(predicate);
        }
        if(null != (predicate = createIPPredicate(criteria, builder, root))) {
            predicates.add(predicate);
        }
        if(null != (predicate = createUsernamePredicate(criteria, builder, root))) {
            predicates.add(predicate);
        }
        return predicates.toArray(new Predicate[] {});
    }

    private Predicate createTimeFromPredicate(LogFilterCriteria criteria, 
            CriteriaBuilder builder, Root<Dnevnik> root) {
        Predicate predicate = null;
        Date vrijemeOd = criteria.getVrijemeOd();
        if (null != vrijemeOd) {
            Expression<Date> vrijeme = root.get("vrijeme");
            predicate = builder.greaterThanOrEqualTo(vrijeme, vrijemeOd);
        }
        return predicate;
    }

    
    private Predicate createTimeToPredicate(LogFilterCriteria criteria, 
            CriteriaBuilder builder, Root<Dnevnik> root) {
        Predicate predicate = null;
        Date vrijemeDo = criteria.getVrijemeDo();
        Expression<Date> vrijeme = root.get("vrijeme");
        if (null != vrijemeDo) {
            predicate = builder.lessThanOrEqualTo(vrijeme, vrijemeDo);
        }
        return predicate;
    }

    private Predicate createIPPredicate(LogFilterCriteria criteria, 
            CriteriaBuilder builder, Root<Dnevnik> root) {
        Predicate predicate = null;
        String ipAdresa = criteria.getIpAdresa();
        Expression<String> uneseniIp = root.get("ipadresa");
        if(null != ipAdresa && !ipAdresa.isEmpty()) {
            predicate = builder.equal(uneseniIp, ipAdresa);
        }
        return predicate;
    }

    private Predicate createUsernamePredicate(LogFilterCriteria criteria, 
            CriteriaBuilder builder, Root<Dnevnik> root) {
        Predicate predicate = null;
        String username = criteria.getUsername();
        Korisnik uneseniKorisnik = korisnikFacade.findByUsername(username);
        if(null == uneseniKorisnik) {
            uneseniKorisnik = new Korisnik();
            uneseniKorisnik.setUsername(username);
            uneseniKorisnik.setId(-1);
        }
        Expression<Korisnik> user = root.get("korisnikId");
        if(null != username && !username.isEmpty()) {
            predicate = builder.equal(user, uneseniKorisnik);
        }
        return predicate;
    }
    
    
    
}
