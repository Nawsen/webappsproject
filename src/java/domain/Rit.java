/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Wannes
 */
@Entity
@Table(name = "TBL_RIT")
@NamedQueries({
    @NamedQuery(name = "RIT.find", query = "SELECT r FROM Rit r WHERE r.id = :id")
})
public class Rit {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    private String title;
    
    private long afstand;
    
    @ManyToOne
    private User user;

    public Rit() {
    }

    public Rit(String title, long afstand) {
        this.title = title;
        this.afstand = afstand;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAfstand() {
        return afstand;
    }

    public void setAfstand(long afstand) {
        this.afstand = afstand;
    }
    
    
    
}
