/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.mpavlovi2.app21.ejb.sb;

import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Korisnik;
import java.util.List;
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
public class KorisnikFacade extends AbstractFacade<Korisnik> {
    @PersistenceContext(unitName = "mpavlovi2_aplikacija_2_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KorisnikFacade() {
        super(Korisnik.class);
    }
    
    public Korisnik findByCredentials(String username, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Korisnik> korisnikRoot = criteriaQuery.from(Korisnik.class);
        Expression<String> usernameCol = korisnikRoot.get("username");
        Expression<String> passwordCol = korisnikRoot.get("password");
        Predicate[] predicates = new Predicate[2];
        predicates[0] = cb.equal(usernameCol, username);
        predicates[1] = cb.equal(passwordCol, password);
        criteriaQuery.where(predicates);
        TypedQuery typedQuery = em.createQuery(criteriaQuery);
        List<Korisnik> userList = typedQuery.getResultList();
        if(userList.size() != 1) {
            return null;
        }
        return userList.get(0);
    }
    
    public Korisnik findByUsername(String username) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Korisnik> korisnikRoot = criteriaQuery.from(Korisnik.class);
        Expression<String> usernameCol = korisnikRoot.get("username");
        criteriaQuery.where(cb.equal(usernameCol, username));
        TypedQuery typedQuery = em.createQuery(criteriaQuery);
        List<Korisnik> userList = typedQuery.getResultList();
        if(userList.size() != 1) {
            return null;
        }
        return userList.get(0);
    }
    
}
