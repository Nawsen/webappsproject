
package resources;

import domain.Rit;
import domain.User;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 *
 * @author Wannes
 */
@RequestScoped
@Path("ritten")
@Transactional(dontRollbackOn = {BadRequestException.class, NotFoundException.class})
public class Ritten
{
    @PersistenceContext
    private EntityManager em;
    
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Rit getRit(@PathParam("id") Long id)
    {
        Rit rit= em.find(Rit.class, id);
        
        if (rit == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        return rit;
    }
    
    
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(@PathParam("id") Long id, InputStream input)
    {
        Rit rit = em.find(Rit.class, id);
        
        if (rit == null) {
            throw new NotFoundException("Gebruiker niet gevonden");
        }
        
        try (JsonReader jsonInput = Json.createReader(input)) {
            JsonObject jsonRit = jsonInput.readObject();
            
            String titel = jsonRit.getString("titel", null).trim();
            if(titel.length() >3){
                rit.setTitle(titel);
            }
            long afstand = jsonRit.getInt("afstand", 0);
            
            rit.setAfstand(afstand);
            
        } catch (JsonException | ClassCastException ex) {
            throw new BadRequestException("Ongeldige JSON invoer");
        }
        
    }
    
    @Path("{id}")
    @DELETE
    public void removeUser(@PathParam("id") Long id)
    {
        Rit rit = em.find(Rit.class, id);
        
        if (rit == null) {
            throw new NotFoundException("rit niet gevonden");
        }
        
        em.remove(rit);
    }
}
