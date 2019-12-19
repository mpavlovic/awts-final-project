/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app21.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Milan
 */
@Entity
@Table(name = "DNEVNIK")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d"),
    @NamedQuery(name = "Dnevnik.findById", query = "SELECT d FROM Dnevnik d WHERE d.id = :id"),
    @NamedQuery(name = "Dnevnik.findByUrl", query = "SELECT d FROM Dnevnik d WHERE d.url = :url"),
    @NamedQuery(name = "Dnevnik.findByIpadresa", query = "SELECT d FROM Dnevnik d WHERE d.ipadresa = :ipadresa"),
    @NamedQuery(name = "Dnevnik.findByVrijeme", query = "SELECT d FROM Dnevnik d WHERE d.vrijeme = :vrijeme"),
    @NamedQuery(name = "Dnevnik.findByTrajanje", query = "SELECT d FROM Dnevnik d WHERE d.trajanje = :trajanje"),
    @NamedQuery(name = "Dnevnik.findByStatus", query = "SELECT d FROM Dnevnik d WHERE d.status = :status")})
public class Dnevnik implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 255)
    @Column(name = "URL")
    private String url;
    @Size(max = 255)
    @Column(name = "IPADRESA")
    private String ipadresa;
    @Column(name = "VRIJEME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijeme;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TRAJANJE")
    private int trajanje;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS")
    private int status;
    @JoinColumn(name = "KORISNIK_ID", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Korisnik korisnikId;

    public Dnevnik() {
    }

    public Dnevnik(Integer id) {
        this.id = id;
    }

    public Dnevnik(Integer id, int trajanje, int status) {
        this.id = id;
        this.trajanje = trajanje;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpadresa() {
        return ipadresa;
    }

    public void setIpadresa(String ipadresa) {
        this.ipadresa = ipadresa;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Korisnik getKorisnikId() {
        return korisnikId;
    }

    public void setKorisnikId(Korisnik korisnikId) {
        this.korisnikId = korisnikId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dnevnik)) {
            return false;
        }
        Dnevnik other = (Dnevnik) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Dnevnik[ id=" + id + " ]";
    }
    
}
