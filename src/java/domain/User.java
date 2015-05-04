/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import validation.OnPasswordUpdate;
import validation.ValidPassword;

/**
 *
 * @author Wannes
 */
@Entity
@Table(name = "TBL_USER")
@SecondaryTable(name = "USER_PASSWORD") // We gaan de paswoorden opslaan in een aparte tabel, dit is een goede gewoonte.
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
})
public class User {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @NotNull
    @Length(min = 5, max = 255)
    private String name;
    
    @Transient // Het plain text paswoord mag nooit opgeslagen worden.
    @ValidPassword(groups = OnPasswordUpdate.class) // En het moet enkel worden gevalideerd wanneer het is gewijzigd.
    private String plainPassword;
    
    @NotNull // Dit zou nooit mogen gebeuren.
    @Pattern(regexp = "[A-Fa-f0-9]{64}+") // Dit zou eveneens nooit mogen gebeuren (wijst op fout bij encryptie).
    @Column(name = "PASSWORD", table = "USER_PASSWORD")
    private String password;
    
    @NotNull
    @Length(min = 7, max = 255)
    private String email;
    
    @OneToMany(mappedBy="user")
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

    public void setPassword(String plainPassword)
    {
        this.plainPassword = plainPassword != null ? plainPassword.trim() : "";
        
        // De onderstaande code zal het paswoord hashen met SHA-256 en de hexadecimale hashcode opslaan.
        try {
            BigInteger hash = new BigInteger(1, MessageDigest.getInstance("SHA-256")
                    .digest(this.plainPassword.getBytes("UTF-8")));
            password = hash.toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    @ElementCollection
    // We kiezen hier zelf de naam voor de tabel en de kolommen omdat we deze nodig hebben voor het
    // instellen van de security realm.
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USERNAME"))
    @Column(name = "ROLES")
    private final List<String> roles = new ArrayList<>();

    public List<String> getRoles()
    {
        return roles;
    }


    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString()
    {
        return name;
    }
    
}
