/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Wannes
 */
@Entity
@Table(name = "TBL_USER")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
})
public class User {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    private String name;
    
    private String password;
    
    private String email;
    
    @OneToMany
    private List<Rit> ritten;

    public User() {
    }
    
    

    public User(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public List<Rit> getRitten() {
        return ritten;
    }

    public void setRitten(List<Rit> ritten) {
        this.ritten = ritten;
    }
 
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void addRit(Rit rit){
        ritten.add(rit);
    }
    
    
}
